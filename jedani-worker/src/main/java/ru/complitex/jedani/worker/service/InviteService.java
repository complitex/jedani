package ru.complitex.jedani.worker.service;

import com.google.common.io.BaseEncoding;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.domain.service.DomainService;
import ru.complitex.jedani.worker.entity.Setting;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.UUID;

/**
 * @author Anatoly Ivanov
 * 01.11.2020 19:06
 */

public class InviteService {
    private final Logger log = LoggerFactory.getLogger(InviteService.class);

    @Inject
    private DomainService domainService;

    public String getInviteSecret(){
        Setting inviteKeySetting = domainService.getDomain(Setting.class, Setting.INVITE_SECRET);

        if (inviteKeySetting == null){
            inviteKeySetting = new Setting();

            inviteKeySetting.setObjectId(Setting.INVITE_SECRET);
            inviteKeySetting.setValue(UUID.randomUUID().toString());

            domainService.insert(inviteKeySetting);
        }else if (inviteKeySetting.getValue() == null){
            inviteKeySetting.setValue(UUID.randomUUID().toString());

            domainService.update(inviteKeySetting);
        }

        return inviteKeySetting.getValue().substring(0, 32);
    }

    public String encodeKey(String jId){
        try {
            if (jId == null){
                return null;
            }

            Cipher cipher = Cipher.getInstance("AES");

            Key secret = new SecretKeySpec(StringUtils.rightPad(getInviteSecret(), 32, "jedani").getBytes(StandardCharsets.US_ASCII), "AES");

            cipher.init(Cipher.ENCRYPT_MODE, secret);

            byte[] b = cipher.doFinal((jId).getBytes(StandardCharsets.US_ASCII));

            return BaseEncoding.base64Url().omitPadding().encode(b);
        } catch (Exception e) {
            log.error("error decodeJId ", e);
        }

        return null;
    }

    public String decodeJId(String key){
        try {
            Cipher cipher = Cipher.getInstance("AES");

            Key secret = new SecretKeySpec(StringUtils.rightPad(getInviteSecret(), 32, "jedani").getBytes(StandardCharsets.US_ASCII), "AES");

            cipher.init(Cipher.DECRYPT_MODE, secret);

            byte[] t = BaseEncoding.base64Url().decode(key);

            return new String(cipher.doFinal(t), StandardCharsets.US_ASCII);
        } catch (Exception e) {
            log.error("error decodeJId ", e);
        }

        return null;
    }
}

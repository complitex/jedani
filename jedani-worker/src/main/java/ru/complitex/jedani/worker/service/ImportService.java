package ru.complitex.jedani.worker.service;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.address.entity.City;
import ru.complitex.address.entity.Region;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.domain.util.Locales;
import ru.complitex.jedani.worker.entity.Worker;
import ru.complitex.name.entity.FirstName;
import ru.complitex.name.entity.LastName;
import ru.complitex.name.entity.MiddleName;
import ru.complitex.user.entity.User;
import ru.complitex.user.mapper.UserMapper;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

import static ru.complitex.domain.entity.Status.ACTIVE;
import static ru.complitex.domain.entity.Status.SYNC;

/**
 * @author Anatoly A. Ivanov
 * 22.11.2017 16:49
 */
public class ImportService implements Serializable {
    private Logger log = LoggerFactory.getLogger(ImportService.class);

    @Inject
    private DomainMapper domainMapper;

    @Inject
    private UserMapper userMapper;

    @Inject
    private Principal principal;

    public static class Status{
        private int count = 0;
        private String errorMessage;

        public Integer getCount() {
            return count;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public Status importRegions(InputStream inputStream){
        User currentUser = userMapper.getUser(principal.getName());

        Status status = new Status();
        List<Region> regions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] columns = line.split(";", -1);

                if (columns.length != 3){
                    status.errorMessage = "Неверное количество колонок в файле: 3 <> " + columns.length;

                    break;
                }

                Region region = new Region();
                region.setUpperTextValue(Region.NAME, columns[1]);

                region.setText(Region.IMPORT_ID, columns[0]);
                region.setText(Region.IMPORT_MANAGER_ID, columns[2]);

                regions.add(region);
            }

            regions.stream().filter(r -> !domainMapper.hasDomain(Region.ENTITY_NAME, Region.IMPORT_ID, r.getText(Region.IMPORT_ID)))
                    .forEach(r -> {
                        r.setUserId(currentUser.getId());
                        domainMapper.insertDomain(r);
                        ++status.count;
                    });
        } catch (Exception e) {
            log.error("error import regions", e);

            status.errorMessage = "" + e.getMessage();
        }

        return status;
    }

    public Status importCities(InputStream inputStream){
        User currentUser = userMapper.getUser(principal.getName());

        Status status = new Status();
        List<City> cities = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))){
            String line;

            while ((line = br.readLine()) != null){
                String[] columns = line.split(";", -1);

                if (columns.length != 4){
                    status.errorMessage = "Неверное количество колонок в файле: 4 <> " + columns.length;

                    break;
                }

                Domain region = domainMapper.getDomain("region", Region.IMPORT_ID, columns[2]);

                if (region == null){
                    status.errorMessage = "Ненайден район " + columns[2] + " для " + columns[1];

                    break;
                }

                City city = new City();
                city.setUpperTextValue(City.NAME, columns[1]);
                city.setText(City.IMPORT_ID, columns[0]);
                city.setParentEntityId(Region.ENTITY_ID);
                city.setParentId(region.getObjectId());
                city.setText(City.IMPORT_MANAGER_ID, columns[3]);

                cities.add(city);
            }

            cities.stream().filter(c -> !domainMapper.hasDomain(City.ENTITY_NAME, City.IMPORT_ID, c.getText(City.IMPORT_ID)))
                    .forEach(r -> {
                        r.setUserId(currentUser.getId());

                        domainMapper.insertDomain(r);
                        ++status.count;
                    });
        } catch (Exception e) {
            log.error("error import cities", e);

            status.errorMessage = "Ошибка: " + e.getMessage();
        }

        return status;
    }

    public Status importWorkers(InputStream inputStream, Consumer<String> listener){
        Status status = new Status();

        List<Worker> workers = new ArrayList<>();
        Map<String, User> userMap = new HashMap<>();

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))){
            String line;

            int row = 0;

            while ((line = br.readLine()) != null){
                row++;

                if (line.trim().isEmpty()){
                    continue;
                }

                String[] columns = line.split(";", -1);

                if (columns.length != 26){
                    status.errorMessage = "Неверное количество колонок в файле: 26 <> " + columns.length + " в строке " + row;

                    log.error(status.errorMessage);

                    return status;
                }

                User user = new User();

                user.setLogin(columns[4]);

                if (!columns[2].isEmpty()) {
                    user.setPassword(columns[2]);
                }else {
                    user.setPassword(UUID.randomUUID().toString());
                }

                userMap.put(columns[0], user);

                Worker worker = new Worker();

                worker.setText(Worker.IMPORT_ID, columns[0]);
                worker.setUpperText(Worker.J_ID, columns[4]);
                worker.setUpperText(Worker.EMAIL, columns[1]);
                worker.setText(Worker.IMPORT_ANCESTRY, columns[3]);
                worker.setText(Worker.RESET_PASSWORD_TOKEN, columns[5]);
                worker.setText(Worker.RESET_PASSWORD_SEND_AT, columns[6]);
                worker.setText(Worker.REMEMBER_CREATED_AT, columns[7]);

                if (!Strings.isNullOrEmpty(columns[8])) {
                    worker.setDate(Worker.CREATED_AT, dateTimeFormat.parse(columns[8]));
                }
                if (!Strings.isNullOrEmpty(columns[9])) {
                    worker.setDate(Worker.UPDATED_AT, dateTimeFormat.parse(columns[9]));
                }

                if (!Strings.isNullOrEmpty(columns[10])) {
                    worker.setNumber(Worker.MK_STATUS, Long.parseLong(columns[10]) + 1);
                }

                worker.setUpperText(Worker.FIRST_NAME, StringUtils.trim(columns[11]));
                worker.setUpperText(Worker.MIDDLE_NAME, StringUtils.trim(columns[12]));
                worker.setUpperText(Worker.LAST_NAME, StringUtils.trim(columns[13]));
                worker.addUpperTextValue(Worker.PHONE, columns[14]);

                if (!columns[15].trim().isEmpty()) {
                    Domain city = domainMapper.getDomain("city", City.IMPORT_ID, columns[15]);
                    worker.addNumberValue(Worker.CITIES, city.getObjectId());
                    worker.addNumberValue(Worker.REGIONS, city.getParentId());
                }

                worker.setText(Worker.IMPORT_MANAGER_RANK_ID, columns[16]);

                if (!Strings.isNullOrEmpty(columns[17])) {
                    worker.setDate(Worker.INVOLVED_AT, dateTimeFormat.parse(columns[17]));
                }

                worker.setText(Worker.CONTACT_INFO, columns[21]);

                if (!Strings.isNullOrEmpty(columns[22])) {
                    worker.setDate(Worker.BIRTHDAY, dayFormat.parse(columns[22]));
                }

                worker.setText(Worker.FIRED_STATUS, columns[23]);

                worker.setText(Worker.IMPORT_OLD_PARENT_ID, columns[24]);
                worker.setText(Worker.IMPORT_OLD_CHILD_ID, columns[25]);

                workers.add(worker);

                worker.setStatus(SYNC);
            }

            for (Worker w : workers) {
                if (!domainMapper.hasDomain(Worker.ENTITY_NAME, Worker.IMPORT_ID, w.getText(Worker.IMPORT_ID))) {
                    insertUserProfile(userMap.get(w.getText(Worker.IMPORT_ID)), w);
                    ++status.count;

                    listener.accept(status.count * 100 / workers.size() + "%");
                }
            }

            updateWorkerManagerId(listener);
        }catch (Exception e){
            log.error("error import users", e);

            status.errorMessage = "error import users: " + e.getMessage();
        }

        return status;
    }

    private void updateWorkerManagerId(Consumer<String> listener){
        domainMapper.getDomains(FilterWrapper.of(new Domain(Worker.ENTITY_NAME).setStatus(SYNC))).forEach(w -> {
            String importAncestry = w.getText(Worker.IMPORT_ANCESTRY);

            if (importAncestry != null) {
                String importManagerId = importAncestry.substring(importAncestry.lastIndexOf('/') + 1);

                Domain manager = domainMapper.getDomain(Worker.ENTITY_NAME, Worker.IMPORT_ID, importManagerId);

                w.setNumber(Worker.MANAGER_ID, manager.getObjectId());

                listener.accept(importAncestry);
            }else{
                w.setNumber(Worker.MANAGER_ID, 1L);
            }

            w.setStatus(ACTIVE);

            domainMapper.updateDomain(w);
        });
    }

    private void insertUserProfile(User user, Worker worker){
        User currentUser = userMapper.getUser(principal.getName());

        if (!user.getLogin().isEmpty()) {
            userMapper.insert(user);

            worker.setParentId(user.getId());
            worker.setParentEntityId(User.ENTITY_ID);
        }

        {
            FirstName firstName = new FirstName();
            firstName.getOrCreateAttribute(FirstName.NAME).getOrCreateValue(Locales.getSystemLocaleId())
                    .setText(worker.getText(Worker.FIRST_NAME));
            Long firstNameId = domainMapper.getDomainObjectId(firstName);
            if (firstNameId == null) {
                firstName.setUserId(currentUser.getId());
                domainMapper.insertDomain(firstName);
                firstNameId = firstName.getObjectId();
            }
            worker.setNumber(Worker.FIRST_NAME, firstNameId);
        }

        {
            MiddleName middleName = new MiddleName();
            middleName.getOrCreateAttribute(MiddleName.NAME).getOrCreateValue(Locales.getSystemLocaleId())
                    .setText(worker.getText(Worker.MIDDLE_NAME));
            Long middleNameId = domainMapper.getDomainObjectId(middleName);
            if (middleNameId == null) {
                middleName.setUserId(currentUser.getId());

                domainMapper.insertDomain(middleName);
                middleNameId = middleName.getObjectId();
            }
            worker.setNumber(Worker.MIDDLE_NAME, middleNameId);
        }

        {
            LastName lastName = new LastName();
            lastName.getOrCreateAttribute(LastName.NAME).getOrCreateValue(Locales.getSystemLocaleId())
                    .setText(worker.getText(Worker.LAST_NAME));
            Long lastNameId = domainMapper.getDomainObjectId(lastName);
            if (lastNameId == null) {
                lastName.setUserId(currentUser.getId());

                domainMapper.insertDomain(lastName);
                lastNameId = lastName.getObjectId();
            }
            worker.setNumber(Worker.LAST_NAME, lastNameId);

            worker.setUserId(currentUser.getId());

            domainMapper.insertDomain(worker);
        }
    }
}

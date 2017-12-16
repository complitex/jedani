package ru.complitex.jedani.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.address.entity.City;
import ru.complitex.address.entity.Region;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.jedani.entity.User;
import ru.complitex.jedani.mapper.UserMapper;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 22.11.2017 16:49
 */
public class ImportService {
    private Logger log = LoggerFactory.getLogger(ImportService.class);

    @Inject
    private DomainMapper domainMapper;

    @Inject
    private UserMapper userMapper;

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
        Status status = new Status();
        List<Region> regions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] columns = line.split(";", -1);

                if (columns.length != 3){
                    status.errorMessage = "Неверное количество колонок в файле: 3 <> " + columns.length;

                    break;
                }

                Region region = new Region();
                region.setExternalId(columns[0]);
                region.setText(Region.NAME, columns[1]);
                if (columns[2] != null) {
                    region.setNumber(Region.MANAGER_ID, Long.parseLong(columns[2]));
                }

                regions.add(region);
            }

            regions.stream().filter(r -> !domainMapper.hasExternalId(r))
                    .forEach(r -> {
                        domainMapper.save(r);
                        ++status.count;
                    });
        } catch (Exception e) {
            log.error("error import regions", e);

            status.errorMessage = "" + e.getMessage();
        }

        return status;
    }

    public Status importCities(InputStream inputStream){
        Status status = new Status();
        List<City> cities = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))){
            String line;

            while ((line = br.readLine()) != null){
                String[] columns = line.split(";", -1);

                if (columns.length != 4){
                    status.errorMessage = "Неверное количество колонок в файле: 4 <> " + columns.length;

                    break;
                }

                Domain region = domainMapper.getDomain(new Region(columns[2]));

                if (region == null){
                    status.errorMessage = "Ненайден район " + columns[2] + " для " + columns[1];

                    break;
                }

                City city = new City();
                city.setExternalId(columns[0]);
                city.setText(City.NAME, columns[1]);
                city.setParentEntityId(Region.ENTITY_ID);
                city.setParentId(region.getObjectId());
                if (!columns[3].isEmpty()) {
                    city.setNumber(City.MANAGER_ID, Long.parseLong(columns[3]));
                }

                cities.add(city);
            }

            cities.stream().filter(c -> !domainMapper.hasExternalId(c))
                    .forEach(r -> {
                        domainMapper.save(r);
                        ++status.count;
                    });
        } catch (Exception e) {
            log.error("error import cities", e);

            status.errorMessage = e.getMessage();
        }

        return status;
    }

    public Status importUsers(InputStream inputStream){
        Status status = new Status();
        List<User> users = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))){
            String line;

            while ((line = br.readLine()) != null){
                String[] columns = line.split(";", -1);

                if (columns.length != 26){
                    status.errorMessage = "Неверное количество колонок в файле: 26 <> " + columns.length;

                    break;
                }

                User user = new User();
                user.setId(Integer.valueOf(columns[0]));
                user.setEmail(columns[1]);
                user.setEncryptedPassword(columns[2]);
                user.setAncestry(columns[3]);
                user.setJId(columns[4]);
                user.setResetPasswordToken(columns[5]);
                if (!columns[6].isEmpty()) {
                    user.setResetPasswordSendAt(dateFormat.parse(columns[6]));
                }
                if (!columns[7].isEmpty()) {
                    user.setRememberCreatedAt(dateFormat.parse(columns[7]));
                }
                if (!columns[8].isEmpty()) {
                    user.setCreatedAt(dateFormat.parse(columns[8]));
                }
                if (!columns[9].isEmpty()) {
                    user.setUpdatedAt(dateFormat.parse(columns[9]));
                }
                if (!columns[10].isEmpty()) {
                    user.setMkStatus(Integer.valueOf(columns[10]));
                }
                user.setFirstName(columns[11]);
                user.setSecondName(columns[12]);
                user.setLastName(columns[13]);
                user.setPhone(columns[14]);
                if (!columns[25].isEmpty()) {
                    user.setCityId(Integer.valueOf(columns[15]));
                }
                if (!columns[16].isEmpty()) {
                    user.setManagerRankId(Integer.valueOf(columns[16]));
                }
                if (!columns[17].isEmpty()) {
                    user.setInvolvedAt(dateFormat.parse(columns[17]));
                }
                user.setFullAncestryPath(columns[18]);
                user.setDepthLevel(columns[19]);
                user.setAncestryDepth(Integer.valueOf(columns[20]));
                user.setContactInfo(columns[21]);
                if (!columns[22].isEmpty()) {
                    user.setBirthday(dayFormat.parse(columns[22]));
                }
                user.setFiredStatus(Boolean.valueOf(columns[23]));
                if (!columns[24].isEmpty()) {
                    user.setOldParentId(Integer.valueOf(columns[24]));
                }
                user.setOldChildId(columns[25]);

                users.add(user);
            }

            users.stream()
                    .filter(r -> !userMapper.hasUser(r.getId()))
                    .forEach(r -> {
                        userMapper.insertUser(r);
                        ++status.count;
                    });
        }catch (Exception e){
            log.error("error import users", e);

            status.errorMessage = e.getMessage();
        }

        return status;
    }
}

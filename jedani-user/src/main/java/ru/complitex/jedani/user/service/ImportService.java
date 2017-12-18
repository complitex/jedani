package ru.complitex.jedani.user.service;

import org.mybatis.cdi.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.address.entity.City;
import ru.complitex.address.entity.Region;
import ru.complitex.domain.entity.Domain;
import ru.complitex.domain.mapper.DomainMapper;
import ru.complitex.jedani.user.entity.Profile;
import ru.complitex.user.entity.User;
import ru.complitex.user.mapper.UserMapper;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
                region.setValue(Region.NAME, columns[1]);
                if (columns[2] != null) {
                    region.setNumber(Region.MANAGER_ID, Long.parseLong(columns[2]));
                }

                regions.add(region);
            }

            regions.stream().filter(r -> !domainMapper.hasDomain(r))
                    .forEach(r -> {
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
                city.setValue(City.NAME, columns[1]);
                city.setParentEntityId(Region.ENTITY_ID);
                city.setParentId(region.getObjectId());
                if (!columns[3].isEmpty()) {
                    city.setNumber(City.MANAGER_ID, Long.parseLong(columns[3]));
                }

                cities.add(city);
            }

            cities.stream().filter(c -> !domainMapper.hasDomain(c))
                    .forEach(r -> {
                        domainMapper.insertDomain(r);
                        ++status.count;
                    });
        } catch (Exception e) {
            log.error("error import cities", e);

            status.errorMessage = e.getMessage();
        }

        return status;
    }

    public Status importUsers(InputStream inputStream, Consumer<Double> listener){
        Status status = new Status();

        List<Profile> profiles = new ArrayList<>();
        Map<String, User> userMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))){
            String line;

            while ((line = br.readLine()) != null){
                String[] columns = line.split(";", -1);

                if (columns.length != 26){
                    status.errorMessage = "Неверное количество колонок в файле: 26 <> " + columns.length;

                    break;
                }

                User user = new User();

                user.setLogin(columns[4]);
                user.setPassword(columns[2]);
                userMap.put(columns[0], user);

                Profile profile = new Profile();

                profile.setExternalId(columns[0]);
                profile.setText(Profile.EMAIL, columns[1]);
                profile.setText(Profile.ANCESTRY, columns[3]);
                profile.setText(Profile.J_ID, columns[4]);
                profile.setText(Profile.RESET_PASSWORD_TOKEN, columns[5]);
                profile.setText(Profile.RESET_PASSWORD_SEND_AT, columns[6]);
                profile.setText(Profile.REMEMBER_CREATED_AT, columns[7]);
                profile.setText(Profile.CREATED_AT, columns[8]);
                profile.setText(Profile.UPDATED_AT, columns[9]);
                profile.setText(Profile.MK_STATUS, columns[10]);
                profile.setText(Profile.FIRST_NAME, columns[11]);
                profile.setText(Profile.SECOND_NAME, columns[12]);
                profile.setText(Profile.LAST_NAME, columns[13]);
                profile.setText(Profile.PHONE, columns[14]);
                profile.setNumber(Profile.CITY_ID, columns[15]);
                profile.setNumber(Profile.MANAGER_RANK_ID, columns[16]);
                profile.setText(Profile.INVOLVED_AT, columns[17]);
                profile.setText(Profile.FULL_ANCESTRY_PATH, columns[18]);
                profile.setText(Profile.DEPTH_LEVEL, columns[19]);
                profile.setNumber(Profile.ANCESTRY_DEPTH, columns[20]);
                profile.setText(Profile.CONTACT_INFO, columns[21]);
                profile.setText(Profile.BIRTHDAY, columns[22]);
                profile.setText(Profile.FIRED_STATUS, columns[23]);
                profile.setNumber(Profile.OLD_PARENT_ID, columns[24]);
                profile.setText(Profile.OLD_CHILD_ID, columns[25]);

                profiles.add(profile);
            }

            profiles.stream()
                    .filter(p -> !domainMapper.hasDomain(p))
                    .forEach(p -> {
                        insertUserProfile(userMap.get(p.getExternalId()), p);
                        ++status.count;

                        listener.accept((double)status.count/profiles.size());
                    });
        }catch (Exception e){
            log.error("error import users", e);

            status.errorMessage = e.getMessage();
        }

        return status;
    }

    @Transactional
    private void insertUserProfile(User user, Profile profile){
        if (!user.getLogin().isEmpty() && !user.getPassword().isEmpty()) {
            userMapper.insertUser(user);

            profile.setParentId(user.getId());
            profile.setParentEntityId(User.ENTITY_ID);
        }

        domainMapper.insertDomain(profile);
    }
}

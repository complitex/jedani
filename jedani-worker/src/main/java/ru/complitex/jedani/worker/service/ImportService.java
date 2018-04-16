package ru.complitex.jedani.worker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mybatis.cdi.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.address.entity.City;
import ru.complitex.address.entity.Region;
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

    public Status importWorkers(InputStream inputStream, Consumer<Double> listener){
        Status status = new Status();

        List<Worker> workers = new ArrayList<>();
        Map<String, User> userMap = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();

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

                Worker worker = new Worker();

                worker.setExternalId(columns[0]);
                worker.setText(Worker.J_ID, columns[4]);
                worker.setText(Worker.EMAIL, columns[1]);
                worker.setText(Worker.ANCESTRY, columns[3]);
                worker.setText(Worker.RESET_PASSWORD_TOKEN, columns[5]);
                worker.setText(Worker.RESET_PASSWORD_SEND_AT, columns[6]);
                worker.setText(Worker.REMEMBER_CREATED_AT, columns[7]);
                worker.setText(Worker.CREATED_AT, columns[8]);
                worker.setText(Worker.UPDATED_AT, columns[9]);
                worker.setText(Worker.MK_STATUS, columns[10]);
                worker.setText(Worker.FIRST_NAME, columns[11]);
                worker.setText(Worker.SECOND_NAME, columns[12]);
                worker.setText(Worker.LAST_NAME, columns[13]);
                worker.setJson(Worker.PHONE, objectMapper.createArrayNode().add(columns[14]).toString());
                worker.setNumber(Worker.CITY_ID, columns[15]);
                worker.setNumber(Worker.MANAGER_RANK_ID, columns[16]);
                worker.setText(Worker.INVOLVED_AT, columns[17]);
                worker.setText(Worker.FULL_ANCESTRY_PATH, columns[18]);
                worker.setText(Worker.DEPTH_LEVEL, columns[19]);
                worker.setNumber(Worker.ANCESTRY_DEPTH, columns[20]);
                worker.setText(Worker.CONTACT_INFO, columns[21]);
                worker.setText(Worker.BIRTHDAY, columns[22]);
                worker.setText(Worker.FIRED_STATUS, columns[23]);
                worker.setNumber(Worker.OLD_PARENT_ID, columns[24]);
                worker.setText(Worker.OLD_CHILD_ID, columns[25]);

                workers.add(worker);
            }

            workers.stream()
                    .filter(p -> !domainMapper.hasDomain(p))
                    .forEach(p -> {
                        insertUserProfile(userMap.get(p.getExternalId()), p);
                        ++status.count;

                        listener.accept((double)status.count/ workers.size());
                    });
        }catch (Exception e){
            log.error("error import users", e);

            status.errorMessage = e.getMessage();
        }

        return status;
    }

    @Transactional
    private void insertUserProfile(User user, Worker worker){
        if (!user.getLogin().isEmpty() && !user.getPassword().isEmpty()) {
            userMapper.insertUser(user);

            worker.setParentId(user.getId());
            worker.setParentEntityId(User.ENTITY_ID);
        }

        FirstName firstName = new FirstName();
        firstName.getOrCreateAttribute(FirstName.NAME).getOrCreateValue(Locales.getSystemLocaleId())
                .setText(worker.getText(Worker.FIRST_NAME));
        Long firstNameId = domainMapper.getDomainObjectId(firstName);
        if (firstNameId == null){
            domainMapper.insertDomain(firstName);
            firstNameId = firstName.getObjectId();
        }
        worker.setNumber(Worker.FIRST_NAME, firstNameId);

        MiddleName middleName = new MiddleName();
        middleName.getOrCreateAttribute(MiddleName.NAME).getOrCreateValue(Locales.getSystemLocaleId())
                .setText(worker.getText(Worker.SECOND_NAME));
        Long middleNameId = domainMapper.getDomainObjectId(middleName);
        if (middleNameId == null){
            domainMapper.insertDomain(middleName);
            middleNameId = middleName.getObjectId();
        }
        worker.setNumber(Worker.SECOND_NAME, middleNameId);

        LastName lastName = new LastName();
        lastName.getOrCreateAttribute(LastName.NAME).getOrCreateValue(Locales.getSystemLocaleId())
                .setText(worker.getText(Worker.LAST_NAME));
        Long lastNameId = domainMapper.getDomainObjectId(lastName);
        if (lastNameId == null){
            domainMapper.insertDomain(lastName);
            lastNameId = middleName.getObjectId();
        }
        worker.setNumber(Worker.LAST_NAME, lastNameId);

        domainMapper.insertDomain(worker);
    }
}

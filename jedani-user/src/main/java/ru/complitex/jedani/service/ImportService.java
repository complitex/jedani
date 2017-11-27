package ru.complitex.jedani.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.jedani.entity.Region;
import ru.complitex.jedani.mapper.RegionMapper;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 22.11.2017 16:49
 */
@RequestScoped
public class ImportService {
    private Logger log = LoggerFactory.getLogger(ImportService.class);

    @Inject
    private RegionMapper regionMapper;

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

                // use comma as separator
                String[] columns = line.split(";");

                if (columns.length != 3){
                    status.errorMessage = "Неверное количество колонок в файле: 3 <> " + columns.length;

                    break;
                }

                Region region = new Region();
                region.setId(Integer.valueOf(columns[0]));
                region.setName(columns[1]);
                region.setManagerId(Integer.valueOf(columns[2]));

                regions.add(region);
            }
        } catch (Exception e) {
            log.error("error import regions", e);

            status.errorMessage = e.getMessage();
        }

        if (status.errorMessage == null) {
            for (Region r : regions) {
                if (!regionMapper.hasRegion(r.getId())) {
                    regionMapper.insertRegion(r);

                    ++status.count;
                }
            }
        }

        return status;
    }
}

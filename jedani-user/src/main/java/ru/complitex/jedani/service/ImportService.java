package ru.complitex.jedani.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.jedani.entity.Region;
import ru.complitex.jedani.mapper.RegionMapper;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
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

    public void importRegions(InputStream inputStream){
        List<Region> regions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            String line;

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] columns = line.split(";");

                Region region = new Region();
                region.setId(Integer.valueOf(columns[0]));
                region.setName(columns[1]);
                region.setManagerId(Integer.valueOf(columns[2]));

                regions.add(region);
            }
        } catch (IOException e) {
            log.error("error import regions", e);

            throw new RuntimeException(e);
        }

        regionMapper.insertRegions(regions);
        //todo validate duplicate
    }
}

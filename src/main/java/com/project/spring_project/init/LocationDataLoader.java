package com.project.spring_project.init;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.spring_project.entity.location.City;
import com.project.spring_project.entity.location.Country;
import com.project.spring_project.entity.location.State;
import com.project.spring_project.repository.location.CityRepository;
import com.project.spring_project.repository.location.CountryRepository;
import com.project.spring_project.repository.location.StateRepository;
import com.project.spring_project.util.obj.LocationHelper;
import lombok.AllArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class LocationDataLoader implements CommandLineRunner {

    private final CountryRepository countryRepo;
    private final StateRepository stateRepo;
    private final CityRepository cityRepo;
    private final ObjectMapper objectMapper;

    /**
     * Loads country, state, and city data from JSON files into the database.
     * This method is executed at application startup.
     *
     * @param args command line arguments
     * @throws Exception if an error occurs during data loading
     */
    @Override
    public void run(String... args) throws Exception {
        //if(!importData) return; // skip if not configured to import data
        if (countryRepo.count() > 0) return; // avoid reimporting

        ObjectMapper mapper = new ObjectMapper();

        // Load and parse JSON
        InputStream countriesInput = getClass().getResourceAsStream("/data/countries.json");
        InputStream statesInput = getClass().getResourceAsStream("/data/states.json");
        InputStream citiesInput = getClass().getResourceAsStream("/data/cities.json");

        List<Country> countries = Arrays.asList(objectMapper.readValue(countriesInput, Country[].class));
        List<State> states = Arrays.asList(objectMapper.readValue(statesInput, State[].class));
        List<City> cities = Arrays.asList(objectMapper.readValue(citiesInput, City[].class));

        // Save countries
        countryRepo.saveAll(countries);

        InputStream statesInputDto = getClass().getResourceAsStream("/data/states.json");
        List<LocationHelper> statesHelper = Arrays.asList(objectMapper.readValue(statesInputDto, LocationHelper[].class));
        Map<Long, LocationHelper> LocationHelperMap = statesHelper.stream()
                .collect(Collectors.toMap(LocationHelper::getId, c -> c));
        for (State state : states) {
            LocationHelper LocationHelper = LocationHelperMap.get(state.getId());
            state.setCountry(countryRepo.findById(LocationHelper.getCountry_id()).orElse(null));
        }
        stateRepo.saveAll(states);

        InputStream cityInputDto = getClass().getResourceAsStream("/data/cities.json");
        List<LocationHelper> citiesHelper = Arrays.asList(objectMapper.readValue(cityInputDto, LocationHelper[].class));
        Map<Long, LocationHelper> LocationHelperMap1 = citiesHelper.stream()
                .collect(Collectors.toMap(LocationHelper::getId, c -> c));
        Map<Long, State> stateMap = stateRepo.findAll().stream()
                .collect(Collectors.toMap(State::getId, s -> s));
        for (City city : cities) {
            LocationHelper LocationHelper = LocationHelperMap1.get(city.getId());
            city.setState(stateMap.get(LocationHelper.getState_id()));
        }
        cityRepo.saveAll(cities);
    }
}
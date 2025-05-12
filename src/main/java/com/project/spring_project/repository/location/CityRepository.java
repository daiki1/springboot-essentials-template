package com.project.spring_project.repository.location;

import com.project.spring_project.entity.location.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {

}
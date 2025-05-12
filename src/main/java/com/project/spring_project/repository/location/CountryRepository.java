package com.project.spring_project.repository.location;

import com.project.spring_project.entity.location.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {

}
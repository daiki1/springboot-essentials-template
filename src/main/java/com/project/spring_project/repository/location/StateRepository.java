package com.project.spring_project.repository.location;

import com.project.spring_project.entity.location.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StateRepository extends JpaRepository<State, Long> {
    List<State> findByCountryId(Long countryId);

}

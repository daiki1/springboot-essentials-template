package com.project.spring_project.entity.location;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "states")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class State {
    @Id
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @OneToMany(mappedBy = "state", cascade = CascadeType.ALL)
    private List<City> cities;
}

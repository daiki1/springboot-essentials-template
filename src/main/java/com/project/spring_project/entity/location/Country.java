package com.project.spring_project.entity.location;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "countries")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Country {
    @Id
    private Long id;
    private String name;
    private String iso3;
    private String iso2;
    private String phonecode;
    private String capital;
    private String currency;
    @Column(name = "native")
    private String nativeName;
    private String region;
    private String subregion;
    private Double latitude;
    private Double longitude;
    private String emojiU;

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL)
    private List<State> states;
}


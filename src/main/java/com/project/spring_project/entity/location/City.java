package com.project.spring_project.entity.location;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cities")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class City {
    @Id
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

}

package ru.vladimir.sazonov.dispatchLog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.vladimir.sazonov.dispatchLog.model.categories.PositionsRtp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rtp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty
    private String fioRtp;

    @ManyToOne
    @JoinColumn(name = "emergencyTrip")
    private EmergencyTrips emergencyTrip;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "positionRtp")
    private PositionsRtp positionRtp;

    @Min(1)
    private byte orderRtp;
}

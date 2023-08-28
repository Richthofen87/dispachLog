package ru.vladimir.sazonov.dispatchLog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vladimir.sazonov.dispatchLog.model.categories.PositionsWasReported;

import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToWhomWasReported {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty
    private String nameWasReported;

    @ManyToOne
    @JoinColumn(name = "emergencyTrip")
    private EmergencyTrips emergencyTrip;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "positionWasReported")
    private PositionsWasReported positionWasReported;

    @NotNull
    private LocalTime messageTime;

    private String nameWasArrived;

    private LocalTime arrivedTime;
}

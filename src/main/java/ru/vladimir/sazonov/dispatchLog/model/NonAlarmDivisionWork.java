package ru.vladimir.sazonov.dispatchLog.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;
import ru.vladimir.sazonov.dispatchLog.model.categories.CarType;
import ru.vladimir.sazonov.dispatchLog.model.categories.Divisions;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NonAlarmDivisionWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @JoinColumn(name = "check_out_time")
    private LocalTime checkOutTime;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "division")
    private Divisions division;

    @NotEmpty
    private String divisionNumber;

    @Min(1)
    private byte stuff;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "non_emergencyTrip")
    private NonEmergencyTrips nonEmergencyTrip;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "car_type")
    private CarType carType;

    private LocalTime returnTime;
}

package ru.vladimir.sazonov.dispatchLog.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vladimir.sazonov.dispatchLog.model.categories.CarType;
import ru.vladimir.sazonov.dispatchLog.model.categories.Divisions;
import ru.vladimir.sazonov.dispatchLog.model.statistic.CarTypeInfo;
import ru.vladimir.sazonov.dispatchLog.model.statistic.CarsAndStuffCount;
import ru.vladimir.sazonov.dispatchLog.model.statistic.StuffInfo;

import java.time.LocalTime;

@SqlResultSetMapping(
        name = "carTypeInfo",
        classes = @ConstructorResult(
                targetClass = CarTypeInfo.class,
                columns = {
                        @ColumnResult(name = "carType", type = String.class),
                        @ColumnResult(name = "count", type = Integer.class),
                }
        )
)
@SqlResultSetMapping(
        name = "stuffInfo",
        classes = @ConstructorResult(
                targetClass = StuffInfo.class,
                columns = {
                        @ColumnResult(name = "division", type = String.class),
                        @ColumnResult(name = "count", type = Integer.class),
                }
        )
)
@SqlResultSetMapping(
        name = "carsAndStuffCount",
        classes = @ConstructorResult(
                targetClass = CarsAndStuffCount.class,
                columns = {
                        @ColumnResult(name = "cars", type = Integer.class),
                        @ColumnResult(name = "stuff", type = Integer.class),
                }
        )
)
@NamedNativeQuery(name = "AlarmDivisionWork.carTypeInfo", resultClass = CarTypeInfo.class, resultSetMapping = "carTypeInfo",
        query = """
                select count(*) as count,
                car_type as carType
                from alarm_division_work
                where emergency_trip = ?1
                and division = ?2
                group by car_type""")
@NamedNativeQuery(name = "AlarmDivisionWork.stuffInfo", resultClass = StuffInfo.class, resultSetMapping = "stuffInfo",
        query = """
                select sum(stuff) as count,
                division
                from alarm_division_work
                where emergency_trip = ?1
                group by division""")
@NamedNativeQuery(name = "AlarmDivisionWork.carsAndStuffCount", resultClass = CarsAndStuffCount.class, resultSetMapping = "carsAndStuffCount",
        query = """
                select count(*) as cars, sum(stuff) as stuff from alarm_division_work
                where emergency_trip in (
                select id from emergency_trips
                where (((date(date_time) = ?1 and check_out_time >= '08:00')
                or (date(date_time) = ?2 and check_out_time <= '07:59'))
                and trip_category = 'Пожар'
                and settlement = 'г.Магадан'
                ))""")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmDivisionWork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private LocalTime checkOutTime;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "division")
    private Divisions division;

    @NotEmpty
    private String divisionNumber;

    @Min(1)
    private byte stuff;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "emergencyTrip")
    private EmergencyTrips emergencyTrip;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "carType")
    private CarType carType;

    private LocalTime arrivalTime;

    private LocalTime returnTime;

    @Column(name = "barrel_a")
    private byte barrelA;

    @Column(name = "barrel_b")
    private byte barrelB;

    private byte barrelLs;

    private byte barrelGps;

    private byte barrelPurga;

    private byte barrelSvp;

    private byte gdzsCount;

    private byte gdzsWork;

    private String carNumber;
}

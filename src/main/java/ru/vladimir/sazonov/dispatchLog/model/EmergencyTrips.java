package ru.vladimir.sazonov.dispatchLog.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.vladimir.sazonov.dispatchLog.model.categories.*;
import ru.vladimir.sazonov.dispatchLog.model.statistic.FireStatisticCause;
import ru.vladimir.sazonov.dispatchLog.model.statistic.FireStatisticInfo;
import ru.vladimir.sazonov.dispatchLog.model.statistic.FireStatisticObject;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@SqlResultSetMapping(
        name = "fireStatisticInfo",
        classes = @ConstructorResult(
                targetClass = FireStatisticInfo.class,
                columns = {
                        @ColumnResult(name = "countOfFires", type = Integer.class),
                        @ColumnResult(name = "fireDamage", type = Integer.class),
                        @ColumnResult(name = "rescued", type = Integer.class),
                        @ColumnResult(name = "died", type = Integer.class),
                        @ColumnResult(name = "injured", type = Integer.class)
                }
        )
)
@SqlResultSetMapping(
        name = "fireStatisticCause",
        classes = @ConstructorResult(
                targetClass = FireStatisticCause.class,
                columns = {
                        @ColumnResult(name = "countOfFires", type = Integer.class),
                        @ColumnResult(name = "cause", type = String.class),
                }
        )
)
@SqlResultSetMapping(
        name = "fireStatisticObject",
        classes = @ConstructorResult(
                targetClass = FireStatisticObject.class,
                columns = {
                        @ColumnResult(name = "countOfFires", type = Integer.class),
                        @ColumnResult(name = "fireObject", type = String.class),
                }
        )
)
@NamedNativeQuery(name = "EmergencyTrips.fireStatisticInfo", resultClass = FireStatisticInfo.class, resultSetMapping = "fireStatisticInfo",
        query = """
                select count(*) as countOfFires,
                coalesce(sum(fire_damage), 0) as fireDamage,
                coalesce(sum(rescued), 0) as rescued,
                coalesce(sum(died), 0) as died,
                coalesce(sum(injured), 0) as injured
                from emergency_trips
                where ((date(date_time) = ?1 and check_out_time >= '08:00')
                or (date(date_time) = ?1 and check_out_time is null and message_time >= '08:00')
                or (date(date_time) = ?2 and check_out_time <= '07:59')
                or (date(date_time) = ?2 and check_out_time is null and message_time <= '07:59'))
                and settlement in ?3
                and trip_category = 'Пожар'""")
@NamedNativeQuery(name = "EmergencyTrips.fireStatisticCause", resultClass = FireStatisticInfo.class, resultSetMapping = "fireStatisticCause",
        query = """
                select count(*) as countOfFires,
                cause_of_the_fire as cause
                from emergency_trips
                where ((date(date_time) = ?1 and check_out_time >= '08:00')
                or (date(date_time) = ?1 and check_out_time is null and message_time >= '08:00')
                or (date(date_time) = ?2 and check_out_time <= '07:59')
                or (date(date_time) = ?2 and check_out_time is null and message_time <= '07:59'))
                and settlement in ?3
                and trip_category = 'Пожар'
                group by cause_of_the_fire""")
@NamedNativeQuery(name = "EmergencyTrips.fireStatisticObject", resultClass = FireStatisticInfo.class, resultSetMapping = "fireStatisticObject",
        query = """
                select count(*) as countOfFires,
                fire_object as fireObject
                from emergency_trips
                where ((date(date_time) = ?1 and check_out_time >= '08:00')
                or (date(date_time) = ?1 and check_out_time is null and message_time >= '08:00')
                or (date(date_time) = ?2 and check_out_time <= '07:59')
                or (date(date_time) = ?2 and check_out_time is null and message_time <= '07:59'))
                and settlement in ?3
                and trip_category = 'Пожар'
                group by fire_object""")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyTrips {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private LocalDateTime dateTime;

    @NotNull
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "settlement")
    private Settlements settlement;

    @NotBlank
    @Size(max = 50)
    private String address;

    @NotNull
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "tripCategory")
    private EmergencyTripsCategories tripCategory;

    @NotBlank
    @Size(max = 30)
    private String applicant;

    @Size(max = 30)
    private String whoReported;

    @NotNull
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "whoTook")
    private FioDispatcher whoTook;

    private String primaryMessage;

    @NotNull
    private LocalTime messageTime;

    private LocalTime checkOutTime;

    private LocalTime arrivalTime;

    private LocalTime firstBarrelTime;

    private LocalTime locTime;

    private LocalTime logTime;

    private LocalTime completeLiqTime;

    private LocalTime returnTime;

    @NotNull
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "departureArea")
    private Divisions departureArea;

    private String fireRank;

    private boolean fireFightingHeadquarters;

    private byte rescued;

    private byte rescuedChildren;

    private byte injured;

    private byte injuredChildren;

    private byte evacuated;

    private byte evacuatedChildren;

    private byte died;

    private byte diedChildren;

    private byte diedBeforeArrival;

    private byte diedBeforeArrivalChildren;

    private byte released;

    private byte releasedChildren;

    private byte injuredEmployees;

    private byte diedEmployees;

    private String extinguished;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "waterSource")
    private WaterSource waterSource;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "waterSupply")
    private WaterSupply waterSupply;

    private int drainedWater;

    private int drainedFoamConcentrate;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "whoExtinguished")
    private WhoExtinguished whoExtinguished;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cause_of_the_fire")
    private FireCause causeOfTheFire;

    private String guiltyPerson;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "fireObject")
    private FireObject fireObject;

    private int fireDamage;

    private String descript;

    private String radioExchange;

    private String additionalInformation;

    @Column(name = "is_complete")
    private boolean complete;

    @Valid
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "emergencyTrip", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AlarmDivisionWork> alarmDivisionWorkList = new ArrayList<>();

    @Valid
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "emergencyTrip", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Rtp> rtpList = new ArrayList<>();

    @Valid
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "emergencyTrip", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ToWhomWasReported> toWhomWasReportedList = new ArrayList<>();

    public void addAlarmDivisionWork(Integer index) {
        int count = (index == null || index == 0) ? 1 : index;
        for (int i = 0; i < count; i++) {
            AlarmDivisionWork alarmDivisionWork = new AlarmDivisionWork();
            alarmDivisionWork.setEmergencyTrip(this);
            alarmDivisionWorkList.add(alarmDivisionWork);
        }
    }

    public void addRtp(Integer index) {
        int count = (index == null || index == 0) ? 1 : index;
        for (int i = 0; i < count; i++) {
            Rtp rtp = new Rtp();
            rtp.setEmergencyTrip(this);
            rtpList.add(rtp);
        }
    }

    public void addReported(Integer index) {
        int count = (index == null || index == 0) ? 1 : index;
        for (int i = 0; i < count; i++) {
            ToWhomWasReported toWhomWasReported = new ToWhomWasReported();
            toWhomWasReported.setEmergencyTrip(this);
            toWhomWasReportedList.add(toWhomWasReported);
        }
    }
}

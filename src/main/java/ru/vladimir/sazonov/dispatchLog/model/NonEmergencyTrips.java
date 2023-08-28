package ru.vladimir.sazonov.dispatchLog.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.vladimir.sazonov.dispatchLog.model.categories.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NonEmergencyTrips {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private LocalDateTime dateTime;

    @NotNull
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "settlement")
    private Settlements settlement;

    @NotBlank
    @Size(max = 50)
    private String address;

    @NotNull
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "tripCategory")
    private NonEmergencyTripsCategories tripCategory;

    @NotEmpty
    @Size(max = 30)
    private String whoReported;

    @NotNull
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "whoTook")
    private FioDispatcher whoTook;

    @Column(name = "is_complete")
    private boolean complete;

    private String additionalInformation;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @Valid
    @OneToMany(mappedBy = "nonEmergencyTrip", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NonAlarmDivisionWork> nonAlarmDivisionWorkList = new ArrayList<>();

    public void addNonAlarmDivisionWork(Integer index) {
        int count = (index == null || index == 0) ? 1 : index;
        for (int i = 0; i < count; i++) {
            NonAlarmDivisionWork nonAlarmDivisionWork = new NonAlarmDivisionWork();
            nonAlarmDivisionWork.setNonEmergencyTrip(this);
            nonAlarmDivisionWorkList.add(nonAlarmDivisionWork);
        }
    }
}
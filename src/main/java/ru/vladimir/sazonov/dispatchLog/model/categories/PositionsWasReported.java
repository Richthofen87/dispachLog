package ru.vladimir.sazonov.dispatchLog.model.categories;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Entity
@Data
@NoArgsConstructor()
@AllArgsConstructor
public class PositionsWasReported {
    @Id
    private String name;

//    @OneToMany(mappedBy = "positionWasReported")
//    List<ToWhomWasReported> toWhomWasReportedList;
}
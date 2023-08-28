package ru.vladimir.sazonov.dispatchLog.model.categories;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Entity
@Data
@NoArgsConstructor()
@AllArgsConstructor
public class WhoExtinguished {
    @Id
    private String name;

//    @OneToMany(mappedBy = "whoExtinguished")
//    List<EmergencyTrips> emergencyTripsList;
}

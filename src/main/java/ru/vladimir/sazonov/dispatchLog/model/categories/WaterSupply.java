package ru.vladimir.sazonov.dispatchLog.model.categories;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Entity
@Data
@NoArgsConstructor()
@AllArgsConstructor
public class WaterSupply {
    @Id
    private String name;

//    @OneToMany(mappedBy = "waterSupply")
//    List<EmergencyTrips> emergencyTripsList;
}

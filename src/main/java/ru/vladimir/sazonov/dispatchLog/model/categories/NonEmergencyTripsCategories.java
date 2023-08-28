package ru.vladimir.sazonov.dispatchLog.model.categories;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Data
@NoArgsConstructor()
@AllArgsConstructor
public class NonEmergencyTripsCategories {
    @Id
    private String name;
}
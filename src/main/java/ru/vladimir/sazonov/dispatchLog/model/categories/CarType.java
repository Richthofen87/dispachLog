package ru.vladimir.sazonov.dispatchLog.model.categories;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarType {
    @Id
    private String name;
}
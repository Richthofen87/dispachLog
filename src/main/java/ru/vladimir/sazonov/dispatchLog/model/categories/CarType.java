package ru.vladimir.sazonov.dispatchLog.model.categories;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarType {
    @Id
    private String name;
}
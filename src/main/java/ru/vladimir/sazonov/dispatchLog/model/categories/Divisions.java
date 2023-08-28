package ru.vladimir.sazonov.dispatchLog.model.categories;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Divisions {
    @Id
    private String name;

    @ManyToOne
    @JoinColumn(name = "settlement")
    private Settlements settlement;
}

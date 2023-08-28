package ru.vladimir.sazonov.dispatchLog.model.categories;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Garrison {
    @NotEmpty
    @Id
    private String name;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "garrison", fetch = FetchType.LAZY)
    private List<Settlements> settlementsList;
}

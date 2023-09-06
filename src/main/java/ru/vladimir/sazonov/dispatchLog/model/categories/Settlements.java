package ru.vladimir.sazonov.dispatchLog.model.categories;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor()
@AllArgsConstructor
public class Settlements {
    @Id
    private String name;

    @ManyToOne
    @JoinColumn(name = "garrison")
    private Garrison garrison;

    private boolean urban;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "settlement", fetch = FetchType.LAZY)
    private List<Divisions> divisionsList;
}
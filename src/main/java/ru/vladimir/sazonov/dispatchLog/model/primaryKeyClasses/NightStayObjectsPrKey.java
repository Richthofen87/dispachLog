package ru.vladimir.sazonov.dispatchLog.model.primaryKeyClasses;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
public class NightStayObjectsPrKey implements Serializable {

    private String name;

    private String address;
}

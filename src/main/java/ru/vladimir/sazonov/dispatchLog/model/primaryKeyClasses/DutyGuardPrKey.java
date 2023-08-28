package ru.vladimir.sazonov.dispatchLog.model.primaryKeyClasses;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Data
@NoArgsConstructor
public class DutyGuardPrKey implements Serializable {

    private LocalDate dutyDate;

    @NotEmpty(message = "Обязательное поле")
    private String guardNumber;
}

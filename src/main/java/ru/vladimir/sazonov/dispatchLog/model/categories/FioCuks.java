package ru.vladimir.sazonov.dispatchLog.model.categories;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FioCuks {
    @NotEmpty(message = "Обязательное поле")
    @Id
    private String name;
}

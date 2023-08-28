package ru.vladimir.sazonov.dispatchLog.model.categories;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vladimir.sazonov.dispatchLog.model.categories.FioCuks;
import ru.vladimir.sazonov.dispatchLog.model.categories.FioDispatcher;
import ru.vladimir.sazonov.dispatchLog.model.categories.FioSpt;
import ru.vladimir.sazonov.dispatchLog.model.primaryKeyClasses.DutyGuardPrKey;
import ru.vladimir.sazonov.dispatchLog.model.primaryKeyClasses.NightStayObjectsPrKey;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NightStayObjects {

    @EmbeddedId
    private NightStayObjectsPrKey prKey;

    private String tel;

    @Transient
    private int peopleCount;

    @Transient
    private int immobilePeopleCount;

    @Transient
    private int stuffCount;
}

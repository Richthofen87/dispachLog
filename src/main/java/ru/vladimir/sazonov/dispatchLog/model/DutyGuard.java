package ru.vladimir.sazonov.dispatchLog.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vladimir.sazonov.dispatchLog.model.categories.FioCuks;
import ru.vladimir.sazonov.dispatchLog.model.categories.FioDispatcher;
import ru.vladimir.sazonov.dispatchLog.model.categories.FioSpt;
import ru.vladimir.sazonov.dispatchLog.model.primaryKeyClasses.DutyGuardPrKey;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DutyGuard {

    @Valid
    @EmbeddedId
    private DutyGuardPrKey prKey;

    @Valid
    @ManyToOne
    @JoinColumn(name = "sodCuks")
    private FioCuks sodCuks;

    @ManyToOne
    @JoinColumn(name = "ndsCuks")
    private FioCuks ndsCuks;

    @Valid
    @ManyToOne
    @JoinColumn(name = "ndsSpt")
    private FioSpt ndsSpt;

    @ManyToOne
    @JoinColumn(name = "spndsSpt")
    private FioSpt spndsSpt;

    @Valid
    @ManyToOne
    @JoinColumn(name = "garrisonDispatcher")
    private FioDispatcher garrisonDispatcher;

    @ManyToOne
    @JoinColumn(name = "cppsDispatcher")
    private FioDispatcher cppsDispatcher;
}

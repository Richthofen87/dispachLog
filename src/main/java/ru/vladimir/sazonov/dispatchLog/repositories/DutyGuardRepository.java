package ru.vladimir.sazonov.dispatchLog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vladimir.sazonov.dispatchLog.model.DutyGuard;
import ru.vladimir.sazonov.dispatchLog.model.primaryKeyClasses.DutyGuardPrKey;

public interface DutyGuardRepository extends JpaRepository<DutyGuard, DutyGuardPrKey> {
}

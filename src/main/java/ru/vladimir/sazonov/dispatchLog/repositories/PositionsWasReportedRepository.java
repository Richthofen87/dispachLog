package ru.vladimir.sazonov.dispatchLog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vladimir.sazonov.dispatchLog.model.categories.PositionsRtp;
import ru.vladimir.sazonov.dispatchLog.model.categories.PositionsWasReported;

public interface PositionsWasReportedRepository extends JpaRepository<PositionsWasReported, String> {
}

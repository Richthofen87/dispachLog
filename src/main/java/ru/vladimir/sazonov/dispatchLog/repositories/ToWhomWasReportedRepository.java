package ru.vladimir.sazonov.dispatchLog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vladimir.sazonov.dispatchLog.model.ToWhomWasReported;

public interface ToWhomWasReportedRepository extends JpaRepository<ToWhomWasReported, Integer> {
}

package ru.vladimir.sazonov.dispatchLog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vladimir.sazonov.dispatchLog.model.categories.PositionsRtp;

public interface PositionsRtpRepository extends JpaRepository<PositionsRtp, String> {
}

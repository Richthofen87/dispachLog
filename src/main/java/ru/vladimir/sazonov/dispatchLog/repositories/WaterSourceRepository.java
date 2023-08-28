package ru.vladimir.sazonov.dispatchLog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vladimir.sazonov.dispatchLog.model.categories.PositionsRtp;
import ru.vladimir.sazonov.dispatchLog.model.categories.WaterSource;

public interface WaterSourceRepository extends JpaRepository<WaterSource, String> {
}

package ru.vladimir.sazonov.dispatchLog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vladimir.sazonov.dispatchLog.model.categories.WaterSource;
import ru.vladimir.sazonov.dispatchLog.model.categories.WaterSupply;

public interface WaterSupplyRepository extends JpaRepository<WaterSupply, String> {
}

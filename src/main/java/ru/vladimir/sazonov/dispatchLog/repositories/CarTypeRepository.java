package ru.vladimir.sazonov.dispatchLog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vladimir.sazonov.dispatchLog.model.categories.CarType;

public interface CarTypeRepository extends JpaRepository<CarType, String> {
}

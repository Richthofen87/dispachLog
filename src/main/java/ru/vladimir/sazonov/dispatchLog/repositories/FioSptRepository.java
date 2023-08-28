package ru.vladimir.sazonov.dispatchLog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vladimir.sazonov.dispatchLog.model.categories.FioCuks;
import ru.vladimir.sazonov.dispatchLog.model.categories.FioSpt;

public interface FioSptRepository extends JpaRepository<FioSpt, String> {
}

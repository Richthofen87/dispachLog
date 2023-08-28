package ru.vladimir.sazonov.dispatchLog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vladimir.sazonov.dispatchLog.model.categories.FioCuks;

public interface FioCuksRepository extends JpaRepository<FioCuks, String> {
}

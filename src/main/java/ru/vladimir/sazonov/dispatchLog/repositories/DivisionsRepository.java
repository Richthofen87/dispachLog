package ru.vladimir.sazonov.dispatchLog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vladimir.sazonov.dispatchLog.model.categories.Divisions;

public interface DivisionsRepository extends JpaRepository<Divisions, String> {
}

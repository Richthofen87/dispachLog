package ru.vladimir.sazonov.dispatchLog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vladimir.sazonov.dispatchLog.model.categories.FioCuks;
import ru.vladimir.sazonov.dispatchLog.model.categories.FireObject;

public interface FireObjectRepository extends JpaRepository<FireObject, String> {
}

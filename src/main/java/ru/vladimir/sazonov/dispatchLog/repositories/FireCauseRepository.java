package ru.vladimir.sazonov.dispatchLog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vladimir.sazonov.dispatchLog.model.categories.FireCause;

public interface FireCauseRepository extends JpaRepository<FireCause, String> {
}

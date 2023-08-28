package ru.vladimir.sazonov.dispatchLog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vladimir.sazonov.dispatchLog.model.categories.WhoExtinguished;

public interface WhoExtinguishedRepository extends JpaRepository<WhoExtinguished, String> {
}

package ru.vladimir.sazonov.dispatchLog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vladimir.sazonov.dispatchLog.model.categories.FioDispatcher;

public interface FioDispatcherRepository extends JpaRepository<FioDispatcher, String> {
}

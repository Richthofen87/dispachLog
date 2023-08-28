package ru.vladimir.sazonov.dispatchLog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vladimir.sazonov.dispatchLog.model.categories.EmergencyTripsCategories;

public interface EmergencyTripsCategoriesRepository extends JpaRepository<EmergencyTripsCategories, String> {
}

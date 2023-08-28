package ru.vladimir.sazonov.dispatchLog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vladimir.sazonov.dispatchLog.model.categories.EmergencyTripsCategories;
import ru.vladimir.sazonov.dispatchLog.model.categories.NonEmergencyTripsCategories;

public interface NonEmergencyTripsCategoriesRepository extends JpaRepository<NonEmergencyTripsCategories, String> {
}

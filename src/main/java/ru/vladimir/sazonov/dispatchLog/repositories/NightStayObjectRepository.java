package ru.vladimir.sazonov.dispatchLog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vladimir.sazonov.dispatchLog.model.categories.NightStayObjects;
import ru.vladimir.sazonov.dispatchLog.model.primaryKeyClasses.NightStayObjectsPrKey;

public interface NightStayObjectRepository extends JpaRepository<NightStayObjects, NightStayObjectsPrKey> {
}

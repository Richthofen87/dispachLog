package ru.vladimir.sazonov.dispatchLog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vladimir.sazonov.dispatchLog.model.NonAlarmDivisionWork;

public interface NonAlarmDivisionWorkRepository extends JpaRepository<NonAlarmDivisionWork, Integer> {
}

package ru.vladimir.sazonov.dispatchLog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.vladimir.sazonov.dispatchLog.model.AlarmDivisionWork;
import ru.vladimir.sazonov.dispatchLog.model.statistic.CarTypeInfo;
import ru.vladimir.sazonov.dispatchLog.model.statistic.CarsAndStuffCount;
import ru.vladimir.sazonov.dispatchLog.model.statistic.StuffInfo;

import java.time.LocalDate;
import java.util.List;

public interface AlarmDivisionWorkRepository extends JpaRepository<AlarmDivisionWork, Integer> {

    @Query(nativeQuery = true, name = "AlarmDivisionWork.carTypeInfo")
    List<CarTypeInfo> getCarTypeInfo(int emergencyTripId, String division);

    @Query(nativeQuery = true, name = "AlarmDivisionWork.stuffInfo")
    List<StuffInfo> getStuffCount(int emergencyTripId);

    @Query(nativeQuery = true, name = "AlarmDivisionWork.carsAndStuffCount")
    CarsAndStuffCount getCarsAndStuffCount(LocalDate start, LocalDate end);
}

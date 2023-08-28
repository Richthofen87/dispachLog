package ru.vladimir.sazonov.dispatchLog.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.vladimir.sazonov.dispatchLog.model.NonEmergencyTrips;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface NonEmergencyTripsRepository extends JpaRepository<NonEmergencyTrips, Integer> {

    Page<NonEmergencyTrips> findAllBySettlementNameIn(List<String> settlements, Pageable page);

    @Query(nativeQuery = true, value = """
            select * from non_emergency_trips
            where (date_time >= ?1 and date_time <= ?2)
            and settlement in (?3)
            and trip_category = ?4""")
    Page<NonEmergencyTrips> findAllByDateTimeBetweenAndSettlementNameInAndTripCategoryName(LocalDateTime start, LocalDateTime end,
                                                                                           List<String> settlements, String category, Pageable page);

    @Query(nativeQuery = true, value = """
            select * from non_emergency_trips
            where (date_time >= ?1 and date_time <= ?2)
            and settlement in (select name from settlements where garrison = ?3)
            and trip_category = ?4""")
    Page<NonEmergencyTrips> findAllByDateTimeBetweenAndSettlementGarrisonNameAndTripCategoryName(LocalDateTime start, LocalDateTime end,
                                                                                                 String garrison, String category, Pageable page);

    @Query(nativeQuery = true, value = """
            select * from non_emergency_trips
            where (date_time >= ?1 and date_time <= ?2)
            and settlement in (?3)""")
    Page<NonEmergencyTrips> findAllByDateTimeBetweenAndSettlementNameIn(LocalDateTime start, LocalDateTime end,
                                                                        List<String> settlements, Pageable page);

    @Query(nativeQuery = true, value = """
            select * from non_emergency_trips
            where (date_time >= ?1 and date_time <= ?2)
            and settlement in (select name from settlements where garrison = ?3)""")
    Page<NonEmergencyTrips> findAllByDateTimeBetweenAndSettlementGarrisonName(LocalDateTime start, LocalDateTime end,
                                                                              String garrison, Pageable page);

}

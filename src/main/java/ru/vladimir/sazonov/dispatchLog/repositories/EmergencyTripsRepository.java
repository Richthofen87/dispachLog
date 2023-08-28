package ru.vladimir.sazonov.dispatchLog.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.vladimir.sazonov.dispatchLog.model.EmergencyTrips;
import ru.vladimir.sazonov.dispatchLog.model.statistic.FireStatisticCause;
import ru.vladimir.sazonov.dispatchLog.model.statistic.FireStatisticInfo;
import ru.vladimir.sazonov.dispatchLog.model.statistic.FireStatisticObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface EmergencyTripsRepository extends JpaRepository<EmergencyTrips, Integer> {

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where settlement in (?1)
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllBySettlementNameIn(List<String> settlements, Pageable page);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where (date(date_time) = ?1 and check_out_time >= '07:30')
            or (date(date_time) = ?1 and check_out_time is null and message_time >= '07:30')
            or (date(date_time) = ?2 and check_out_time <= '07:29')
            or (date(date_time) = ?2 and check_out_time is null and message_time <= '07:29')""")
    List<EmergencyTrips> findAllDutyTrips(LocalDate start, LocalDate end);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and trip_category = 'Пожар'
            and settlement in (?5)
            and fire_rank = ?6
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByDateTimeBetweenAndSettlementNameInAndFireRank(
            LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
            List<String> settlements, String fireRank, Pageable page);

    @Query(nativeQuery = true, name = "EmergencyTrips.fireStatisticInfo")
    FireStatisticInfo getFireStaticInfo(LocalDate start, LocalDate end, List<String> settlements);

    @Query(nativeQuery = true, name = "EmergencyTrips.fireStatisticCause")
    List<FireStatisticCause> getFireStaticCause(LocalDate start, LocalDate end, List<String> settlements);

    @Query(nativeQuery = true, name = "EmergencyTrips.fireStatisticObject")
    List<FireStatisticObject> getFireStaticObject(LocalDate start, LocalDate end, List<String> settlements);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and settlement in (?5)
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByDateTimeBetweenAndSettlementNameIn(
            LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
            List<String> settlements, Pageable page);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and settlement in (select name from settlements where garrison = ?5)
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByDateTimeBetweenAndSettlementGarrisonName(
            LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
            String garrison, Pageable page);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and settlement in (select name from settlements where garrison = ?5)
            and fire_rank = ?6
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByDateTimeBetweenAndSettlementGarrisonNameAndFireRank(
            LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
            String garrison, String fireRank, Pageable page);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and settlement in (?5)
            and trip_category = ?6
            and died = ?7
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByDateTimeBetweenAndSettlementNameInAndTripCategoryNameAndDied(
            LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
            List<String> settlements, String category, int died, Pageable page);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and settlement in (select name from settlements where garrison = ?5)
            and trip_category = ?6
            and died = ?7
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByDateTimeBetweenAndSettlementGarrisonNameAndTripCategoryNameAndDied(
            LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
            String garrison, String category, int died, Pageable page);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and settlement in (?5)
            and trip_category = ?6
            and died >= ?7
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByDateTimeBetweenAndSettlementNameInAndTripCategoryNameAndDiedGreaterThan(
            LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
            List<String> settlements, String category, int died, Pageable page);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and settlement in (select name from settlements where garrison = ?5)
            and trip_category = ?6
            and died >= ?7
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByDateTimeBetweenAndSettlementGarrisonNameAndTripCategoryNameAndDiedGreaterThan(
            LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
            String garrison, String category, int died, Pageable page);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and settlement in (?5)
            and fire_object = ?6
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByDateTimeBetweenAndSettlementNameInAndFireObjectName(
            LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
            List<String> settlements, String fireObject, Pageable page);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and settlement in (select name from settlements where garrison = ?5)
            and fire_object = ?6
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByDateTimeBetweenAndSettlementGarrisonNameAndFireObjectName(
            LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
            String garrison, String fireObject, Pageable page);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and settlement in (?5)
            and trip_category = ?6
            and injured = ?7
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByDateTimeBetweenAndSettlementNameInAndTripCategoryNameAndInjured(
            LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
            List<String> settlements, String category, int injured, Pageable page);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and settlement in (select name from settlements where garrison = ?5)
            and trip_category = ?6
            and injured = ?7
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByDateTimeBetweenAndSettlementGarrisonNameAndTripCategoryNameAndInjured(
            LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
            String garrison, String category, int injured, Pageable page);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and settlement in (?5)
            and trip_category = ?6
            and injured >= ?7
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByDateTimeBetweenAndSettlementNameInAndTripCategoryNameAndInjuredGreaterThan(
            LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
            List<String> settlements, String category, int injured, Pageable page);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and settlement in (select name from settlements where garrison = ?5)
            and trip_category = ?6
            and injured >= ?7
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByDateTimeBetweenAndSettlementGarrisonNameAndTripCategoryNameAndInjuredGreaterThan(
            LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
            String garrison, String category, int injured, Pageable page);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and settlement in (?5)
            and trip_category = ?6
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByDateTimeBetweenAndSettlementNameInAndTripCategoryName(
            LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
            List<String> settlements, String category, Pageable page);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and settlement in (?5)
            and trip_category = 'Пожар'
            order by date(date_time), coalesce(check_out_time, message_time)""")
    List<EmergencyTrips> findAllFireTrips(LocalDate startDate, LocalTime startTime,
                                          LocalDate endDate, LocalTime endTime, List<String> settlements);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and settlement in (select name from settlements where garrison = ?5)
            and trip_category = ?6
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByGarrisonAndTripCategory(
            LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
            String garrison, String category, Pageable page);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and trip_category = ?5
            and departure_area = ?6
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByDateTimeBetweenAndTripCategoryNameAndDepartureAreaName(
            LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
            String category, String departureArea, Pageable page);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and settlement in (?5)
            and id in (
            select emergency_trip from alarm_division_work
            group by emergency_trip
            having sum(gdzs_count) = ?6)
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByGdzsCount(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
                                            List<String> settlements, int gdzsCount, Pageable pageable);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and settlement in (select name from settlements where garrison = ?5)
            and id in (
            select emergency_trip from alarm_division_work
            group by emergency_trip
            having sum(gdzs_count) = ?6)
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByGarrisonAndGdzsCount(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
                                                       String garrison, int gdzsCount, Pageable pageable);


    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and settlement in (?5)
            and id in (
            select emergency_trip from alarm_division_work
            group by emergency_trip
            having sum(gdzs_count) >= ?6)
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByGdzsCountGraiterThen(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
                                                       List<String> settlements, int gdzsCount, Pageable pageable);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and settlement in (select name from settlements where garrison = ?5)
            and id in (
            select emergency_trip from alarm_division_work
            group by emergency_trip
            having sum(gdzs_count) >= ?6)
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByGarrisonAndGdzsCountGraterThen(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
                                                                 String garrison, int gdzsCount, Pageable pageable);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and settlement in (?5)
            and id in (
            select emergency_trip from alarm_division_work
            group by emergency_trip
            having (sum(barrel_a) + sum(barrel_b) + sum(barrel_ls)) = ?6)
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByWaterBarrelsCount(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
                                                    List<String> settlements, int barrelsCount, Pageable pageable);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and settlement in (select name from settlements where garrison = ?5)
            and id in (
            select emergency_trip from alarm_division_work
            group by emergency_trip
            having (sum(barrel_a) + sum(barrel_b) + sum(barrel_ls)) = ?6)
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByGarrisonAndWaterBarrelsCount(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
                                                               String garrison, int barrelsCount, Pageable pageable);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and settlement in (?5)
            and id in (
            select emergency_trip from alarm_division_work
            group by emergency_trip
            having (sum(barrel_a) + sum(barrel_b) + sum(barrel_ls)) >= ?6)
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByWaterBarrelsCountGraterThen(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
                                                              List<String> settlements, int barrelsCount, Pageable pageable);

    @Query(nativeQuery = true, value = """
            select * from emergency_trips
            where ((date(date_time) = ?1 and check_out_time >= ?2)
            or (date(date_time) = ?1 and check_out_time is null and message_time >= ?2)
            or (date(date_time) = ?3 and check_out_time <= ?4)
            or (date(date_time) = ?3 and check_out_time is null and message_time <= ?4)
            or (date(date_time) > ?1 and date(date_time) < ?3))
            and settlement in (select name from settlements where garrison = ?5)
            and id in (
            select emergency_trip from alarm_division_work
            group by emergency_trip
            having (sum(barrel_a) + sum(barrel_b) + sum(barrel_ls)) >= ?6)
            order by date(date_time) desc, coalesce(check_out_time, message_time) desc""")
    Page<EmergencyTrips> findAllByGarrisonAndWaterBarrelsCountGraterThen(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime,
                                                                         String garrison, int barrelsCount, Pageable pageable);
}

package ru.vladimir.sazonov.dispatchLog.service;

import org.springframework.data.domain.Page;
import ru.vladimir.sazonov.dispatchLog.model.*;
import ru.vladimir.sazonov.dispatchLog.model.statistic.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DAOService {
    Map<String, List<String>> getCategoriesMap();

    Map<String, List<String>> getGarrisonSettlementsMap();

    Map<String, List<String>> getSettlementDivisionsMap();

    DutyGuard saveDutyGuard(DutyGuard dutyGuard);

    EmergencyTrips saveOrUpdateEmergencyTrip(EmergencyTrips emergencyTrip, Set<AlarmDivisionWork> workSet,
                                             Set<Rtp> rtpSet, Set<ToWhomWasReported> reportedSet);

    NonEmergencyTrips saveOrUpdateNonEmergencyTrip(NonEmergencyTrips nonEmergencyTrip, Set<NonAlarmDivisionWork> workSet);

    Page<EmergencyTrips> getCityEmergencyTripsPage(int pageNumber, int tripsCount);

    Page<EmergencyTrips> getRegionEmergencyTripsPage(int pageNumber, int tripsCount);

    Page<NonEmergencyTrips> getCityNonEmergencyTripsPage(int pageNumber, int tripsCount);

    Page<NonEmergencyTrips> getRegionNonEmergencyTripsPage(int pageNumber, int tripsCount);

    void deleteNonEmergencyTrip(NonEmergencyTrips nonEmergencyTrip, Set<NonAlarmDivisionWork> workSet);

    void deleteEmergencyTrip(EmergencyTrips emergencyTrip, Set<AlarmDivisionWork> workSet,
                             Set<Rtp> rtpSet, Set<ToWhomWasReported> reportedSet);

    Page<NonEmergencyTrips> findAllCityNonEmergencyTripsByCategory(LocalDateTime start, LocalDateTime end, String category,
                                                                   int pageNumber, int tripsCount);

    Page<NonEmergencyTrips> findAllRegionNonEmergencyTripsByCategory(
            LocalDateTime start, LocalDateTime end, String category, int pageNumber, int tripsCount);

    Page<NonEmergencyTrips> findAllNonEmergencyTripsByGarrisonAndCategory(
            LocalDateTime start, LocalDateTime end, String garrison, String category, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllCityTripsByFireRank(LocalDateTime start, LocalDateTime end,
                                                    String fireRank, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllCityTrips(LocalDateTime start, LocalDateTime end, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllRegionTrips(LocalDateTime start, LocalDateTime end, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllRegionTripsByGarrison(LocalDateTime start, LocalDateTime end, String garrison, int pageNumber, int tripsCount);

    Page<NonEmergencyTrips> findAllCityNonEmergencyTrips(LocalDateTime start, LocalDateTime end, int pageNumber, int tripsCount);

    Page<NonEmergencyTrips> findAllRegionNonEmergencyTrips(LocalDateTime start, LocalDateTime end, int pageNumber, int tripsCount);

    Page<NonEmergencyTrips> findAllRegionNonEmergencyTripsByGarrison(LocalDateTime start, LocalDateTime end, String garrison, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllTripsByGarrisonAndFireRank(LocalDateTime start, LocalDateTime end, String garrison,
                                                           String fireRank, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllCityTripsByTripCategoryAndDied(
            LocalDateTime start, LocalDateTime end, String category, int died, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllTripsByGarrisonAndTripCategoryAndDied(
            LocalDateTime start, LocalDateTime end, String garrison, String category, int died, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllCityTripsByTripCategoryAndDiedGraterThen(LocalDateTime start, LocalDateTime end,
                                                                         String category, int died, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllTripsByGarrisonAndTripCategoryAndDiedGraterThen(LocalDateTime start, LocalDateTime end, String garrison,
                                                                                String category, int died, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllCityTripsByFireObject(
            LocalDateTime start, LocalDateTime end, String fireObject, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllTripsByGarrisonAndFireObject(
            LocalDateTime start, LocalDateTime end, String garrison, String fireObject, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllCityTripsByTripCategoryAndInjured(
            LocalDateTime start, LocalDateTime end, String category, int injured, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllTripsByGarrisonAndTripCategoryAndInjured(
            LocalDateTime start, LocalDateTime end, String garrison, String category, int injured, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllCityTripsByTripCategoryAndInjuredGraterThen(LocalDateTime start, LocalDateTime end,
                                                                            String category, int injured, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllTripsByGarrisonAndTripCategoryAndInjuredGraterThen(LocalDateTime start, LocalDateTime end, String garrison,
                                                                                   String category, int injured, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllCityTripsByTripCategory(
            LocalDateTime start, LocalDateTime end, String category, int pageNumber, int tripsCount);

    List<EmergencyTrips> findAllCityFireTrips(LocalDateTime start, LocalDateTime end);

    List<EmergencyTrips> findAllRegionFireTrips(LocalDateTime start, LocalDateTime end);

    Page<EmergencyTrips> findAllTripsByGarrisonAndTripCategory(
            LocalDateTime start, LocalDateTime end, String garrison, String category, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllTripsByTripCategoryAndDepartureArea(LocalDateTime start, LocalDateTime end,
                                                                    String category, String departureArea, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllCityTripsByGdzsCount(LocalDateTime start, LocalDateTime end, int gdzsCount,
                                                     int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllTripsByGarrisonAndGdzsCount(LocalDateTime start, LocalDateTime end, String garrison, int gdzsCount,
                                                            int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllCityTripsByGdzsCountGraterThen(LocalDateTime start, LocalDateTime end, int gdzsCount,
                                                               int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllCityTripsByWaterBarrelsCount(LocalDateTime start, LocalDateTime end, int barrelsCount,
                                                             int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllTripsByGarrisonAndGdzsCountGraiterThen(LocalDateTime start, LocalDateTime end, String garrison,
                                                                       int gdzsCount, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllCityTripsByWaterBarrelsCountGraterThen(LocalDateTime start, LocalDateTime end, int barrelsCount,
                                                                       int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllTripsByGarrisonAndWaterBarrelsCount(LocalDateTime start, LocalDateTime end, String garrison,
                                                                    int barrelsCount, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllTripsByGarrisonAndWaterBarrelsCountGraiterThen(LocalDateTime start, LocalDateTime end, String garrison,
                                                                               int barrelsCount, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllRegionTripsByFireRank(LocalDateTime start, LocalDateTime end, String fireRank, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllRegionTripsByTripCategoryAndDied(LocalDateTime start, LocalDateTime end, String category,
                                                                 int died, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllRegionTripsByTripCategoryAndDiedGraterThen(LocalDateTime start, LocalDateTime end, String category,
                                                                           int died, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllRegionTripsByFireObject(LocalDateTime start, LocalDateTime end, String fireObject, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllRegionTripsByTripCategoryAndInjured(LocalDateTime start, LocalDateTime end, String category,
                                                                    int injured, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllRegionTripsByTripCategoryAndInjuredGraterThen(LocalDateTime start, LocalDateTime end,
                                                                              String category, int injured, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllRegionTripsByTripCategory(LocalDateTime start, LocalDateTime end, String category, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllRegionTripsByGdzsCount(LocalDateTime start, LocalDateTime end, int gdzsCount, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllRegionTripsByGdzsCountGraterThen(LocalDateTime start, LocalDateTime end, int gdzsCount, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllRegionTripsByWaterBarrelsCount(LocalDateTime start, LocalDateTime end, int barrelsCount, int pageNumber, int tripsCount);

    Page<EmergencyTrips> findAllRegionTripsByWaterBarrelsCountGraterThen(LocalDateTime start, LocalDateTime end, int barrelsCount,
                                                                         int pageNumber, int tripsCount);

    FireStatisticInfo getCityStatisticInfo(LocalDate start, LocalDate end);

    List<FireStatisticCause> getCityStatisticCause(LocalDate start, LocalDate end);

    List<FireStatisticObject> getCityStatisticObject(LocalDate start, LocalDate end);

    FireStatisticInfo getRegionStatisticInfo(LocalDate start, LocalDate end);

    List<FireStatisticCause> getRegionStatisticCause(LocalDate start, LocalDate end);

    List<FireStatisticObject> getRegionStatisticObject(LocalDate start, LocalDate end);

    List<CarTypeInfo> getCarTypeInfo(int emergencyTripId, String division);

    List<StuffInfo> getStuffCount(int emergencyTripId);

    List<EmergencyTrips> findAllDutyTrips(LocalDate start, LocalDate end);

    CarsAndStuffCount getCarsAndStuffCount(LocalDate start, LocalDate end);
}

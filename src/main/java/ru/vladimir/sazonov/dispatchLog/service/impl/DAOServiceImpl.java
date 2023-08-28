package ru.vladimir.sazonov.dispatchLog.service.impl;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladimir.sazonov.dispatchLog.model.*;
import ru.vladimir.sazonov.dispatchLog.model.categories.*;
import ru.vladimir.sazonov.dispatchLog.model.statistic.*;
import ru.vladimir.sazonov.dispatchLog.repositories.*;
import ru.vladimir.sazonov.dispatchLog.service.DAOService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

@Service
@Transactional
@Data
public class DAOServiceImpl implements DAOService {

    private List<String> citySettlementsList = List.of("г.Магадан", "п.Уптар", "п.Сокол");
    private List<String> regionSettlementsList;

    //Repositories
    private AlarmDivisionWorkRepository alarmDivisionWorkRepository;
    private CarTypeRepository carTypeRepository;
    private DivisionsRepository divisionsRepository;
    private DutyGuardRepository dutyGuardRepository;
    private EmergencyTripsCategoriesRepository emergencyTripsCategoriesRepository;
    private EmergencyTripsRepository emergencyTripsRepository;
    private FioCuksRepository fioCuksRepository;
    private FioDispatcherRepository fioDispatcherRepository;
    private FioSptRepository fioSptRepository;
    private FireObjectRepository fireObjectRepository;
    private GarrisonRepository garrisonRepository;
    private NightStayObjectRepository nightStayObjectRepository;
    private NonAlarmDivisionWorkRepository nonAlarmDivisionWorkRepository;
    private NonEmergencyTripsCategoriesRepository nonEmergencyTripsCategoriesRepository;
    private NonEmergencyTripsRepository nonEmergencyTripsRepository;
    private PositionsWasReportedRepository positionsWasReportedRepository;
    private PositionsRtpRepository positionsRtpRepository;
    private RtpRepository rtpRepository;
    private SettlementsRepository settlementsRepository;
    private ToWhomWasReportedRepository toWhomReportedRepository;
    private WaterSourceRepository waterSourceRepository;
    private WaterSupplyRepository waterSupplyRepository;
    private WhoExtinguishedRepository whoExtinguishedRepository;
    private FireCauseRepository fireCauseRepository;

    @Autowired
    public DAOServiceImpl(AlarmDivisionWorkRepository alarmDivisionWorkRepository,
                          CarTypeRepository carTypeRepository, DivisionsRepository divisionsRepository,
                          DutyGuardRepository dutyGuardRepository, EmergencyTripsCategoriesRepository emergencyTripsCategoriesRepository,
                          EmergencyTripsRepository emergencyTripsRepository, FioCuksRepository fioCuksRepository,
                          FioDispatcherRepository fioDispatcherRepository,
                          FioSptRepository fioSptRepository, FireObjectRepository fireObjectRepository, GarrisonRepository garrisonRepository,
                          NightStayObjectRepository nightStayObjectRepository, NonAlarmDivisionWorkRepository nonAlarmDivisionWorkRepository,
                          NonEmergencyTripsCategoriesRepository nonEmergencyTripsCategoriesRepository,
                          NonEmergencyTripsRepository nonEmergencyTripsRepository, PositionsWasReportedRepository positionsWasReportedRepository,
                          PositionsRtpRepository positionsRtpRepository, RtpRepository rtpRepository, SettlementsRepository settlementsRepository,
                          ToWhomWasReportedRepository toWhomReportedRepository, WaterSourceRepository waterSourceRepository,
                          WaterSupplyRepository waterSupplyRepository, WhoExtinguishedRepository whoExtinguishedRepository, FireCauseRepository fireCauseRepository) {
        this.alarmDivisionWorkRepository = alarmDivisionWorkRepository;
        this.carTypeRepository = carTypeRepository;
        this.divisionsRepository = divisionsRepository;
        this.dutyGuardRepository = dutyGuardRepository;
        this.emergencyTripsCategoriesRepository = emergencyTripsCategoriesRepository;
        this.emergencyTripsRepository = emergencyTripsRepository;
        this.fioCuksRepository = fioCuksRepository;
        this.fioDispatcherRepository = fioDispatcherRepository;
        this.fioSptRepository = fioSptRepository;
        this.fireObjectRepository = fireObjectRepository;
        this.garrisonRepository = garrisonRepository;
        this.nightStayObjectRepository = nightStayObjectRepository;
        this.nonAlarmDivisionWorkRepository = nonAlarmDivisionWorkRepository;
        this.nonEmergencyTripsCategoriesRepository = nonEmergencyTripsCategoriesRepository;
        this.nonEmergencyTripsRepository = nonEmergencyTripsRepository;
        this.positionsWasReportedRepository = positionsWasReportedRepository;
        this.positionsRtpRepository = positionsRtpRepository;
        this.rtpRepository = rtpRepository;
        this.settlementsRepository = settlementsRepository;
        this.toWhomReportedRepository = toWhomReportedRepository;
        this.waterSourceRepository = waterSourceRepository;
        this.waterSupplyRepository = waterSupplyRepository;
        this.whoExtinguishedRepository = whoExtinguishedRepository;
        this.fireCauseRepository = fireCauseRepository;
    }

    public Map<String, List<String>> getGarrisonSettlementsMap() {
        return garrisonRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Garrison::getName,
                        g -> g.getSettlementsList().stream().map(Settlements::getName).toList()));
    }

    public Map<String, List<String>> getSettlementDivisionsMap() {
        return settlementsRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Settlements::getName,
                        s -> s.getDivisionsList().stream().map(Divisions::getName).toList()));
    }

    @Override
    public Map<String, List<String>> getCategoriesMap() {
        Map<String, List<String>> resultMap = new HashMap<>();
        resultMap.put("carTypesList", carTypeRepository.findAll().stream().map(CarType::getName).sorted().toList());
        resultMap.put("emergencyTripsCategoriesList", emergencyTripsCategoriesRepository.findAll().stream().map(EmergencyTripsCategories::getName).toList());
        resultMap.put("fioCuksList", fioCuksRepository.findAll().stream().map(FioCuks::getName).toList());
        resultMap.put("fioDispatcherList", fioDispatcherRepository.findAll().stream().map(FioDispatcher::getName).toList());
        resultMap.put("fioSptList", fioSptRepository.findAll().stream().map(FioSpt::getName).toList());
        resultMap.put("fireObjectsList", fireObjectRepository.findAll().stream().map(FireObject::getName).toList());
        resultMap.put("nonEmergencyTripsCategoriesList", nonEmergencyTripsCategoriesRepository.findAll().stream().map(NonEmergencyTripsCategories::getName).toList());
        resultMap.put("positionsRtpList", positionsRtpRepository.findAll().stream().map(PositionsRtp::getName).toList());
        resultMap.put("positionsWasReportedList", positionsWasReportedRepository.findAll().stream().map(PositionsWasReported::getName).toList());
        resultMap.put("waterSourceList", waterSourceRepository.findAll().stream().map(WaterSource::getName).toList());
        resultMap.put("waterSupplyList", waterSupplyRepository.findAll().stream().map(WaterSupply::getName).toList());
        resultMap.put("whoExtinguishedList", whoExtinguishedRepository.findAll().stream().map(WhoExtinguished::getName).toList());
        resultMap.put("fireCauseList", fireCauseRepository.findAll().stream().map(FireCause::getName).toList());
        List<String> divisionsCityList = new ArrayList<>();
        List<String> divisionsRegionsList = new ArrayList<>();
        regionSettlementsList = settlementsRepository.findAll()
                .stream()
                .filter(s -> {
                    String name = s.getName();
                    return !name.equals("г.Магадан") && !name.equals("п.Уптар") && !name.equals("п.Сокол");
                })
                .map(Settlements::getName)
                .toList();
        divisionsRepository.findAll()
                .forEach((div -> {
                    String divName = div.getName();
                    String settlement = div.getSettlement().getName();
                    if (settlement.equals("г.Магадан") || settlement.equals("п.Уптар")
                            || settlement.equals("п.Сокол"))
                        divisionsCityList.add(divName);
                    else divisionsRegionsList.add(divName);
                }));
        List<String> divisionsCityEmerTripsList = new ArrayList<>(divisionsCityList);
        divisionsCityEmerTripsList.add("ПЧ-13");
        divisionsCityEmerTripsList.add("ПЧ-11");
        divisionsCityEmerTripsList.add("ПЧ-9");
        ToIntFunction<String> func = s -> {
            int index = s.indexOf("-");
            if (index == -1) return 0;
            return Integer.parseInt(s.substring(++index, s.endsWith(")") ? s.lastIndexOf(" ") : s.length()));
        };
        divisionsCityList.sort(Comparator.comparingInt(func).thenComparing(Comparator.reverseOrder()));
        divisionsCityEmerTripsList.sort(Comparator.comparingInt(func).thenComparing(Comparator.reverseOrder()));
        divisionsRegionsList.sort(Comparator.comparingInt(func).thenComparing(Comparator.reverseOrder()));
        resultMap.put("divisionsCityList", divisionsCityList);
        resultMap.put("divisionsCityEmerTripsList", divisionsCityEmerTripsList);
        resultMap.put("divisionsRegionsList", divisionsRegionsList);
        resultMap.put("regionSettlementsList", regionSettlementsList);
        return resultMap;
    }

    @Override
    public DutyGuard saveDutyGuard(DutyGuard dutyGuard) {
        return dutyGuardRepository.save(dutyGuard);
    }

    @Override
    @Transactional
    public EmergencyTrips saveOrUpdateEmergencyTrip(EmergencyTrips emergencyTrip, Set<AlarmDivisionWork> workSet,
                                                    Set<Rtp> rtpSet, Set<ToWhomWasReported> reportedSet) {
        if (!workSet.isEmpty()) alarmDivisionWorkRepository.deleteAll(workSet);
        if (!rtpSet.isEmpty()) rtpRepository.deleteAll(rtpSet);
        if (!reportedSet.isEmpty()) toWhomReportedRepository.deleteAll(reportedSet);
        return emergencyTripsRepository.save(emergencyTrip);
    }

    @Override
    @Transactional
    public NonEmergencyTrips saveOrUpdateNonEmergencyTrip(NonEmergencyTrips nonEmergencyTrip, Set<NonAlarmDivisionWork> workSet) {
        if (!workSet.isEmpty()) nonAlarmDivisionWorkRepository.deleteAll(workSet);
        return nonEmergencyTripsRepository.save(nonEmergencyTrip);
    }

    @Override
    public Page<EmergencyTrips> getCityEmergencyTripsPage(int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllBySettlementNameIn(citySettlementsList,
                PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> getRegionEmergencyTripsPage(int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllBySettlementNameIn(regionSettlementsList,
                PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<NonEmergencyTrips> getCityNonEmergencyTripsPage(int pageNumber, int tripsCount) {
        return nonEmergencyTripsRepository.findAllBySettlementNameIn(citySettlementsList,
                PageRequest.of(pageNumber, tripsCount, Sort.by("dateTime").descending()));
    }

    @Override
    public Page<NonEmergencyTrips> getRegionNonEmergencyTripsPage(int pageNumber, int tripsCount) {
        return nonEmergencyTripsRepository.findAllBySettlementNameIn(regionSettlementsList,
                PageRequest.of(pageNumber, tripsCount, Sort.by("dateTime").descending()));
    }

    @Override
    @Transactional
    public void deleteNonEmergencyTrip(NonEmergencyTrips nonEmergencyTrip, Set<NonAlarmDivisionWork> workSet) {
        if (!workSet.isEmpty()) nonAlarmDivisionWorkRepository.deleteAll(workSet);
        nonEmergencyTripsRepository.delete(nonEmergencyTrip);
    }

    @Override
    @Transactional
    public void deleteEmergencyTrip(EmergencyTrips emergencyTrip, Set<AlarmDivisionWork> workSet,
                                    Set<Rtp> rtpSet, Set<ToWhomWasReported> reportedSet) {
        if (!workSet.isEmpty()) alarmDivisionWorkRepository.deleteAll(workSet);
        if (!rtpSet.isEmpty()) rtpRepository.deleteAll(rtpSet);
        if (!reportedSet.isEmpty()) toWhomReportedRepository.deleteAll(reportedSet);
        emergencyTripsRepository.delete(emergencyTrip);
    }

    @Override
    public Page<EmergencyTrips> findAllCityTrips(LocalDateTime start, LocalDateTime end, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByDateTimeBetweenAndSettlementNameIn(start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                citySettlementsList, PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllRegionTrips(LocalDateTime start, LocalDateTime end, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByDateTimeBetweenAndSettlementNameIn(start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                regionSettlementsList, PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllRegionTripsByGarrison(LocalDateTime start, LocalDateTime end, String garrison, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByDateTimeBetweenAndSettlementGarrisonName(start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                garrison, PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<NonEmergencyTrips> findAllCityNonEmergencyTrips(LocalDateTime start, LocalDateTime end, int pageNumber, int tripsCount) {
        return nonEmergencyTripsRepository.findAllByDateTimeBetweenAndSettlementNameIn(start, end,
                citySettlementsList, PageRequest.of(pageNumber, tripsCount, Sort.by("date_time").descending()));
    }

    @Override
    public Page<NonEmergencyTrips> findAllRegionNonEmergencyTrips(LocalDateTime start, LocalDateTime end, int pageNumber, int tripsCount) {
        return nonEmergencyTripsRepository.findAllByDateTimeBetweenAndSettlementNameIn(start, end,
                regionSettlementsList, PageRequest.of(pageNumber, tripsCount, Sort.by("date_time").descending()));
    }

    @Override
    public Page<NonEmergencyTrips> findAllRegionNonEmergencyTripsByGarrison(LocalDateTime start, LocalDateTime end, String garrison, int pageNumber, int tripsCount) {
        return nonEmergencyTripsRepository.findAllByDateTimeBetweenAndSettlementGarrisonName(start, end,
                garrison, PageRequest.of(pageNumber, tripsCount, Sort.by("date_time").descending()));
    }

    @Override
    public Page<NonEmergencyTrips> findAllCityNonEmergencyTripsByCategory(
            LocalDateTime start, LocalDateTime end, String category, int pageNumber, int tripsCount) {
        return nonEmergencyTripsRepository.findAllByDateTimeBetweenAndSettlementNameInAndTripCategoryName(
                start, end, citySettlementsList, category,
                PageRequest.of(pageNumber, tripsCount, Sort.by("date_time").descending()));
    }

    @Override
    public Page<NonEmergencyTrips> findAllRegionNonEmergencyTripsByCategory(
            LocalDateTime start, LocalDateTime end, String category, int pageNumber, int tripsCount) {
        return nonEmergencyTripsRepository.findAllByDateTimeBetweenAndSettlementNameInAndTripCategoryName(
                start, end,
                regionSettlementsList, category, PageRequest.of(pageNumber, tripsCount, Sort.by("date_time").descending()));
    }

    @Override
    public Page<EmergencyTrips> findAllCityTripsByFireRank(LocalDateTime start, LocalDateTime end,
                                                           String fireRank, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByDateTimeBetweenAndSettlementNameInAndFireRank(
                start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(), citySettlementsList, fireRank,
                PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllRegionTripsByFireRank(LocalDateTime start, LocalDateTime end,
                                                             String fireRank, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByDateTimeBetweenAndSettlementNameInAndFireRank(
                start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(), regionSettlementsList, fireRank,
                PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllCityTripsByTripCategoryAndDied(LocalDateTime start, LocalDateTime end,
                                                                      String category, int died, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByDateTimeBetweenAndSettlementNameInAndTripCategoryNameAndDied(
                start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(), citySettlementsList, category, died,
                PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllRegionTripsByTripCategoryAndDied(LocalDateTime start, LocalDateTime end,
                                                                        String category, int died, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByDateTimeBetweenAndSettlementNameInAndTripCategoryNameAndDied(
                start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(), regionSettlementsList, category, died,
                PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllCityTripsByTripCategoryAndDiedGraterThen(LocalDateTime start, LocalDateTime end,
                                                                                String category, int died, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByDateTimeBetweenAndSettlementNameInAndTripCategoryNameAndDiedGreaterThan(
                start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(), citySettlementsList, category, died,
                PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllRegionTripsByTripCategoryAndDiedGraterThen(LocalDateTime start, LocalDateTime end,
                                                                                  String category, int died, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByDateTimeBetweenAndSettlementNameInAndTripCategoryNameAndDiedGreaterThan(
                start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(), regionSettlementsList, category, died,
                PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllCityTripsByFireObject(LocalDateTime start, LocalDateTime end,
                                                             String fireObject, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByDateTimeBetweenAndSettlementNameInAndFireObjectName(
                start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(), citySettlementsList, fireObject,
                PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllRegionTripsByFireObject(LocalDateTime start, LocalDateTime end,
                                                               String fireObject, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByDateTimeBetweenAndSettlementNameInAndFireObjectName(
                start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(), regionSettlementsList, fireObject,
                PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllCityTripsByTripCategoryAndInjured(LocalDateTime start, LocalDateTime end,
                                                                         String category, int injured, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByDateTimeBetweenAndSettlementNameInAndTripCategoryNameAndInjured(
                start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(), citySettlementsList, category, injured,
                PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllRegionTripsByTripCategoryAndInjured(LocalDateTime start, LocalDateTime end,
                                                                           String category, int injured, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByDateTimeBetweenAndSettlementNameInAndTripCategoryNameAndInjured(
                start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(), regionSettlementsList, category, injured,
                PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllCityTripsByTripCategoryAndInjuredGraterThen(LocalDateTime start, LocalDateTime end,
                                                                                   String category, int injured, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByDateTimeBetweenAndSettlementNameInAndTripCategoryNameAndInjuredGreaterThan(
                start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(), citySettlementsList, category, injured,
                PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllRegionTripsByTripCategoryAndInjuredGraterThen(LocalDateTime start, LocalDateTime end,
                                                                                     String category, int injured, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByDateTimeBetweenAndSettlementNameInAndTripCategoryNameAndInjuredGreaterThan(
                start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(), regionSettlementsList, category, injured,
                PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllCityTripsByTripCategory(LocalDateTime start, LocalDateTime end,
                                                               String category, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByDateTimeBetweenAndSettlementNameInAndTripCategoryName(
                start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(), citySettlementsList, category,
                PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public List<EmergencyTrips> findAllCityFireTrips(LocalDateTime start, LocalDateTime end) {
        return emergencyTripsRepository.findAllFireTrips(start.toLocalDate(), start.toLocalTime(),
                end.toLocalDate(), end.toLocalTime(), citySettlementsList);
    }

    @Override
    public List<EmergencyTrips> findAllRegionFireTrips(LocalDateTime start, LocalDateTime end) {
        return emergencyTripsRepository.findAllFireTrips(start.toLocalDate(), start.toLocalTime(),
                end.toLocalDate(), end.toLocalTime(), regionSettlementsList);
    }

    @Override
    public Page<EmergencyTrips> findAllRegionTripsByTripCategory(LocalDateTime start, LocalDateTime end,
                                                                 String category, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByDateTimeBetweenAndSettlementNameInAndTripCategoryName(
                start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(), regionSettlementsList, category,
                PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllTripsByTripCategoryAndDepartureArea(LocalDateTime start, LocalDateTime end,
                                                                           String category, String departureArea,
                                                                           int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByDateTimeBetweenAndTripCategoryNameAndDepartureAreaName(
                start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(), category, departureArea,
                PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllCityTripsByGdzsCount(LocalDateTime start, LocalDateTime end, int gdzsCount, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByGdzsCount(start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                citySettlementsList, gdzsCount, PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllRegionTripsByGdzsCount(LocalDateTime start, LocalDateTime end, int gdzsCount, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByGdzsCount(start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                regionSettlementsList, gdzsCount, PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllCityTripsByGdzsCountGraterThen(LocalDateTime start, LocalDateTime end,
                                                                      int gdzsCount, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByGdzsCountGraiterThen(start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                citySettlementsList, gdzsCount, PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllRegionTripsByGdzsCountGraterThen(LocalDateTime start, LocalDateTime end,
                                                                        int gdzsCount, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByGdzsCountGraiterThen(start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                regionSettlementsList, gdzsCount, PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllCityTripsByWaterBarrelsCount(LocalDateTime start, LocalDateTime end,
                                                                    int barrelsCount, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByWaterBarrelsCount(start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                citySettlementsList, barrelsCount, PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllRegionTripsByWaterBarrelsCount(LocalDateTime start, LocalDateTime end,
                                                                      int barrelsCount, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByWaterBarrelsCount(start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                regionSettlementsList, barrelsCount, PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllCityTripsByWaterBarrelsCountGraterThen(LocalDateTime start, LocalDateTime end,
                                                                              int barrelsCount, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByWaterBarrelsCountGraterThen(start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                citySettlementsList, barrelsCount, PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllRegionTripsByWaterBarrelsCountGraterThen(LocalDateTime start, LocalDateTime end,
                                                                                int barrelsCount, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByWaterBarrelsCountGraterThen(start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                regionSettlementsList, barrelsCount, PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<NonEmergencyTrips> findAllNonEmergencyTripsByGarrisonAndCategory(LocalDateTime start, LocalDateTime end, String garrison, String category, int pageNumber, int tripsCount) {
        return nonEmergencyTripsRepository.findAllByDateTimeBetweenAndSettlementGarrisonNameAndTripCategoryName(start, end,
                garrison, category, PageRequest.of(pageNumber, tripsCount, Sort.by("date_time").descending()));
    }

    @Override
    public Page<EmergencyTrips> findAllTripsByGarrisonAndFireRank(LocalDateTime start, LocalDateTime end, String garrison, String fireRank, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByDateTimeBetweenAndSettlementGarrisonNameAndFireRank(start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                garrison, fireRank, PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllTripsByGarrisonAndTripCategoryAndDied(LocalDateTime start, LocalDateTime end, String garrison, String category, int died, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByDateTimeBetweenAndSettlementGarrisonNameAndTripCategoryNameAndDied(start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                garrison, category, died, PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllTripsByGarrisonAndTripCategoryAndDiedGraterThen(LocalDateTime start, LocalDateTime end, String garrison, String category, int died, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByDateTimeBetweenAndSettlementGarrisonNameAndTripCategoryNameAndDiedGreaterThan(start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                garrison, category, died, PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllTripsByGarrisonAndFireObject(LocalDateTime start, LocalDateTime end, String garrison, String fireObject, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByDateTimeBetweenAndSettlementGarrisonNameAndFireObjectName(start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                garrison, fireObject, PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllTripsByGarrisonAndTripCategoryAndInjured(LocalDateTime start, LocalDateTime end, String garrison, String category, int injured, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByDateTimeBetweenAndSettlementGarrisonNameAndTripCategoryNameAndInjured(start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                garrison, category, injured, PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllTripsByGarrisonAndTripCategoryAndInjuredGraterThen(LocalDateTime start, LocalDateTime end, String garrison, String category, int injured, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByDateTimeBetweenAndSettlementGarrisonNameAndTripCategoryNameAndInjuredGreaterThan(start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                garrison, category, injured, PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllTripsByGarrisonAndTripCategory(LocalDateTime start, LocalDateTime end, String garrison, String category, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByGarrisonAndTripCategory(start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                garrison, category, PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllTripsByGarrisonAndGdzsCount(LocalDateTime start, LocalDateTime end, String garrison, int gdzsCount, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByGarrisonAndGdzsCount(start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                garrison, gdzsCount, PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllTripsByGarrisonAndGdzsCountGraiterThen(LocalDateTime start, LocalDateTime end, String garrison,
                                                                              int gdzsCount, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByGarrisonAndGdzsCountGraterThen(start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                garrison, gdzsCount, PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllTripsByGarrisonAndWaterBarrelsCount(LocalDateTime start, LocalDateTime end, String garrison,
                                                                           int barrelsCount, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByGarrisonAndWaterBarrelsCount(start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                garrison, barrelsCount, PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public Page<EmergencyTrips> findAllTripsByGarrisonAndWaterBarrelsCountGraiterThen(LocalDateTime start, LocalDateTime end, String garrison,
                                                                                      int barrelsCount, int pageNumber, int tripsCount) {
        return emergencyTripsRepository.findAllByGarrisonAndWaterBarrelsCountGraterThen(start.toLocalDate(), start.toLocalTime(), end.toLocalDate(), end.toLocalTime(),
                garrison, barrelsCount, PageRequest.of(pageNumber, tripsCount));
    }

    @Override
    public FireStatisticInfo getCityStatisticInfo(LocalDate start, LocalDate end) {
        return emergencyTripsRepository.getFireStaticInfo(start, end, citySettlementsList);
    }

    @Override
    public List<FireStatisticCause> getCityStatisticCause(LocalDate start, LocalDate end) {
        return emergencyTripsRepository.getFireStaticCause(start, end, citySettlementsList);
    }

    @Override
    public List<FireStatisticObject> getCityStatisticObject(LocalDate start, LocalDate end) {
        return emergencyTripsRepository.getFireStaticObject(start, end, citySettlementsList);
    }

    @Override
    public FireStatisticInfo getRegionStatisticInfo(LocalDate start, LocalDate end) {
        return emergencyTripsRepository.getFireStaticInfo(start, end, regionSettlementsList);
    }

    @Override
    public List<FireStatisticCause> getRegionStatisticCause(LocalDate start, LocalDate end) {
        return emergencyTripsRepository.getFireStaticCause(start, end, regionSettlementsList);
    }

    @Override
    public List<FireStatisticObject> getRegionStatisticObject(LocalDate start, LocalDate end) {
        return emergencyTripsRepository.getFireStaticObject(start, end, regionSettlementsList);
    }

    @Override
    public List<CarTypeInfo> getCarTypeInfo(int emergencyTripId, String division) {
        return alarmDivisionWorkRepository.getCarTypeInfo(emergencyTripId, division);
    }

    @Override
    public List<StuffInfo> getStuffCount(int emergencyTripId) {
        return alarmDivisionWorkRepository.getStuffCount(emergencyTripId);
    }

    @Override
    public List<EmergencyTrips> findAllDutyTrips(LocalDate start, LocalDate end) {
        return emergencyTripsRepository.findAllDutyTrips(start, end);
    }

    @Override
    public CarsAndStuffCount getCarsAndStuffCount(LocalDate start, LocalDate end) {
        return alarmDivisionWorkRepository.getCarsAndStuffCount(start, end);
    }
}

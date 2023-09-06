package ru.vladimir.sazonov.dispatchLog.service.impl;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vladimir.sazonov.dispatchLog.model.EmergencyTrips;
import ru.vladimir.sazonov.dispatchLog.model.statistic.FireStatisticCause;
import ru.vladimir.sazonov.dispatchLog.model.statistic.FireStatisticObject;
import ru.vladimir.sazonov.dispatchLog.model.statistic.StuffInfo;
import ru.vladimir.sazonov.dispatchLog.service.DAOService;
import ru.vladimir.sazonov.dispatchLog.service.PrintDutyTripsService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Data
public class PrintDutyTripsServiceImpl implements PrintDutyTripsService {
    
    @Autowired
    DAOService daoService;
    
    @Override
    public Map<String, Object> printDutyTrips(LocalDate date) {
        LocalDate end = date.plusDays(1);
        AtomicInteger countCityTrips = new AtomicInteger();
        List<EmergencyTrips> dutyTrips = daoService.findAllDutyTrips(date, end);
        TreeMap<EmergencyTrips, String> emergencyTripsMap = new TreeMap<>(
                Comparator.comparing(e -> ((EmergencyTrips) e).getSettlement().getGarrison().getName())
                        .thenComparing(e -> ((EmergencyTrips) e).getDateTime().toLocalDate())
                        .thenComparing(e -> {
                            LocalTime result = ((EmergencyTrips) e).getCheckOutTime();
                            return result == null ? ((EmergencyTrips) e).getMessageTime() : result;
                        })
                        .thenComparing(e -> ((EmergencyTrips) e).getTripCategory().getName()));
        Map<String, Integer> cityTripsByCategoryMap = new HashMap<>();
        dutyTrips.forEach(e -> {
            emergencyTripsMap.put(e, printEmergencyTrip(e));
            if (!e.getSettlement().getGarrison().getName().equals("Магаданский ПСГ")) return;
            cityTripsByCategoryMap.merge(e.getTripCategory().getName(), 1, (oV, nV) -> ++oV);
            countCityTrips.addAndGet(1);
        });
        Map<String, Object> modelAttributesMap = new HashMap<>();
        modelAttributesMap.put("date", date);
        modelAttributesMap.put("fireStatisticInfo", daoService.getCityStatisticInfo(date, end));
        modelAttributesMap.put("regionFireStatisticInfo", daoService.getRegionStatisticInfo(date, end));
        modelAttributesMap.put("fireStatisticCause", getFireCauseArray(daoService.getCityStatisticCause(date, end)));
        modelAttributesMap.put("regionFireStatisticCause", getFireCauseArray(daoService.getRegionStatisticCause(date, end)));
        modelAttributesMap.put("fireStatisticObject", getFireObjectArray(daoService.getCityStatisticObject(date, end)));
        modelAttributesMap.put("regionFireStatisticObject", getFireObjectArray(daoService.getRegionStatisticObject(date, end)));
        modelAttributesMap.put("emergencyTripsMap", emergencyTripsMap);
        modelAttributesMap.put("carsAndStuffCount", daoService.getCarsAndStuffCount(date, end));
        modelAttributesMap.put("cityTripsByCategoryMap", cityTripsByCategoryMap);
        modelAttributesMap.put("countCityTrips", countCityTrips);
        return modelAttributesMap;
    }

    private int[] getFireCauseArray(List<FireStatisticCause> fireCauseList) {
        int[] fireCauseArray = new int[7];
        fireCauseList.forEach(c -> {
            String cause = c.getCause();
            if (cause == null) fireCauseArray[5] += c.getCountOfFires();
            else switch (cause) {
                case "Поджог" -> fireCauseArray[0] += c.getCountOfFires();
                case "Электропроводка" -> fireCauseArray[1] += c.getCountOfFires();
                case "Печное оборудование" -> fireCauseArray[2] += c.getCountOfFires();
                case "Неосторожное обращение с огнём" -> fireCauseArray[3] += c.getCountOfFires();
                case "Шалости детей" -> fireCauseArray[4] += c.getCountOfFires();
                case "Устанавливается" -> fireCauseArray[5] += c.getCountOfFires();
                default -> fireCauseArray[6] += c.getCountOfFires();
            }
        });
        return fireCauseArray;
    }

    private int[] getFireObjectArray(List<FireStatisticObject> fireObjectList) {
        int[] fireObjectArray = new int[8];
        fireObjectList.forEach(c -> {
            String object = c.getFireObject();
            if (object == null) fireObjectArray[7] += c.getCountOfFires();
            else switch (object) {
                case "Жилой сектор" -> fireObjectArray[0] += c.getCountOfFires();
                case "Новостройка" -> fireObjectArray[1] += c.getCountOfFires();
                case "ЗДиС производственного назначения" -> fireObjectArray[2] += c.getCountOfFires();
                case "ЗДиС складского назначения" -> fireObjectArray[3] += c.getCountOfFires();
                case "Здания предприятий торговли" -> fireObjectArray[4] += c.getCountOfFires();
                case "Административные здания",
                        "Здания и помещения для временного пребывания людей",
                        "ЗДиС культурно-досуговой деятельности и религиозных обрядов",
                        "Объекты здравоохранения и социального обслуживания населения",
                        "Учебно-образовательные учреждения",
                        "Учреждения сервисного обслуживания населения" -> fireObjectArray[5] += c.getCountOfFires();
                case "ЗДиС сельскохозяйственного назначения" -> fireObjectArray[6] += c.getCountOfFires();
                default -> fireObjectArray[7] += c.getCountOfFires();
            }
        });
        return fireObjectArray;
    }

    private String printEmergencyTrip(EmergencyTrips emergencyTrip) {
        StringBuilder result = new StringBuilder("Первичное сообщение: ");
        result.append(emergencyTrip.getPrimaryMessage())
                .append("<br>")
                .append("Заявитель: ")
                .append(emergencyTrip.getApplicant())
                .append("<br>")
                .append("Принял(а) сообщение: ")
                .append(emergencyTrip.getWhoTook().getName())
                .append("<br>")
                .append(emergencyTrip.getDescript())
                .append("<br>")
                .append("Выезжали: ");
        int id = emergencyTrip.getId();
        Map<String, Integer> stuffMap = daoService.getStuffCount(id)
                .stream().collect(Collectors.toMap(StuffInfo::getDivision, StuffInfo::getCount));
        stuffMap.forEach((division, count) -> {
            result.append(division)
                    .append(", ");
            daoService.getCarTypeInfo(id, division).forEach(c ->
                    result.append(c.getCount())
                            .append(" ")
                            .append(c.getCarType())
                            .append(", ")
            );
            result.append(count)
                    .append(" чел. | ");
        });
        return result.toString();
    }
}

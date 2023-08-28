package ru.vladimir.sazonov.dispatchLog.controllers;

import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.vladimir.sazonov.dispatchLog.model.EmergencyTrips;
import ru.vladimir.sazonov.dispatchLog.model.statistic.FireStatisticCause;
import ru.vladimir.sazonov.dispatchLog.model.statistic.FireStatisticObject;
import ru.vladimir.sazonov.dispatchLog.model.statistic.StuffInfo;
import ru.vladimir.sazonov.dispatchLog.service.DAOService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class MainPageController {

    @Autowired
    ServletContext servletContext;

    @Autowired
    DAOService daoService;

    @GetMapping("/")
    public String initCategories() {
        if (servletContext.getAttribute("categoriesMap") != null
                && servletContext.getAttribute("garrisonSettlementsMap") != null
                && servletContext.getAttribute("settlementDivisionsMap") != null) return "index";
        servletContext.setAttribute("categoriesMap", daoService.getCategoriesMap());
        servletContext.setAttribute("garrisonSettlementsMap", daoService.getGarrisonSettlementsMap());
        servletContext.setAttribute("settlementDivisionsMap", daoService.getSettlementDivisionsMap());
        return "index";
    }

    @GetMapping("/printDutyTrips")
    public String printDutyTrips(@RequestParam(required = false) LocalDate date, Model model) {
        if (date == null) return "redirect:/";
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
        model.addAttribute("date", date);
        model.addAttribute("fireStatisticInfo", daoService.getCityStatisticInfo(date, end));
        model.addAttribute("regionFireStatisticInfo", daoService.getRegionStatisticInfo(date, end));
        model.addAttribute("fireStatisticCause", getFireCaseArray(daoService.getCityStatisticCause(date, end)));
        model.addAttribute("regionFireStatisticCause", getFireCaseArray(daoService.getRegionStatisticCause(date, end)));
        model.addAttribute("fireStatisticObject", getFireObjectArray(daoService.getCityStatisticObject(date, end)));
        model.addAttribute("regionFireStatisticObject", getFireObjectArray(daoService.getRegionStatisticObject(date, end)));
        model.addAttribute("emergencyTripsMap", emergencyTripsMap);
        model.addAttribute("carsAndStuffCount", daoService.getCarsAndStuffCount(date, end));
        model.addAttribute("cityTripsByCategoryMap", cityTripsByCategoryMap);
        model.addAttribute("countCityTrips", countCityTrips);
        return "DutyTrips";
    }

    private int[] getFireCaseArray(List<FireStatisticCause> fireCauseList) {
        int[] fireCaseArray = new int[7];
        fireCauseList.forEach(c -> {
            String cause = c.getCause();
            if (cause == null) fireCaseArray[5] += c.getCountOfFires();
            else switch (cause) {
                case "Поджог" -> fireCaseArray[0] += c.getCountOfFires();
                case "Электропроводка" -> fireCaseArray[1] += c.getCountOfFires();
                case "Печное оборудование" -> fireCaseArray[2] += c.getCountOfFires();
                case "Неосторожное обращение с огнём" -> fireCaseArray[3] += c.getCountOfFires();
                case "Шалости детей" -> fireCaseArray[4] += c.getCountOfFires();
                case "Устанавливается" -> fireCaseArray[5] += c.getCountOfFires();
                default -> fireCaseArray[6] += c.getCountOfFires();
            }
        });
        return fireCaseArray;
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

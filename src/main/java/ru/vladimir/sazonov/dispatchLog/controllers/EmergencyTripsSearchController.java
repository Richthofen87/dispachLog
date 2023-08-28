package ru.vladimir.sazonov.dispatchLog.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import ru.vladimir.sazonov.dispatchLog.model.AlarmDivisionWork;
import ru.vladimir.sazonov.dispatchLog.model.EmergencyTrips;
import ru.vladimir.sazonov.dispatchLog.model.Rtp;
import ru.vladimir.sazonov.dispatchLog.model.ToWhomWasReported;
import ru.vladimir.sazonov.dispatchLog.service.DAOService;
import ru.vladimir.sazonov.dispatchLog.service.PrintDescriptionService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@SessionAttributes({"emergencyTripEditSearch", "formNumber", "searchPage", "searchFormWrapper", "tripsPageSearch"})
@RequestMapping("/emergencyTripsCitySearch")
public class EmergencyTripsSearchController {

    @Autowired
    private DAOService daoService;
    @Autowired
    private PrintDescriptionService descriptionService;
    private final Set<AlarmDivisionWork> worksToDelete = new HashSet<>();
    private final Set<Rtp> rtpToDelete = new HashSet<>();
    private final Set<ToWhomWasReported> reportedToDelete = new HashSet<>();

    @GetMapping("/")
    public String startContent(@RequestParam(required = false) Integer pageNumber,
                               @RequestParam(defaultValue = "false") boolean refresh,
                               @RequestParam(required = false) boolean clearFormFields,
                               @RequestParam(required = false) boolean resetSearchResults,
                               Model model) {
        if (refresh) return "Поиск выездов по тревоге в городе";
        model.addAttribute("emergencyTripEditSearch", new EmergencyTrips());
        model.addAttribute("searchPage", false);
        worksToDelete.clear();
        rtpToDelete.clear();
        reportedToDelete.clear();
        if (clearFormFields) return "Поиск выездов по тревоге в городе";
        if (resetSearchResults) return "redirect:/emergencyTripsCitySearch/" + pageNumber;
        model.addAttribute("formNumber", null);
        model.addAttribute("tripsPageSearch", null);
        model.addAttribute("searchFormWrapper", getForms());
        if (pageNumber == null) return "Поиск выездов по тревоге в городе";
        return "redirect:/emergencyTripsCitySearch/" + pageNumber;
    }

    @GetMapping("/record")
    public String getPageRecord(@ModelAttribute(name = "tripsPageSearch") Page<EmergencyTrips> tripsPageSearch,
                                @RequestParam int tripIndex, Model model) {
        model.addAttribute("emergencyTripEditSearch", tripsPageSearch.getContent().get(tripIndex));
        model.addAttribute("searchPage", true);
        worksToDelete.clear();
        rtpToDelete.clear();
        reportedToDelete.clear();
        return "Поиск выездов по тревоге в городе";
    }

    @PostMapping("/search")
    public String getPage(@ModelAttribute SearchFormWrapper searchFormWrapper,
                          @RequestParam int formNumber, Model model) {
        model.addAttribute("formNumber", formNumber);
        SearchForm searchForm = searchFormWrapper.searchFormList.get(formNumber);
        if ((searchForm.start == null || searchForm.end == null) ||
                (formNumber == 0 && searchForm.fireObject.isBlank()) ||
                (formNumber == 1 && searchForm.tripCategory.isBlank() && searchForm.departureArea.isBlank()) ||
                (formNumber == 2 && searchForm.fireRank.isBlank()) ||
                ((formNumber == 4 || formNumber == 5) && searchForm.tripCategory.isBlank()
                        && (searchForm.died.isEmpty() || searchForm.died.equals("0"))) ||
                ((formNumber == 6 || formNumber == 7) && searchForm.tripCategory.isBlank()
                        && (searchForm.injured.isEmpty() || searchForm.injured.equals("0"))) ||
                ((formNumber == 8 || formNumber == 9 || formNumber == 10 || formNumber == 11)
                        && (searchForm.count.isEmpty() || searchForm.count.equals("0")))) {
            return "redirect:/emergencyTripsCitySearch/";
        }
        return "redirect:/emergencyTripsCitySearch/0";
    }

    @GetMapping("/{pageNumber}")
    public String getPage(@ModelAttribute SearchFormWrapper searchFormWrapper,
                          @ModelAttribute(name = "formNumber") Integer formNumber,
                          @PathVariable int pageNumber, Model model) {
        SearchForm searchForm = searchFormWrapper.searchFormList.get(formNumber);
        if (searchForm.start != null) setPageAttr(searchForm, formNumber, pageNumber, model);
        return "Поиск выездов по тревоге в городе";
    }

    @PostMapping("/edit")
    public String editTrip(@Valid @ModelAttribute(name = "emergencyTripEditSearch") EmergencyTrips emergencyTrip,
                           BindingResult bindingResult,
                           @ModelAttribute SearchFormWrapper searchFormWrapper,
                           @ModelAttribute(name = "formNumber") int formNumber,
                           @ModelAttribute(name = "searchPage") boolean searchPage,
                           @RequestParam(name = "pageNumber") int pageNumber, Model model) {
        if (!searchPage) return "redirect:/emergencyTripsCitySearch/?refresh=true";
        if (bindingResult.hasErrors()) {
            setPageAttr(searchFormWrapper.searchFormList.get(formNumber), formNumber, pageNumber, model);
            return "Поиск выездов по тревоге в городе";
        }
        daoService.saveOrUpdateEmergencyTrip(emergencyTrip, worksToDelete, rtpToDelete, reportedToDelete);
        return "redirect:/emergencyTripsCitySearch/?resetSearchResults=true&pageNumber=" + pageNumber;
    }

    @PostMapping("/delete")
    public String deleteTrip(@Valid @ModelAttribute(name = "emergencyTripEditSearch") EmergencyTrips emergencyTrip,
                             BindingResult bindingResult, Model model,
                             @ModelAttribute SearchFormWrapper searchFormWrapper,
                             @ModelAttribute(name = "formNumber") int formNumber,
                             @ModelAttribute(name = "searchPage") boolean searchPage,
                             @RequestParam(name = "pageNumber") int pageNumber) {
        if (!searchPage) return "redirect:/emergencyTripsCitySearch/?refresh=true";
        if (bindingResult.hasErrors()) {
            setPageAttr(searchFormWrapper.searchFormList.get(formNumber), formNumber, pageNumber, model);
            return "Поиск выездов по тревоге в городе";
        }
        daoService.deleteEmergencyTrip(emergencyTrip, worksToDelete, rtpToDelete, reportedToDelete);
        return "redirect:/emergencyTripsCitySearch/?resetSearchResults=true&pageNumber=" + pageNumber;
    }

    @PostMapping("/addNewDivision")
    public String addNewDivision(@Valid @ModelAttribute(name = "emergencyTripEditSearch") EmergencyTrips emergencyTrip,
                                 BindingResult bindingResult,
                                 @ModelAttribute(name = "searchPage") boolean searchPage,
                                 @RequestParam(name = "indexDivision", required = false) Integer indexDivision) {
        if (!searchPage) return "redirect:/emergencyTripsCitySearch/?refresh=true";
        emergencyTrip.addAlarmDivisionWork(indexDivision);
        return "redirect:/emergencyTripsCitySearch/?refresh=true";
    }

    @PostMapping("/deleteDivisionEdit")
    public String deleteDivisionEdit(@Valid @ModelAttribute(name = "emergencyTripEditSearch") EmergencyTrips emergencyTrip,
                                     BindingResult bindingResult, @ModelAttribute(name = "searchPage") boolean searchPage,
                                     @RequestParam(name = "indexDivision", required = false) Integer indexDivision) {
        List<AlarmDivisionWork> list;
        if (!searchPage || (list = emergencyTrip.getAlarmDivisionWorkList()).isEmpty())
            return "redirect:/emergencyTripsCitySearch/?refresh=true";
        try {
            AlarmDivisionWork alarmDivisionWork = list.get(indexDivision == null ? list.size() - 1 : --indexDivision);
            list.remove(alarmDivisionWork);
            worksToDelete.add(alarmDivisionWork);
        } catch (IndexOutOfBoundsException ignore) {
        }
        return "redirect:/emergencyTripsCitySearch/?refresh=true";
    }

    @PostMapping("/addNewRtp")
    public String addNewRtpEdit(@Valid @ModelAttribute(name = "emergencyTripEditSearch") EmergencyTrips emergencyTrip,
                                BindingResult bindingResult, @ModelAttribute(name = "searchPage") boolean searchPage,
                                @RequestParam(name = "indexRtp", required = false) Integer indexRtp) {
        if (!searchPage) return "redirect:/emergencyTripsCitySearch/?refresh=true";
        emergencyTrip.addRtp(indexRtp);
        return "redirect:/emergencyTripsCitySearch/?refresh=true";
    }

    @PostMapping("/deleteRtpEdit")
    public String deleteRtpEdit(@Valid @ModelAttribute(name = "emergencyTripEditSearch") EmergencyTrips emergencyTrip,
                                BindingResult bindingResult, @ModelAttribute(name = "searchPage") boolean searchPage,
                                @RequestParam(name = "indexRtp", required = false) Integer indexRtp) {
        List<Rtp> list;
        if (!searchPage || (list = emergencyTrip.getRtpList()).isEmpty())
            return "redirect:/emergencyTripsCitySearch/?refresh=true";
        try {
            Rtp rtp = list.get(indexRtp == null ? list.size() - 1 : --indexRtp);
            list.remove(rtp);
            rtpToDelete.add(rtp);
        } catch (IndexOutOfBoundsException ignore) {
        }
        return "redirect:/emergencyTripsCitySearch/?refresh=true";
    }

    @PostMapping("/addReported")
    public String addReportedEdit(@Valid @ModelAttribute(name = "emergencyTripEditSearch") EmergencyTrips emergencyTrip,
                                  BindingResult bindingResult, @ModelAttribute(name = "searchPage") boolean searchPage,
                                  @RequestParam(name = "indexReported", required = false) Integer indexReported) {
        if (!searchPage) return "redirect:/emergencyTripsCitySearch/?refresh=true";
        emergencyTrip.addReported(indexReported);
        return "redirect:/emergencyTripsCitySearch/?refresh=true";
    }

    @PostMapping("/deleteReportedEdit")
    public String deleteReportedEdit(@Valid @ModelAttribute(name = "emergencyTripEditSearch") EmergencyTrips emergencyTrip,
                                     BindingResult bindingResult, @ModelAttribute(name = "searchPage") boolean searchPage,
                                     @RequestParam(name = "indexReported", required = false) Integer indexReported) {
        List<ToWhomWasReported> list;
        if (!searchPage || (list = emergencyTrip.getToWhomWasReportedList()).isEmpty())
            return "redirect:/emergencyTripsCitySearch/?refresh=true";
        try {
            ToWhomWasReported reported = list.get(indexReported == null ? list.size() - 1 : --indexReported);
            list.remove(reported);
            reportedToDelete.add(reported);
        } catch (IndexOutOfBoundsException ignore) {
        }
        return "redirect:/emergencyTripsCitySearch/?refresh=true";
    }

    @PostMapping("/saveDescription")
    private String saveDescription(@Valid @ModelAttribute(name = "emergencyTripEditSearch") EmergencyTrips emergencyTrip,
                                   BindingResult bindingResult, @ModelAttribute(name = "searchPage") boolean searchPage) {
        if (!bindingResult.hasErrors() && searchPage)
            new Thread(() -> descriptionService.printEmergencyTripDesc(emergencyTrip)).start();
        return "redirect:/emergencyTripsCitySearch/?refresh=true";
    }

    @PostMapping("/saveAllDescriptions")
    private String saveAllDescriptions(Model model) {
        Page<EmergencyTrips> emergencyTripsPage = (Page<EmergencyTrips>) model.getAttribute("tripsPageSearch");
        if (emergencyTripsPage != null && !emergencyTripsPage.isEmpty())
            new Thread(() -> emergencyTripsPage.forEach(trip -> descriptionService.printEmergencyTripDesc(trip))).start();
        return "redirect:/emergencyTripsCitySearch/?refresh=true";
    }

    @PostMapping("/saveTripsList")
    private String saveTripsList(@RequestParam(required = false) LocalDateTime start, @RequestParam(required = false) LocalDateTime end) {
        if (start != null && end != null) {
            List<EmergencyTrips> emergencyTripsList = daoService.findAllCityFireTrips(start, end);
            new Thread(() -> descriptionService.printTripsList(emergencyTripsList, start, end, "city")).start();
        }
        return "redirect:/emergencyTripsCitySearch/";
    }

    private void setPageAttr(SearchForm searchForm, int formNumber, int pageNumber, Model model) {
        Page<EmergencyTrips> page = null;
        switch (formNumber) {
            case 0 -> page = daoService.findAllCityTripsByFireObject(searchForm.start, searchForm.end,
                    searchForm.fireObject, pageNumber, 15);
            case 1 -> page = daoService.findAllTripsByTripCategoryAndDepartureArea(searchForm.start, searchForm.end,
                    searchForm.tripCategory, searchForm.departureArea, pageNumber, 15);
            case 2 ->
                    page = daoService.findAllCityTripsByFireRank(searchForm.start, searchForm.end, searchForm.fireRank,
                            pageNumber, 15);
            case 3 -> page = searchForm.tripCategory.isEmpty()
                    ? daoService.findAllCityTrips(searchForm.start, searchForm.end, pageNumber, 15)
                    : daoService.findAllCityTripsByTripCategory(searchForm.start, searchForm.end, searchForm.tripCategory,
                            pageNumber, 15);
            case 4 ->
                    page = daoService.findAllCityTripsByTripCategoryAndDied(searchForm.start, searchForm.end, searchForm.tripCategory,
                            Integer.parseInt(searchForm.died), pageNumber, 15);
            case 5 ->
                    page = daoService.findAllCityTripsByTripCategoryAndDiedGraterThen(searchForm.start, searchForm.end, searchForm.tripCategory,
                            Integer.parseInt(searchForm.died), pageNumber, 15);
            case 6 ->
                    page = daoService.findAllCityTripsByTripCategoryAndInjured(searchForm.start, searchForm.end, searchForm.tripCategory,
                            Integer.parseInt(searchForm.injured), pageNumber, 15);
            case 7 ->
                    page = daoService.findAllCityTripsByTripCategoryAndInjuredGraterThen(searchForm.start, searchForm.end, searchForm.tripCategory,
                            Integer.parseInt(searchForm.injured), pageNumber, 15);
            case 8 ->
                    page = daoService.findAllCityTripsByGdzsCount(searchForm.start, searchForm.end, Integer.parseInt(searchForm.count), pageNumber, 15);
            case 9 ->
                    page = daoService.findAllCityTripsByGdzsCountGraterThen(searchForm.start, searchForm.end, Integer.parseInt(searchForm.count), pageNumber, 15);
            case 10 ->
                    page = daoService.findAllCityTripsByWaterBarrelsCount(searchForm.start, searchForm.end, Integer.parseInt(searchForm.count), pageNumber, 15);
            case 11 ->
                    page = daoService.findAllCityTripsByWaterBarrelsCountGraterThen(searchForm.start, searchForm.end, Integer.parseInt(searchForm.count), pageNumber, 15);
        }
        model.addAttribute("tripsPageSearch", page);
    }

    private SearchFormWrapper getForms() {
        ArrayList<SearchForm> searchForms = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            searchForms.add(new SearchForm());
        }
        return new SearchFormWrapper(searchForms);
    }

    @ExceptionHandler(SQLException.class)
    private String handleException() {
        return "redirect:/emergencyTripsCitySearch/?refresh=true";
    }

    @ExceptionHandler(HttpSessionRequiredException.class)
    private String handleHttpSessionException() {
        return "redirect:/emergencyTripsCitySearch/0";
    }

    @Data
    @NoArgsConstructor
    private static class SearchForm {
        private LocalDateTime start;
        private LocalDateTime end;
        private String garrison;
        private String settlement;
        private String departureArea;
        private String tripCategory;
        private String fireObject;
        private String fireRank;
        private String died;
        private String injured;
        private String count;
    }

    @Data
    @AllArgsConstructor
    private static class SearchFormWrapper {
        private List<SearchForm> searchFormList;
    }
}

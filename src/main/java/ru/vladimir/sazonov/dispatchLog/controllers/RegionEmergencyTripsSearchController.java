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
import ru.vladimir.sazonov.dispatchLog.model.*;
import ru.vladimir.sazonov.dispatchLog.service.DAOService;
import ru.vladimir.sazonov.dispatchLog.service.PrintDescriptionService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@SessionAttributes({"regionEmergencyTripEditSearch", "regionFormNumber", "regionSearchPage", "regionSearchFormWrapper", "regionTripsPageSearch"})
@RequestMapping("/regionEmergencyTripsSearch")
public class RegionEmergencyTripsSearchController {

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
        if (refresh) return "Поиск выездов по тревоге в области";
        model.addAttribute("regionEmergencyTripEditSearch", new EmergencyTrips());
        model.addAttribute("regionSearchPage", false);
        worksToDelete.clear();
        rtpToDelete.clear();
        reportedToDelete.clear();
        if (clearFormFields) return "Поиск выездов по тревоге в области";
        if (resetSearchResults) return "redirect:/regionEmergencyTripsSearch/" + pageNumber;
        model.addAttribute("regionFormNumber", "");
        model.addAttribute("regionTripsPageSearch", null);
        model.addAttribute("regionSearchFormWrapper", getForms());
        if (pageNumber == null) return "Поиск выездов по тревоге в области";
        return "redirect:/regionEmergencyTripsSearch/" + pageNumber;
    }

    @GetMapping("/record")
    public String getPageRecord(@ModelAttribute(name = "regionTripsPageSearch") Page<EmergencyTrips> regionTripsPageSearch,
                                @RequestParam int tripIndex, Model model) {
        model.addAttribute("regionEmergencyTripEditSearch", regionTripsPageSearch.getContent().get(tripIndex));
        model.addAttribute("regionSearchPage", true);
        worksToDelete.clear();
        rtpToDelete.clear();
        reportedToDelete.clear();
        return "Поиск выездов по тревоге в области";
    }

    @PostMapping("/search")
    public String getPage(@ModelAttribute RegionSearchFormWrapper regionSearchFormWrapper,
                          @RequestParam(required = false) Integer regionFormNumber, Model model) {
        model.addAttribute("regionFormNumber", regionFormNumber);
        SearchForm searchForm = regionSearchFormWrapper.searchFormList.get(regionFormNumber);
        if ((searchForm.start == null || searchForm.end == null) ||
                (regionFormNumber == 0 && searchForm.fireObject.isBlank()) ||
                (regionFormNumber == 1 && searchForm.tripCategory.isBlank() && searchForm.departureArea.isBlank()) ||
                (regionFormNumber == 2 && searchForm.fireRank.isBlank()) ||
                ((regionFormNumber == 4 || regionFormNumber == 5) && searchForm.tripCategory.isBlank()
                        && (searchForm.died.isEmpty() || searchForm.died.equals("0"))) ||
                ((regionFormNumber == 6 || regionFormNumber == 7) && searchForm.tripCategory.isBlank()
                        && (searchForm.injured.isEmpty() || searchForm.injured.equals("0"))) ||
                ((regionFormNumber == 8 || regionFormNumber == 9 || regionFormNumber == 10 || regionFormNumber == 11)
                        && (searchForm.count.isEmpty() || searchForm.count.equals("0")))) {
            return "redirect:/regionEmergencyTripsSearch/";
        }
        return "redirect:/regionEmergencyTripsSearch/0";
    }

    @GetMapping("/{pageNumber}")
    public String getPage(@ModelAttribute RegionSearchFormWrapper regionSearchFormWrapper,
                          @ModelAttribute(name = "regionFormNumber") int regionFormNumber,
                          @PathVariable int pageNumber, Model model) {
        SearchForm searchForm = regionSearchFormWrapper.searchFormList.get(regionFormNumber);
        if (searchForm.start != null) setPageAttr(searchForm, regionFormNumber, pageNumber, model);
        return "Поиск выездов по тревоге в области";
    }

    @PostMapping("/edit")
    public String editTrip(@Valid @ModelAttribute(name = "regionEmergencyTripEditSearch") EmergencyTrips emergencyTrip,
                           BindingResult bindingResult,
                           @ModelAttribute RegionSearchFormWrapper regionSearchFormWrapper,
                           @ModelAttribute(name = "regionFormNumber") int regionFormNumber,
                           @ModelAttribute(name = "regionSearchPage") boolean regionSearchPage,
                           @RequestParam(name = "pageNumber") int pageNumber, Model model) {
        if (!regionSearchPage) return "redirect:/regionEmergencyTripsSearch/?refresh=true";
        if (bindingResult.hasErrors()) {
            System.out.println(emergencyTrip);
            setPageAttr(regionSearchFormWrapper.searchFormList.get(regionFormNumber), regionFormNumber, pageNumber, model);
            return "Поиск выездов по тревоге в области";
        }
        daoService.saveOrUpdateEmergencyTrip(emergencyTrip, worksToDelete, rtpToDelete, reportedToDelete);
        return "redirect:/regionEmergencyTripsSearch/?resetSearchResults=true&pageNumber=" + pageNumber;
    }

    @PostMapping("/delete")
    public String deleteTrip(@Valid @ModelAttribute(name = "regionEmergencyTripEditSearch") EmergencyTrips emergencyTrip,
                             BindingResult bindingResult, Model model,
                             @ModelAttribute RegionSearchFormWrapper regionSearchFormWrapper,
                             @ModelAttribute(name = "regionFormNumber") int regionFormNumber,
                             @ModelAttribute(name = "regionSearchPage") boolean regionSearchPage,
                             @RequestParam(name = "pageNumber") int pageNumber) {
        if (!regionSearchPage) return "redirect:/regionEmergencyTripsSearch/?refresh=true";
        if (bindingResult.hasErrors()) {
            setPageAttr(regionSearchFormWrapper.searchFormList.get(regionFormNumber), regionFormNumber, pageNumber, model);
            return "Поиск выездов по тревоге в области";
        }
        daoService.deleteEmergencyTrip(emergencyTrip, worksToDelete, rtpToDelete, reportedToDelete);
        return "redirect:/regionEmergencyTripsSearch/?resetSearchResults=true&pageNumber=" + pageNumber;
    }

    @PostMapping("/addNewDivisionEdit")
    public String addNewDivision(@Valid @ModelAttribute(name = "regionEmergencyTripEditSearch") EmergencyTrips emergencyTrip,
                                 BindingResult bindingResult, @ModelAttribute(name = "regionSearchPage") boolean regionSearchPage,
                                 @RequestParam(name = "indexDivision", required = false) Integer indexDivision) {
        if (!regionSearchPage) return "redirect:/regionEmergencyTripsSearch/?refresh=true";
        emergencyTrip.addAlarmDivisionWork(indexDivision);
        return "redirect:/regionEmergencyTripsSearch/?refresh=true";
    }

    @PostMapping("/deleteDivisionEdit")
    public String deleteDivisionEdit(@Valid @ModelAttribute(name = "regionEmergencyTripEditSearch") EmergencyTrips emergencyTrip,
                                     BindingResult bindingResult,
                                     @ModelAttribute(name = "regionSearchPage") boolean regionSearchPage,
                                     @RequestParam(name = "indexDivision", required = false) Integer indexDivision) {
        List<AlarmDivisionWork> list;
        if (!regionSearchPage || (list = emergencyTrip.getAlarmDivisionWorkList()).isEmpty())
            return "redirect:/regionEmergencyTripsSearch/?refresh=true";
        try {
            AlarmDivisionWork alarmDivisionWork = list.get(indexDivision == null ? list.size() - 1 : --indexDivision);
            list.remove(alarmDivisionWork);
            worksToDelete.add(alarmDivisionWork);
        } catch (IndexOutOfBoundsException ignore) {
        }
        return "redirect:/regionEmergencyTripsSearch/?refresh=true";
    }

    @PostMapping("/addNewRtpEdit")
    public String addNewRtpEdit(@Valid @ModelAttribute(name = "regionEmergencyTripEditSearch") EmergencyTrips emergencyTrip,
                                BindingResult bindingResult,
                                @ModelAttribute(name = "regionSearchPage") boolean regionSearchPage,
                                @RequestParam(name = "indexRtp", required = false) Integer indexRtp) {
        if (!regionSearchPage) return "redirect:/regionEmergencyTripsSearch/?refresh=true";
        emergencyTrip.addRtp(indexRtp);
        return "redirect:/regionEmergencyTripsSearch/?refresh=true";
    }

    @PostMapping("/deleteRtpEdit")
    public String deleteRtpEdit(@Valid @ModelAttribute(name = "regionEmergencyTripEditSearch") EmergencyTrips emergencyTrip,
                                BindingResult bindingResult,
                                @ModelAttribute(name = "regionSearchPage") boolean regionSearchPage,
                                @RequestParam(name = "indexRtp", required = false) Integer indexRtp) {
        List<Rtp> list;
        if (!regionSearchPage || (list = emergencyTrip.getRtpList()).isEmpty())
            return "redirect:/regionEmergencyTripsSearch/?refresh=true";
        try {
            Rtp rtp = list.get(indexRtp == null ? list.size() - 1 : --indexRtp);
            list.remove(rtp);
            rtpToDelete.add(rtp);
        } catch (IndexOutOfBoundsException ignore) {
        }
        return "redirect:/regionEmergencyTripsSearch/?refresh=true";
    }

    @PostMapping("/addReportedEdit")
    public String addReportedEdit(@Valid @ModelAttribute(name = "regionEmergencyTripEditSearch") EmergencyTrips emergencyTrip,
                                  BindingResult bindingResult,
                                  @ModelAttribute(name = "regionSearchPage") boolean regionSearchPage,
                                  @RequestParam(name = "indexReported", required = false) Integer indexReported) {
        if (!regionSearchPage) return "redirect:/regionEmergencyTripsSearch/?refresh=true";
        emergencyTrip.addReported(indexReported);
        return "redirect:/regionEmergencyTripsSearch/?refresh=true";
    }

    @PostMapping("/deleteReportedEdit")
    public String deleteReportedEdit(@Valid @ModelAttribute(name = "regionEmergencyTripEditSearch") EmergencyTrips emergencyTrip,
                                     BindingResult bindingResult,
                                     @ModelAttribute(name = "regionSearchPage") boolean regionSearchPage,
                                     @RequestParam(name = "indexReported", required = false) Integer indexReported) {
        List<ToWhomWasReported> list;
        if (!regionSearchPage || (list = emergencyTrip.getToWhomWasReportedList()).isEmpty())
            return "redirect:/regionEmergencyTripsSearch/?refresh=true";
        try {
            ToWhomWasReported reported = list.get(indexReported == null ? list.size() - 1 : --indexReported);
            list.remove(reported);
            reportedToDelete.add(reported);
        } catch (IndexOutOfBoundsException ignore) {
        }
        return "redirect:/regionEmergencyTripsSearch/?refresh=true";
    }

    @PostMapping("/saveDescription")
    private String saveDescription(@Valid @ModelAttribute(name = "regionEmergencyTripEditSearch") EmergencyTrips emergencyTrip,
                                   BindingResult bindingResult, @ModelAttribute(name = "regionSearchPage") boolean regionSearchPage) {
        if (!bindingResult.hasErrors() && regionSearchPage)
            new Thread(() -> descriptionService.printEmergencyTripDesc(emergencyTrip)).start();
        return "redirect:/regionEmergencyTripsSearch/?refresh=true";
    }

    @PostMapping("/saveAllDescriptions")
    private String saveAllDescriptions(Model model) {
        Page<EmergencyTrips> emergencyTripsPage = (Page<EmergencyTrips>) model.getAttribute("regionTripsPageSearch");
        if (emergencyTripsPage != null && !emergencyTripsPage.isEmpty())
            new Thread(() -> emergencyTripsPage.forEach(trip -> descriptionService.printEmergencyTripDesc(trip))).start();
        return "redirect:/regionEmergencyTripsSearch/?refresh=true";
    }

    @PostMapping("/saveTripsList")
    private String saveTripsList(@RequestParam(required = false) LocalDateTime start, @RequestParam(required = false) LocalDateTime end) {
        if (start != null && end != null) {
            List<EmergencyTrips> regionEmergencyTripsList = daoService.findAllRegionFireTrips(start, end);
            new Thread(() -> descriptionService.printTripsList(regionEmergencyTripsList, start, end, "region")).start();
        }
        return "redirect:/regionEmergencyTripsSearch/";
    }

    private void setPageAttr(SearchForm searchForm, int regionFormNumber, int pageNumber, Model model) {
        Page<EmergencyTrips> page = null;
        switch (regionFormNumber) {
            case 0 -> page = searchForm.getGarrison().isEmpty()
                    ? daoService.findAllRegionTripsByFireObject(searchForm.start, searchForm.end,
                    searchForm.fireObject, pageNumber, 15)
                    : daoService.findAllTripsByGarrisonAndFireObject(searchForm.start, searchForm.end, searchForm.garrison,
                    searchForm.fireObject, pageNumber, 15);
            case 1 -> page = daoService.findAllTripsByTripCategoryAndDepartureArea(searchForm.start, searchForm.end,
                    searchForm.tripCategory, searchForm.departureArea, pageNumber, 15);
            case 2 -> page = searchForm.getGarrison().isEmpty()
                    ? daoService.findAllRegionTripsByFireRank(searchForm.start, searchForm.end, searchForm.fireRank,
                    pageNumber, 15)
                    : daoService.findAllTripsByGarrisonAndFireRank(searchForm.start, searchForm.end, searchForm.garrison, searchForm.fireRank,
                    pageNumber, 15);
            case 3 -> page = searchForm.getGarrison().isEmpty()
                    ? (searchForm.tripCategory.isEmpty() ? daoService.findAllRegionTrips(searchForm.start, searchForm.end, pageNumber, 15)
                    : daoService.findAllRegionTripsByTripCategory(searchForm.start, searchForm.end, searchForm.tripCategory,
                    pageNumber, 15))
                    : (searchForm.tripCategory.isEmpty() ? daoService.findAllRegionTripsByGarrison(searchForm.start, searchForm.end, searchForm.garrison,
                    pageNumber, 15) : daoService.findAllTripsByGarrisonAndTripCategory(searchForm.start, searchForm.end, searchForm.garrison,
                    searchForm.tripCategory, pageNumber, 15));
            case 4 -> page = searchForm.getGarrison().isEmpty()
                    ? daoService.findAllRegionTripsByTripCategoryAndDied(searchForm.start, searchForm.end, searchForm.tripCategory,
                    Integer.parseInt(searchForm.died), pageNumber, 15)
                    : daoService.findAllTripsByGarrisonAndTripCategoryAndDied(searchForm.start, searchForm.end, searchForm.garrison,
                    searchForm.tripCategory, Integer.parseInt(searchForm.died), pageNumber, 15);
            case 5 -> page = searchForm.getGarrison().isEmpty()
                    ? daoService.findAllRegionTripsByTripCategoryAndDiedGraterThen(searchForm.start, searchForm.end, searchForm.tripCategory,
                    Integer.parseInt(searchForm.died), pageNumber, 15)
                    : daoService.findAllTripsByGarrisonAndTripCategoryAndDiedGraterThen(searchForm.start, searchForm.end, searchForm.garrison,
                    searchForm.tripCategory, Integer.parseInt(searchForm.died), pageNumber, 15);
            case 6 -> page = searchForm.getGarrison().isEmpty()
                    ? daoService.findAllRegionTripsByTripCategoryAndInjured(searchForm.start, searchForm.end, searchForm.tripCategory,
                    Integer.parseInt(searchForm.injured), pageNumber, 15)
                    : daoService.findAllTripsByGarrisonAndTripCategoryAndInjured(searchForm.start, searchForm.end, searchForm.garrison,
                    searchForm.tripCategory, Integer.parseInt(searchForm.injured), pageNumber, 15);
            case 7 -> page = searchForm.getGarrison().isEmpty()
                    ? daoService.findAllRegionTripsByTripCategoryAndInjuredGraterThen(searchForm.start, searchForm.end, searchForm.tripCategory,
                    Integer.parseInt(searchForm.injured), pageNumber, 15)
                    : daoService.findAllTripsByGarrisonAndTripCategoryAndInjuredGraterThen(searchForm.start, searchForm.end, searchForm.garrison,
                    searchForm.tripCategory, Integer.parseInt(searchForm.injured), pageNumber, 15);
            case 8 -> page = searchForm.getGarrison().isEmpty()
                    ? daoService.findAllRegionTripsByGdzsCount(searchForm.start, searchForm.end, Integer.parseInt(searchForm.count), pageNumber, 15)
                    : daoService.findAllTripsByGarrisonAndGdzsCount(searchForm.start, searchForm.end, searchForm.garrison,
                    Integer.parseInt(searchForm.count), pageNumber, 15);
            case 9 -> page = searchForm.getGarrison().isEmpty()
                    ? daoService.findAllRegionTripsByGdzsCountGraterThen(searchForm.start, searchForm.end, Integer.parseInt(searchForm.count), pageNumber, 15)
                    : daoService.findAllTripsByGarrisonAndGdzsCountGraiterThen(searchForm.start, searchForm.end, searchForm.garrison,
                    Integer.parseInt(searchForm.count), pageNumber, 15);
            case 10 -> page = searchForm.getGarrison().isEmpty()
                    ? daoService.findAllRegionTripsByWaterBarrelsCount(searchForm.start, searchForm.end, Integer.parseInt(searchForm.count),
                    pageNumber, 15)
                    : daoService.findAllTripsByGarrisonAndWaterBarrelsCount(searchForm.start, searchForm.end, searchForm.garrison,
                    Integer.parseInt(searchForm.count), pageNumber, 15);
            case 11 -> page = searchForm.getGarrison().isEmpty()
                    ? daoService.findAllRegionTripsByWaterBarrelsCountGraterThen(searchForm.start, searchForm.end, Integer.parseInt(searchForm.count),
                    pageNumber, 15)
                    : daoService.findAllTripsByGarrisonAndWaterBarrelsCountGraiterThen(searchForm.start, searchForm.end, searchForm.garrison,
                    Integer.parseInt(searchForm.count), pageNumber, 15);
        }
        model.addAttribute("regionTripsPageSearch", page);
    }

    private RegionSearchFormWrapper getForms() {
        ArrayList<SearchForm> searchForms = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            searchForms.add(new SearchForm());
        }
        return new RegionSearchFormWrapper(searchForms);
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

    @ExceptionHandler(SQLException.class)
    private String handleException() {
        return "redirect:/regionEmergencyTripsSearch/?refresh=true";
    }

    @ExceptionHandler(HttpSessionRequiredException.class)
    private String handleHttpSessionException() {
        return "redirect:/regionEmergencyTripsSearch/0";
    }

    @Data
    @AllArgsConstructor
    public static class RegionSearchFormWrapper {
        private List<SearchForm> searchFormList;
    }
}

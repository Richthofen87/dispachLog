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
import ru.vladimir.sazonov.dispatchLog.model.NonAlarmDivisionWork;
import ru.vladimir.sazonov.dispatchLog.model.NonEmergencyTrips;
import ru.vladimir.sazonov.dispatchLog.service.DAOService;
import ru.vladimir.sazonov.dispatchLog.service.impl.PrintDescriptionServiceImpl;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@SessionAttributes({"regionNonEmergencyTripSearch", "regionNonEmergencySearchForm", "regionNonEmergencySearchPage", "regionNonEmergencyTripsPageSearch"})
@RequestMapping("/regionNonEmergencyTripsSearch")
public class RegionNonEmergencyTripsSearchController {

    @Autowired
    private DAOService daoService;
    private final Set<NonAlarmDivisionWork> worksToDelete = new HashSet<>();

    @GetMapping("/")
    public String startContent(@RequestParam(required = false) Integer pageNumber,
                               @RequestParam(defaultValue = "false") boolean addFields,
                               @RequestParam(required = false) boolean resetSearchResults,
                               @RequestParam(required = false) boolean clearFormFields,
                               @RequestParam(required = false) boolean resetParamsAndSearchResults,
                               Model model) {
        if (addFields) return "Поиск выездов на занятия в области";
        model.addAttribute("regionNonEmergencyTripSearch", new NonEmergencyTrips());
        model.addAttribute("regionNonEmergencySearchPage", false);
        worksToDelete.clear();
        if (clearFormFields) return "Поиск выездов на занятия в области";
        if (resetSearchResults) return "redirect:/regionNonEmergencyTripsSearch/" + pageNumber;
        if (model.getAttribute("regionNonEmergencySearchForm") == null || resetParamsAndSearchResults)
            model.addAttribute("regionNonEmergencySearchForm", new NonEmergencySearchForm());
        if (pageNumber == null) {
            model.addAttribute("regionNonEmergencyTripsPageSearch", null);
            return "Поиск выездов на занятия в области";
        }
        return "redirect:/regionNonEmergencyTripsSearch/" + pageNumber;
    }

    @GetMapping("/record")
    public String getPageRecord(@ModelAttribute(name = "regionNonEmergencyTripsPageSearch") Page<NonEmergencyTrips> regionNonEmergencyTripsPageSearch,
                                @RequestParam int tripIndex, Model model) {
        model.addAttribute("regionNonEmergencyTripSearch", regionNonEmergencyTripsPageSearch.getContent().get(tripIndex));
        model.addAttribute("regionNonEmergencySearchPage", true);
        worksToDelete.clear();
        return "Поиск выездов на занятия в области";
    }

    @PostMapping("/search")
    public String getPage(@ModelAttribute(name = "regionNonEmergencySearchForm") NonEmergencySearchForm regionNonEmergencySearchForm, Model model) {
        if (regionNonEmergencySearchForm.start == null || regionNonEmergencySearchForm.end == null)
            return "redirect:/regionNonEmergencyTripsSearch/";
        return "redirect:/regionNonEmergencyTripsSearch/0";
    }

    @GetMapping("/{pageNumber}")
    public String getPage(@PathVariable int pageNumber, Model model,
                          @RequestParam(required = false) Integer tripIndex) {
        NonEmergencySearchForm nonEmergencySearchForm = (NonEmergencySearchForm) model.getAttribute("regionNonEmergencySearchForm");
        Page<NonEmergencyTrips> page;
        if (nonEmergencySearchForm.start != null) page = setPageAttr(pageNumber, model);
        else return "Поиск выездов на занятия в городе";
        if (tripIndex != null) {
            model.addAttribute("regionNonEmergencyTripSearch", page.getContent().get(tripIndex));
            model.addAttribute("regionNonEmergencySearchPage", true);
        }
        return "Поиск выездов на занятия в области";
    }

    @PostMapping("/edit")
    public String editTrip(@Valid @ModelAttribute(name = "regionNonEmergencyTripSearch") NonEmergencyTrips nonEmergencyTripEdit,
                           BindingResult bindingResult, @RequestParam(name = "pageNumber") int pageNumber,
                           @ModelAttribute(name = "regionNonEmergencySearchPage") boolean regionNonEmergencySearchPage, Model model) {
        if (!regionNonEmergencySearchPage) return "redirect:/regionNonEmergencyTripsSearch/?addFields=true";
        if (bindingResult.hasErrors()) {
            setPageAttr(pageNumber, model);
            return "Поиск выездов на занятия в области";
        }
        daoService.saveOrUpdateNonEmergencyTrip(nonEmergencyTripEdit, worksToDelete);
        return "redirect:/regionNonEmergencyTripsSearch/?resetSearchResults=true&pageNumber=" + pageNumber;
    }

    @PostMapping("/delete")
    public String deleteTrip(@Valid @ModelAttribute(name = "regionNonEmergencyTripSearch") NonEmergencyTrips nonEmergencyTrip,
                             BindingResult bindingResult,
                             @ModelAttribute(name = "regionNonEmergencySearchPage") boolean regionNonEmergencySearchPage,
                             @RequestParam(name = "pageNumber") int pageNumber, Model model) {
        if (!regionNonEmergencySearchPage) return "redirect:/regionNonEmergencyTripsSearch/?addFields=true";
        if (bindingResult.hasErrors()) {
            setPageAttr(pageNumber, model);
            return "Поиск выездов на занятия в области";
        }
        daoService.deleteNonEmergencyTrip(nonEmergencyTrip, worksToDelete);
        return "redirect:/regionNonEmergencyTripsSearch/?resetSearchResults=true&pageNumber=" + pageNumber;
    }

    @PostMapping("/addNewDivisionEdit")
    public String addNewDivision(@Valid @ModelAttribute(name = "regionNonEmergencyTripSearch") NonEmergencyTrips nonEmergencyTrip,
                                 BindingResult bindingResult,
                                 @ModelAttribute(name = "regionNonEmergencySearchPage") boolean regionNonEmergencySearchPage,
                                 @RequestParam(name = "index", required = false) Integer indexDivision) {
        if (!regionNonEmergencySearchPage) return "redirect:/regionNonEmergencyTripsSearch/?addFields=true";
        nonEmergencyTrip.addNonAlarmDivisionWork(indexDivision);
        return "redirect:/regionNonEmergencyTripsCitySearch/?addFields=true";
    }

    @PostMapping("/deleteDivisionEdit")
    public String deleteDivisionEdit(@Valid @ModelAttribute(name = "regionNonEmergencyTripSearch") NonEmergencyTrips nonEmergencyTrip,
                                     BindingResult bindingResult,
                                     @ModelAttribute(name = "regionNonEmergencySearchPage") boolean regionNonEmergencySearchPage,
                                     @RequestParam(name = "index", required = false) Integer indexDivision) {
        List<NonAlarmDivisionWork> list;
        if (!regionNonEmergencySearchPage || (list = nonEmergencyTrip.getNonAlarmDivisionWorkList()).isEmpty())
            return "redirect:/regionNonEmergencyTripsSearch/?addFields=true";
        try {
            NonAlarmDivisionWork nonAlarmDivisionWork = list.get(indexDivision == null ? list.size() - 1 : --indexDivision);
            list.remove(nonAlarmDivisionWork);
            worksToDelete.add(nonAlarmDivisionWork);
        } catch (IndexOutOfBoundsException ignore) {
        }
        return "redirect:/regionNonEmergencyTripsSearch/?addFields=true";
    }

    private Page<NonEmergencyTrips> setPageAttr(int pageNumber, Model model) {
        NonEmergencySearchForm regionNonEmergencySearchForm = (NonEmergencySearchForm) model.getAttribute("regionNonEmergencySearchForm");
        Page<NonEmergencyTrips> page = regionNonEmergencySearchForm.garrison.isEmpty()
                ? (regionNonEmergencySearchForm.tripCategory.isEmpty()
                ? daoService.findAllRegionNonEmergencyTrips(regionNonEmergencySearchForm.start, regionNonEmergencySearchForm.end, pageNumber, 20)
                : daoService.findAllRegionNonEmergencyTripsByCategory(regionNonEmergencySearchForm.start, regionNonEmergencySearchForm.end,
                regionNonEmergencySearchForm.tripCategory, pageNumber, 20))
                : (regionNonEmergencySearchForm.tripCategory.isEmpty()
                ? daoService.findAllRegionNonEmergencyTripsByGarrison(regionNonEmergencySearchForm.start, regionNonEmergencySearchForm.end,
                regionNonEmergencySearchForm.garrison, pageNumber, 20)
                : daoService.findAllNonEmergencyTripsByGarrisonAndCategory(regionNonEmergencySearchForm.start, regionNonEmergencySearchForm.end,
                regionNonEmergencySearchForm.garrison, regionNonEmergencySearchForm.tripCategory, pageNumber, 20));
        model.addAttribute("regionNonEmergencyTripsPageSearch", page);
        return page;
    }

    @ExceptionHandler(SQLException.class)
    private String handleException() {
        return "redirect:/regionNonEmergencyTripsSearch/?addFields=true";
    }

    @ExceptionHandler(HttpSessionRequiredException.class)
    private String handleHttpSessionException() {
        return "redirect:/regionNonEmergencyTripsSearch/0";
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class NonEmergencySearchForm {
        private LocalDateTime start;
        private LocalDateTime end;
        private String tripCategory;
        private String garrison;
    }
}

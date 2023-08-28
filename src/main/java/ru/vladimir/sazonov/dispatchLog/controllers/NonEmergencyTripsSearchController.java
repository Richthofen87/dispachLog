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
@SessionAttributes({"nonEmergencyTripSearch", "nonEmergencySearchForm", "nonEmergencySearchPage", "nonEmergencyTripsPageSearch"})
@RequestMapping("/nonEmergencyTripsCitySearch")
public class NonEmergencyTripsSearchController {

    @Autowired
    private DAOService daoService;
    @Autowired
    private PrintDescriptionServiceImpl descriptionService;
    private final Set<NonAlarmDivisionWork> worksToDelete = new HashSet<>();

    @GetMapping("/")
    public String startContent(@RequestParam(required = false) Integer pageNumber,
                               @RequestParam(defaultValue = "false") boolean addFields,
                               @RequestParam(required = false) boolean resetSearchResults,
                               @RequestParam(required = false) boolean clearFormFields,
                               @RequestParam(required = false) boolean resetParamsAndSearchResults,
                               Model model) {
        if (addFields) return "Поиск выездов на занятия в городе";
        model.addAttribute("nonEmergencyTripSearch", new NonEmergencyTrips());
        model.addAttribute("nonEmergencySearchPage", false);
        worksToDelete.clear();
        if (clearFormFields) return "Поиск выездов на занятия в городе";
        if (resetSearchResults) return "redirect:/nonEmergencyTripsCitySearch/" + pageNumber;
        if (model.getAttribute("nonEmergencySearchForm") == null || resetParamsAndSearchResults)
            model.addAttribute("nonEmergencySearchForm", new NonEmergencySearchForm());
        if (pageNumber == null) {
            model.addAttribute("nonEmergencyTripsPageSearch", null);
            return "Поиск выездов на занятия в городе";
        }
        return "redirect:/nonEmergencyTripsCitySearch/" + pageNumber;
    }

    @GetMapping("/record")
    public String getPageRecord(@ModelAttribute(name = "nonEmergencyTripsPageSearch") Page<NonEmergencyTrips> nonEmergencyTripsPageSearch,
                                @RequestParam int tripIndex, Model model) {
        model.addAttribute("nonEmergencyTripSearch", nonEmergencyTripsPageSearch.getContent().get(tripIndex));
        model.addAttribute("nonEmergencySearchPage", true);
        worksToDelete.clear();
        return "Поиск выездов на занятия в городе";
    }

    @PostMapping("/search")
    public String getPage(@ModelAttribute(name = "nonEmergencySearchForm") NonEmergencySearchForm nonEmergencySearchForm, Model model) {
        LocalDateTime start = nonEmergencySearchForm.start;
        LocalDateTime end = nonEmergencySearchForm.end;
        String tripCategory = nonEmergencySearchForm.tripCategory;
        if (start == null || end == null) return "redirect:/nonEmergencyTripsCitySearch/";
        model.addAttribute("nonEmergencySearchForm", new NonEmergencySearchForm(start, end, tripCategory));
        return "redirect:/nonEmergencyTripsCitySearch/0";
    }

    @GetMapping("/{pageNumber}")
    public String getPage(@PathVariable int pageNumber, Model model,
                          @RequestParam(required = false) Integer tripIndex) {
        NonEmergencySearchForm nonEmergencySearchForm = (NonEmergencySearchForm) model.getAttribute("nonEmergencySearchForm");
        Page<NonEmergencyTrips> page;
        if (nonEmergencySearchForm.start != null) page = setPageAttr(pageNumber, model);
        else return "Поиск выездов на занятия в городе";
        if (tripIndex != null) {
            model.addAttribute("nonEmergencyTripSearch", page.getContent().get(tripIndex));
            model.addAttribute("nonEmergencySearchPage", true);
        }
        return "Поиск выездов на занятия в городе";
    }

    @PostMapping("/edit")
    public String editTrip(@Valid @ModelAttribute(name = "nonEmergencyTripSearch") NonEmergencyTrips nonEmergencyTripEdit,
                           BindingResult bindingResult, @RequestParam(name = "pageNumber") int pageNumber,
                           @ModelAttribute(name = "nonEmergencySearchPage") boolean nonEmergencySearchPage, Model model) {
        if (!nonEmergencySearchPage) return "redirect:/nonEmergencyTripsCitySearch/?addFields=true";
        if (bindingResult.hasErrors()) {
            setPageAttr(pageNumber, model);
            return "Поиск выездов на занятия в городе";
        }
        daoService.saveOrUpdateNonEmergencyTrip(nonEmergencyTripEdit, worksToDelete);
        return "redirect:/nonEmergencyTripsCitySearch/?resetSearchResults=true&pageNumber=" + pageNumber;
    }

    @PostMapping("/delete")
    public String deleteTrip(@Valid @ModelAttribute(name = "nonEmergencyTripSearch") NonEmergencyTrips nonEmergencyTrip,
                             BindingResult bindingResult,
                             @ModelAttribute(name = "nonEmergencySearchPage") boolean nonEmergencySearchPage,
                             @RequestParam(name = "pageNumber") int pageNumber, Model model) {
        if (!nonEmergencySearchPage) return "redirect:/nonEmergencyTripsCitySearch/?addFields=true";
        if (bindingResult.hasErrors()) {
            setPageAttr(pageNumber, model);
            return "Поиск выездов на занятия в городе";
        }
        daoService.deleteNonEmergencyTrip(nonEmergencyTrip, worksToDelete);
        return "redirect:/nonEmergencyTripsCitySearch/?resetSearchResults=true&pageNumber=" + pageNumber;
    }

    @PostMapping("/addNewDivisionEdit")
    public String addNewDivision(@Valid @ModelAttribute(name = "nonEmergencyTripSearch") NonEmergencyTrips nonEmergencyTrip,
                                 BindingResult bindingResult,
                                 @ModelAttribute(name = "nonEmergencySearchPage") boolean nonEmergencySearchPage,
                                 @RequestParam(name = "indexDivision", required = false) Integer indexDivision) {
        if (!nonEmergencySearchPage) return "redirect:/nonEmergencyTripsCitySearch/?addFields=true";
        nonEmergencyTrip.addNonAlarmDivisionWork(indexDivision);
        return "redirect:/nonEmergencyTripsCitySearch/?addFields=true";
    }

    @PostMapping("/deleteDivisionEdit")
    public String deleteDivisionEdit(@Valid @ModelAttribute(name = "nonEmergencyTripSearch") NonEmergencyTrips nonEmergencyTrip,
                                     BindingResult bindingResult,
                                     @ModelAttribute(name = "nonEmergencySearchPage") boolean nonEmergencySearchPage,
                                     @RequestParam(name = "indexDivision", required = false) Integer indexDivision) {
        List<NonAlarmDivisionWork> list;
        if (!nonEmergencySearchPage || (list = nonEmergencyTrip.getNonAlarmDivisionWorkList()).isEmpty())
            return "redirect:/nonEmergencyTripsCitySearch/?addFields=true";
        try {
            NonAlarmDivisionWork nonAlarmDivisionWork = list.get(indexDivision == null ? list.size() - 1 : --indexDivision);
            list.remove(nonAlarmDivisionWork);
            worksToDelete.add(nonAlarmDivisionWork);
        } catch (IndexOutOfBoundsException ignore) {
        }
        return "redirect:/nonEmergencyTripsCitySearch/?addFields=true";
    }

    private Page<NonEmergencyTrips> setPageAttr(int pageNumber, Model model) {
        NonEmergencySearchForm nonEmergencySearchForm = (NonEmergencySearchForm) model.getAttribute("nonEmergencySearchForm");
        Page<NonEmergencyTrips> page = nonEmergencySearchForm.tripCategory.isEmpty()
                ? daoService.findAllCityNonEmergencyTrips(nonEmergencySearchForm.start, nonEmergencySearchForm.end, pageNumber, 20)
                : daoService.findAllCityNonEmergencyTripsByCategory(nonEmergencySearchForm.start, nonEmergencySearchForm.end,
                nonEmergencySearchForm.tripCategory, pageNumber, 20);
        model.addAttribute("nonEmergencyTripsPageSearch", page);
        return page;
    }

    @ExceptionHandler(SQLException.class)
    private String handleException() {
        return "redirect:/nonEmergencyTripsCitySearch/?addFields=true";
    }

    @ExceptionHandler(HttpSessionRequiredException.class)
    private String handleHttpSessionException() {
        return "redirect:/nonEmergencyTripsCitySearch/0";
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class NonEmergencySearchForm {
        private LocalDateTime start;
        private LocalDateTime end;
        private String tripCategory;
    }
}

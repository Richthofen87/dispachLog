package ru.vladimir.sazonov.dispatchLog.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import ru.vladimir.sazonov.dispatchLog.model.EmergencyTrips;
import ru.vladimir.sazonov.dispatchLog.model.NonAlarmDivisionWork;
import ru.vladimir.sazonov.dispatchLog.model.NonEmergencyTrips;
import ru.vladimir.sazonov.dispatchLog.service.DAOService;
import ru.vladimir.sazonov.dispatchLog.service.impl.PrintDescriptionServiceImpl;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@SessionAttributes({"regionNonEmergencyTripEdit", "regionNonEmergencyTripsPage"})
@RequestMapping("/regionNonEmergencyTrips")
public class RegionNonEmergencyTripsController {

    @Autowired
    private DAOService daoService;
    private final Set<NonAlarmDivisionWork> worksToDelete = new HashSet<>();

    @GetMapping("/")
    public String startContent(@RequestParam(required = false) Integer pageNumber,
                               @RequestParam(defaultValue = "false") boolean editFields,
                               Model model) {
        if (editFields) return "Выезды на занятия в области";
        NonEmergencyTrips regionNonEmergencyTripEdit = new NonEmergencyTrips();
        regionNonEmergencyTripEdit.addNonAlarmDivisionWork(1);
        model.addAttribute("regionNonEmergencyTripEdit", regionNonEmergencyTripEdit);
        worksToDelete.clear();
        if (pageNumber != null) return "redirect:/regionNonEmergencyTrips/" + pageNumber;
        return "Выезды на занятия в области";
    }

    @GetMapping("/record")
    public String getPageRecord(@ModelAttribute(name = "regionNonEmergencyTripsPage") Page<NonEmergencyTrips> regionNonEmergencyTripsPage,
                                @RequestParam int tripIndex, Model model) {
        model.addAttribute("regionNonEmergencyTripEdit", regionNonEmergencyTripsPage.getContent().get(tripIndex));
        worksToDelete.clear();
        return "Выезды на занятия в области";
    }

    @GetMapping("/{pageNumber}")
    public String getPage(@PathVariable int pageNumber, Model model) {
        addPage(pageNumber, model);
        return "Выезды на занятия в области";
    }

    @PostMapping("/edit")
    public String editTrip(@Valid @ModelAttribute(name = "regionNonEmergencyTripEdit") NonEmergencyTrips regionNonEmergencyTripEdit,
                           BindingResult bindingResult, @RequestParam(name = "pageNumber") int pageNumber, Model model) {
        if (bindingResult.hasErrors()) {
            addPage(pageNumber, model);
            return "Выезды на занятия в области";
        }
        daoService.saveOrUpdateNonEmergencyTrip(regionNonEmergencyTripEdit, worksToDelete);
        return "redirect:/regionNonEmergencyTrips/?pageNumber=" + pageNumber;
    }

    @PostMapping("/delete")
    public String deleteTrip(@Valid @ModelAttribute(name = "regionNonEmergencyTripEdit") NonEmergencyTrips regionNonEmergencyTripEdit,
                             BindingResult bindingResult, @RequestParam(name = "pageNumber") int pageNumber, Model model) {
        if (bindingResult.hasErrors()) {
            addPage(pageNumber, model);
            return "Выезды на занятия в области";
        }
        daoService.deleteNonEmergencyTrip(regionNonEmergencyTripEdit, worksToDelete);
        return "redirect:/regionNonEmergencyTrips/?pageNumber=" + pageNumber;
    }

    @PostMapping("/addNewDivisionEdit")
    public String addNewDivision(@Valid @ModelAttribute(name = "regionNonEmergencyTripEdit") NonEmergencyTrips regionNonEmergencyTripEdit,
                                 BindingResult bindingResult,
                                 @RequestParam(name = "index", required = false) Integer index) {
        regionNonEmergencyTripEdit.addNonAlarmDivisionWork(index);
        return "redirect:/regionNonEmergencyTrips/?editFields=true";
    }

    @PostMapping("/deleteDivisionEdit")
    public String deleteDivisionEdit(@Valid @ModelAttribute(name = "regionNonEmergencyTripEdit") NonEmergencyTrips regionNonEmergencyTripEdit,
                                     BindingResult bindingResult, @RequestParam(name = "index", required = false) Integer index) {
        List<NonAlarmDivisionWork> list = regionNonEmergencyTripEdit.getNonAlarmDivisionWorkList();
        if (list.isEmpty()) return "redirect:/regionNonEmergencyTrips/?editFields=true";
        try {
            NonAlarmDivisionWork nonAlarmDivisionWork = list.get(index == null ? list.size() - 1 : --index);
            list.remove(nonAlarmDivisionWork);
            worksToDelete.add(nonAlarmDivisionWork);
        } catch (IndexOutOfBoundsException ignore) {
        }
        return "redirect:/regionNonEmergencyTrips/?editFields=true";
    }

    @ExceptionHandler(SQLException.class)
    private String handleException() {
        return "redirect:/regionNonEmergencyTrips/?editFields=true";
    }

    @ExceptionHandler(HttpSessionRequiredException.class)
    private String handleHttpSessionException() {
        return "redirect:/regionNonEmergencyTrips/0";
    }

    private void addPage(int pageNumber, Model model) {
        Page<NonEmergencyTrips> page = daoService.getRegionNonEmergencyTripsPage(pageNumber, 20);
        model.addAttribute("regionNonEmergencyTripsPage", page);
    }
}

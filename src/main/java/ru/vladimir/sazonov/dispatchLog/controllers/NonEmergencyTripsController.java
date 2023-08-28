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
@SessionAttributes({"nonEmergencyTrip", "nonEmergencyTripsPage"})
@RequestMapping("/nonEmergencyTripsCity")
public class NonEmergencyTripsController {

    @Autowired
    private DAOService daoService;
    @Autowired
    private PrintDescriptionServiceImpl descriptionService;
    private final Set<NonAlarmDivisionWork> worksToDelete = new HashSet<>();

    @GetMapping("/")
    public String startContent(@RequestParam(required = false) Integer pageNumber,
                               @RequestParam(defaultValue = "false") boolean editFields,
                               Model model) {
        if (editFields) return "Выезды на занятия в городе";
        NonEmergencyTrips nonEmergencyTrip = new NonEmergencyTrips();
        nonEmergencyTrip.addNonAlarmDivisionWork(1);
        model.addAttribute("nonEmergencyTrip", nonEmergencyTrip);
        worksToDelete.clear();
        if (pageNumber != null) return "redirect:/nonEmergencyTripsCity/" + pageNumber;
        return "Выезды на занятия в городе";
    }

    @GetMapping("/record")
    public String getPageRecord(@ModelAttribute(name = "nonEmergencyTripsPage") Page<NonEmergencyTrips> nonEmergencyTripsPage,
                                @RequestParam int tripIndex, Model model) {
        model.addAttribute("nonEmergencyTrip", nonEmergencyTripsPage.getContent().get(tripIndex));
        worksToDelete.clear();
        return "Выезды на занятия в городе";
    }

    @GetMapping("/{pageNumber}")
    public String getPage(@PathVariable int pageNumber, Model model) {
        addPage(pageNumber, model);
        return "Выезды на занятия в городе";
    }

    @PostMapping("/edit")
    public String editTrip(@Valid @ModelAttribute(name = "nonEmergencyTrip") NonEmergencyTrips nonEmergencyTrip,
                           BindingResult bindingResult, @RequestParam(name = "pageNumber") int pageNumber, Model model) {
        if (bindingResult.hasErrors()) {
            addPage(pageNumber, model);
            return "Выезды на занятия в городе";
        }
        daoService.saveOrUpdateNonEmergencyTrip(nonEmergencyTrip, worksToDelete);
        return "redirect:/nonEmergencyTripsCity/?pageNumber=" + pageNumber;
    }

    @PostMapping("/delete")
    public String deleteTrip(@Valid @ModelAttribute(name = "nonEmergencyTrip") NonEmergencyTrips nonEmergencyTrip,
                             BindingResult bindingResult, @RequestParam(name = "pageNumber") int pageNumber, Model model) {
        if (bindingResult.hasErrors()) {
            addPage(pageNumber, model);
            return "Выезды на занятия в городе";
        }
        daoService.deleteNonEmergencyTrip(nonEmergencyTrip, worksToDelete);
        return "redirect:/nonEmergencyTripsCity/?pageNumber=" + pageNumber;
    }

    @PostMapping("/addNewDivisionEdit")
    public String addNewDivision(@Valid @ModelAttribute(name = "nonEmergencyTrip") NonEmergencyTrips nonEmergencyTrip,
                                 BindingResult bindingResult,
                                 @RequestParam(name = "index", required = false) Integer index) {
        nonEmergencyTrip.addNonAlarmDivisionWork(index);
        return "redirect:/nonEmergencyTripsCity/?editFields=true";
    }

    @PostMapping("/deleteDivisionEdit")
    public String deleteDivisionEdit(@Valid @ModelAttribute(name = "nonEmergencyTrip") NonEmergencyTrips nonEmergencyTrip,
                                     BindingResult bindingResult, @RequestParam(name = "index", required = false) Integer index) {
        List<NonAlarmDivisionWork> list = nonEmergencyTrip.getNonAlarmDivisionWorkList();
        if (list.isEmpty()) return "redirect:/nonEmergencyTripsCity/?editFields=true";
        try {
            NonAlarmDivisionWork nonAlarmDivisionWork = list.get(index == null ? list.size() - 1 : --index);
            list.remove(nonAlarmDivisionWork);
            worksToDelete.add(nonAlarmDivisionWork);
        } catch (IndexOutOfBoundsException ignore) {
        }
        return "redirect:/nonEmergencyTripsCity/?editFields=true";
    }

    @PostMapping("/saveDescription")
    private String saveDescription(@ModelAttribute(name = "nonEmergencyTripEdit") EmergencyTrips nonEmergencyTripEdit,
                                   BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) descriptionService.printEmergencyTripDesc(nonEmergencyTripEdit);
        return "redirect:/nonEmergencyTripsCity/?editFields=true";
    }

    @ExceptionHandler(SQLException.class)
    private String handleException() {
        return "redirect:/nonEmergencyTripsCity/?editFields=true";
    }

    @ExceptionHandler(HttpSessionRequiredException.class)
    private String handleHttpSessionException() {
        return "redirect:/nonEmergencyTripsCity/0";
    }

    private void addPage(int pageNumber, Model model) {
        Page<NonEmergencyTrips> page = daoService.getCityNonEmergencyTripsPage(pageNumber, 30);
        model.addAttribute("nonEmergencyTripsPage", page);
    }
}

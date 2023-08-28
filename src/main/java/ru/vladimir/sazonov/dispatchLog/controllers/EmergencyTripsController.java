package ru.vladimir.sazonov.dispatchLog.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import ru.vladimir.sazonov.dispatchLog.model.*;
import ru.vladimir.sazonov.dispatchLog.service.DAOService;
import ru.vladimir.sazonov.dispatchLog.service.impl.PrintDescriptionServiceImpl;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@SessionAttributes({"emergencyTripEdit", "tripsPage"})
@RequestMapping("/emergencyTripsCity")
public class EmergencyTripsController {

    @Autowired
    private DAOService daoService;
    @Autowired
    private PrintDescriptionServiceImpl descriptionService;
    private final Set<AlarmDivisionWork> worksToDelete = new HashSet<>();
    private final Set<Rtp> rtpToDelete = new HashSet<>();
    private final Set<ToWhomWasReported> reportedToDelete = new HashSet<>();

    @GetMapping("/")
    public String startContent(@RequestParam(required = false) Integer pageNumber,
                               @RequestParam(defaultValue = "false") boolean editFields,
                               Model model) {
        if (editFields) return "Выезды по тревоге в городе";
        EmergencyTrips emergencyTripEdit = new EmergencyTrips();
        emergencyTripEdit.addAlarmDivisionWork(1);
        model.addAttribute("emergencyTripEdit", emergencyTripEdit);
        worksToDelete.clear();
        rtpToDelete.clear();
        reportedToDelete.clear();
        if (pageNumber != null) return "redirect:/emergencyTripsCity/" + pageNumber;
        return "Выезды по тревоге в городе";
    }

    @GetMapping("/record")
    public String getPageRecord(@RequestParam int tripIndex, Model model) {
        model.addAttribute("emergencyTripEdit",
                ((Page<EmergencyTrips>) model.getAttribute("tripsPage")).getContent().get(tripIndex));
        worksToDelete.clear();
        rtpToDelete.clear();
        reportedToDelete.clear();
        return "Выезды по тревоге в городе";
    }

    @GetMapping("/{pageNumber}")
    public String getPage(@PathVariable int pageNumber, Model model) {
        addPage(pageNumber, model);
        return "Выезды по тревоге в городе";
    }

    @PostMapping("/edit")
    public String editTrip(@Valid @ModelAttribute(name = "emergencyTripEdit") EmergencyTrips emergencyTripEdit,
                           BindingResult bindingResult, @RequestParam(name = "pageNumber") int pageNumber, Model model) {
        if (bindingResult.hasErrors()) {
            addPage(pageNumber, model);
            return "Выезды по тревоге в городе";
        }
        daoService.saveOrUpdateEmergencyTrip(emergencyTripEdit, worksToDelete, rtpToDelete, reportedToDelete);
        return "redirect:/emergencyTripsCity/?pageNumber=" + pageNumber;
    }

    @PostMapping("/delete")
    public String deleteTrip(@Valid @ModelAttribute(name = "emergencyTripEdit") EmergencyTrips emergencyTripEdit,
                             BindingResult bindingResult, @RequestParam(name = "pageNumber") int pageNumber, Model model) {
        if (bindingResult.hasErrors()) {
            addPage(pageNumber, model);
            return "Выезды по тревоге в городе";
        }
        daoService.deleteEmergencyTrip(emergencyTripEdit, worksToDelete, rtpToDelete, reportedToDelete);
        return "redirect:/emergencyTripsCity/?pageNumber=" + pageNumber;
    }

    @PostMapping("/addNewDivisionEdit")
    public String addNewDivision(@Valid @ModelAttribute(name = "emergencyTripEdit") EmergencyTrips emergencyTripEdit,
                                 BindingResult bindingResult,
                                 @RequestParam(name = "indexDivision", required = false) Integer indexDivision) {
        emergencyTripEdit.addAlarmDivisionWork(indexDivision);
        return "redirect:/emergencyTripsCity/?editFields=true";
    }

    @PostMapping("/deleteDivisionEdit")
    public String deleteDivisionEdit(@Valid @ModelAttribute(name = "emergencyTripEdit") EmergencyTrips emergencyTripEdit,
                                     BindingResult bindingResult,
                                     @RequestParam(name = "indexDivision", required = false) Integer indexDivision) {
        List<AlarmDivisionWork> list = emergencyTripEdit.getAlarmDivisionWorkList();
        if (list.isEmpty())
            return "redirect:/emergencyTripsCity/?editFields=true";
        try {
            AlarmDivisionWork alarmDivisionWork = list.get(indexDivision == null ? list.size() - 1 : --indexDivision);
            list.remove(alarmDivisionWork);
            worksToDelete.add(alarmDivisionWork);
        } catch (IndexOutOfBoundsException ignore) {
        }
        return "redirect:/emergencyTripsCity/?editFields=true";
    }

    @PostMapping("/addNewRtpEdit")
    public String addNewRtpEdit(@Valid @ModelAttribute(name = "emergencyTripEdit") EmergencyTrips emergencyTripEdit,
                                BindingResult bindingResult,
                                @RequestParam(name = "indexRtp", required = false) Integer indexRtp) {
        emergencyTripEdit.addRtp(indexRtp);
        return "redirect:/emergencyTripsCity/?editFields=true";
    }

    @PostMapping("/deleteRtpEdit")
    public String deleteRtpEdit(@Valid @ModelAttribute(name = "emergencyTripEdit") EmergencyTrips emergencyTripEdit,
                                BindingResult bindingResult,
                                @RequestParam(name = "indexRtp", required = false) Integer indexRtp) {
        List<Rtp> list = emergencyTripEdit.getRtpList();
        if (list.isEmpty()) return "redirect:/emergencyTripsCity/?editFields=true";
        try {
            Rtp rtp = list.get(indexRtp == null ? list.size() - 1 : --indexRtp);
            list.remove(rtp);
            rtpToDelete.add(rtp);
        } catch (IndexOutOfBoundsException ignore) {
        }
        return "redirect:/emergencyTripsCity/?editFields=true";
    }

    @PostMapping("/addReportedEdit")
    public String addReportedEdit(@Valid @ModelAttribute(name = "emergencyTripEdit") EmergencyTrips emergencyTripEdit,
                                  BindingResult bindingResult,
                                  @RequestParam(name = "indexReported", required = false) Integer indexReported) {
        emergencyTripEdit.addReported(indexReported);
        return "redirect:/emergencyTripsCity/?editFields=true";
    }

    @PostMapping("/deleteReportedEdit")
    public String deleteReportedEdit(@Valid @ModelAttribute(name = "emergencyTripEdit") EmergencyTrips emergencyTripEdit,
                                     BindingResult bindingResult,
                                     @RequestParam(name = "indexReported", required = false) Integer indexReported) {
        List<ToWhomWasReported> list = emergencyTripEdit.getToWhomWasReportedList();
        if (list.isEmpty())
            return "redirect:/emergencyTripsCity/?editFields=true";
        try {
            ToWhomWasReported reported = list.get(indexReported == null ? list.size() - 1 : --indexReported);
            list.remove(reported);
            reportedToDelete.add(reported);
        } catch (IndexOutOfBoundsException ignore) {
        }
        return "redirect:/emergencyTripsCity/?editFields=true";
    }

    @PostMapping("/saveDescription")
    private String saveDescription(@Valid @ModelAttribute(name = "emergencyTripEdit") EmergencyTrips emergencyTripEdit,
                                   BindingResult bindingResult) {
        if (!bindingResult.hasErrors())
            new Thread(() -> descriptionService.printEmergencyTripDesc(emergencyTripEdit)).start();
        return "redirect:/emergencyTripsCity/?editFields=true";
    }

    @ExceptionHandler(SQLException.class)
    private String handleSQLException() {
        return "redirect:/emergencyTripsCity/?editFields=true";
    }

    @ExceptionHandler(HttpSessionRequiredException.class)
    private String handleHttpSessionException() {
        return "redirect:/emergencyTripsCity/0";
    }

    private void addPage(int pageNumber, Model model) {
        model.addAttribute("tripsPage", daoService.getCityEmergencyTripsPage(pageNumber, 15));
    }
}

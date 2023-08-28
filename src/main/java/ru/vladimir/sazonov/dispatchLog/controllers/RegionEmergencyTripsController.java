package ru.vladimir.sazonov.dispatchLog.controllers;

import jakarta.validation.Valid;
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
import ru.vladimir.sazonov.dispatchLog.service.impl.PrintDescriptionServiceImpl;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@SessionAttributes({"regionEmergencyTripEdit", "regionTripsPage"})
@RequestMapping("/regionEmergencyTrips")
public class RegionEmergencyTripsController {

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
        if (editFields) return "Выезды по тревоге в области";
        EmergencyTrips emergencyTrip = new EmergencyTrips();
        emergencyTrip.addAlarmDivisionWork(1);
        model.addAttribute("regionEmergencyTripEdit", emergencyTrip);
        worksToDelete.clear();
        rtpToDelete.clear();
        reportedToDelete.clear();
        if (pageNumber != null) return "redirect:/regionEmergencyTrips/" + pageNumber;
        return "Выезды по тревоге в области";
    }

    @GetMapping("/record")
    public String getPageRecord(@RequestParam int tripIndex, Model model) {
        model.addAttribute("regionEmergencyTripEdit",
                ((Page<EmergencyTrips>) model.getAttribute("regionTripsPage")).getContent().get(tripIndex));
        worksToDelete.clear();
        rtpToDelete.clear();
        reportedToDelete.clear();
        return "Выезды по тревоге в области";
    }

    @GetMapping("/{pageNumber}")
    public String getPage(@PathVariable int pageNumber, Model model) {
        addPage(pageNumber, model);
        return "Выезды по тревоге в области";
    }

    @PostMapping("/edit")
    public String editTrip(@Valid @ModelAttribute(name = "regionEmergencyTripEdit") EmergencyTrips regionEmergencyTripEdit,
                           BindingResult bindingResult, @RequestParam(name = "pageNumber") int pageNumber, Model model) {
        if (bindingResult.hasErrors()) {
            addPage(pageNumber, model);
            return "Выезды по тревоге в области";
        }
        daoService.saveOrUpdateEmergencyTrip(regionEmergencyTripEdit, worksToDelete, rtpToDelete, reportedToDelete);
        return "redirect:/regionEmergencyTrips/?pageNumber=" + pageNumber;
    }

    @PostMapping("/delete")
    public String deleteTrip(@Valid @ModelAttribute(name = "regionEmergencyTripEdit") EmergencyTrips regionEmergencyTripEdit,
                             BindingResult bindingResult, @RequestParam(name = "pageNumber") int pageNumber, Model model) {
        if (bindingResult.hasErrors()) {
            addPage(pageNumber, model);
            return "Выезды по тревоге в области";
        }
        daoService.deleteEmergencyTrip(regionEmergencyTripEdit, worksToDelete, rtpToDelete, reportedToDelete);
        return "redirect:/regionEmergencyTrips/?pageNumber=" + pageNumber;
    }

    @PostMapping("/addNewDivisionEdit")
    public String addNewDivision(@Valid @ModelAttribute(name = "regionEmergencyTripEdit") EmergencyTrips regionEmergencyTripEdit,
                                 BindingResult bindingResult,
                                 @RequestParam(name = "indexDivision", required = false) Integer indexDivision) {
        regionEmergencyTripEdit.addAlarmDivisionWork(indexDivision);
        return "redirect:/regionEmergencyTrips/?editFields=true";
    }

    @PostMapping("/deleteDivisionEdit")
    public String deleteDivisionEdit(@Valid @ModelAttribute(name = "regionEmergencyTripEdit") EmergencyTrips regionEmergencyTripEdit,
                                     BindingResult bindingResult,
                                     @RequestParam(name = "indexDivision", required = false) Integer indexDivision) {
        List<AlarmDivisionWork> list = regionEmergencyTripEdit.getAlarmDivisionWorkList();
        if (list.isEmpty()) return "redirect:/regionEmergencyTrips/?editFields=true";
        try {
            AlarmDivisionWork alarmDivisionWork = list.get(indexDivision == null ? list.size() - 1 : --indexDivision);
            list.remove(alarmDivisionWork);
            worksToDelete.add(alarmDivisionWork);
        } catch (IndexOutOfBoundsException ignore) {
        }
        return "redirect:/regionEmergencyTrips/?editFields=true";
    }

    @PostMapping("/addNewRtpEdit")
    public String addNewRtpEdit(@Valid @ModelAttribute(name = "regionEmergencyTripEdit") EmergencyTrips regionEmergencyTripEdit,
                                BindingResult bindingResult,
                                @RequestParam(name = "indexRtp", required = false) Integer indexRtp) {
        regionEmergencyTripEdit.addRtp(indexRtp);
        return "redirect:/regionEmergencyTrips/?editFields=true";
    }

    @PostMapping("/deleteRtpEdit")
    public String deleteRtpEdit(@Valid @ModelAttribute(name = "regionEmergencyTripEdit") EmergencyTrips regionEmergencyTripEdit,
                                BindingResult bindingResult,
                                @RequestParam(name = "indexRtp", required = false) Integer indexRtp) {
        List<Rtp> list = regionEmergencyTripEdit.getRtpList();
        if (list.isEmpty()) return "redirect:/regionEmergencyTrips/?editFields=true";
        try {
            Rtp rtp = list.get(indexRtp == null ? list.size() - 1 : --indexRtp);
            list.remove(rtp);
            rtpToDelete.add(rtp);
        } catch (IndexOutOfBoundsException ignore) {
        }
        return "redirect:/regionEmergencyTrips/?editFields=true";
    }

    @PostMapping("/addReportedEdit")
    public String addReportedEdit(@Valid @ModelAttribute(name = "regionEmergencyTripEdit") EmergencyTrips regionEmergencyTripEdit,
                                  BindingResult bindingResult,
                                  @RequestParam(name = "indexReported", required = false) Integer indexReported) {
        regionEmergencyTripEdit.addReported(indexReported);
        return "redirect:/regionEmergencyTrips/?editFields=true";
    }

    @PostMapping("/deleteReportedEdit")
    public String deleteReportedEdit(@Valid @ModelAttribute(name = "regionEmergencyTripEdit") EmergencyTrips regionEmergencyTripEdit,
                                     BindingResult bindingResult,
                                     @RequestParam(name = "indexReported", required = false) Integer indexReported) {
        List<ToWhomWasReported> list = regionEmergencyTripEdit.getToWhomWasReportedList();
        if (list.isEmpty()) return "redirect:/regionEmergencyTrips/?editFields=true";
        try {
            ToWhomWasReported reported = list.get(indexReported == null ? list.size() - 1 : --indexReported);
            list.remove(reported);
            reportedToDelete.add(reported);
        } catch (IndexOutOfBoundsException ignore) {
        }
        return "redirect:/regionEmergencyTrips/?editFields=true";
    }

    @PostMapping("/saveDescription")
    private String saveDescription(@Valid @ModelAttribute(name = "regionEmergencyTripEdit") EmergencyTrips regionEmergencyTripEdit,
                                   BindingResult bindingResult) {
        if (!bindingResult.hasErrors())
            new Thread(() -> descriptionService.printEmergencyTripDesc(regionEmergencyTripEdit)).start();
        return "redirect:/regionEmergencyTrips/?editFields=true";
    }

    @ExceptionHandler(SQLException.class)
    private String handleException() {
        return "redirect:/regionEmergencyTrips/?editFields=true";
    }

    @ExceptionHandler(HttpSessionRequiredException.class)
    private String handleHttpSessionException() {
        return "redirect:/regionEmergencyTrips/0";
    }

    private void addPage(int pageNumber, Model model) {
        Page<EmergencyTrips> page = daoService.getRegionEmergencyTripsPage(pageNumber, 15);
        model.addAttribute("regionTripsPage", page);
    }
}

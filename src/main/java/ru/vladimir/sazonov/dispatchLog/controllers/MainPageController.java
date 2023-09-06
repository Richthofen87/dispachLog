package ru.vladimir.sazonov.dispatchLog.controllers;

import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ru.vladimir.sazonov.dispatchLog.service.DAOService;
import ru.vladimir.sazonov.dispatchLog.service.PrintDutyTripsService;

import java.time.LocalDate;


@Controller
@RequestMapping("/")
public class MainPageController {

    @Autowired
    ServletContext servletContext;

    @Autowired
    DAOService daoService;

    @Autowired
    PrintDutyTripsService printDutyTripsService;

    @GetMapping("/")
    public String getMainPage() {
        initData();
        return "index";
    }

    @GetMapping("/printDutyTrips")
    public String printDutyTrips(@RequestParam(required = false) LocalDate date, Model model) {
        if (date == null) return "index";
        printDutyTripsService.printDutyTrips(date)
                .forEach(model::addAttribute);
        return "DutyTrips";
    }

    private void initData() {
        if (servletContext.getAttribute("categoriesMap") == null)
            servletContext.setAttribute("categoriesMap", daoService.getCategoriesMap());
        if (servletContext.getAttribute("garrisonSettlementsMap") == null)
            servletContext.setAttribute("garrisonSettlementsMap", daoService.getGarrisonSettlementsMap());
        if (servletContext.getAttribute("settlementDivisionsMap") == null)
            servletContext.setAttribute("settlementDivisionsMap", daoService.getSettlementDivisionsMap());
    }
}

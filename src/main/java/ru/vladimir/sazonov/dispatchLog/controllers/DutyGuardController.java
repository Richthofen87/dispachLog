package ru.vladimir.sazonov.dispatchLog.controllers;

import jakarta.servlet.ServletContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.vladimir.sazonov.dispatchLog.model.DutyGuard;
import ru.vladimir.sazonov.dispatchLog.service.DAOService;

import java.time.LocalDate;

@Controller
@RequestMapping("/duty_guard")
public class DutyGuardController {

    @Autowired
    ServletContext servletContext;

    @Autowired
    DAOService daoService;

    @GetMapping()
    public String dutyGuard(@ModelAttribute DutyGuard dutyGuard) {
        return "Дежурная смена";
    }

    @PostMapping("/save")
    public String saveDutyGuard(@Valid DutyGuard dutyGuard, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "Дежурная смена";
        dutyGuard.getPrKey().setDutyDate(LocalDate.now());
        if (dutyGuard.getNdsCuks().getName().isEmpty()) dutyGuard.setNdsCuks(null);
        if (dutyGuard.getSpndsSpt().getName().isEmpty()) dutyGuard.setSpndsSpt(null);
        if (dutyGuard.getCppsDispatcher().getName().isEmpty()) dutyGuard.setCppsDispatcher(null);
        servletContext.setAttribute("dutyGuard", daoService.saveDutyGuard(dutyGuard));
        System.out.println(servletContext.getAttribute("dutyGuard"));
        return "redirect:/";
    }
}

package com.example.pair_employees.api;

import com.example.pair_employees.service.PairingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Validated
@Controller
public class PairingController {

    private final PairingService pairingService;

    @Autowired
    public PairingController(PairingService pairingService) {
        this.pairingService = pairingService;
    }

    @GetMapping("/pairing")
    public String getPairedEmployees() {
        return "pairing-page";
    }

    @GetMapping("/result")
    public String getResult() {
        return "result";
    }

    @PostMapping("/paired-employees")
    public RedirectView getPairedEmployeesPost(@RequestParam("file") MultipartFile file,
                                               RedirectAttributes redirectAttributes) {
        String result = pairingService.getEmployeesByTimeSpentOnSameProjects(file);

        redirectAttributes.addFlashAttribute("result", result);

        return new RedirectView("result");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex) {
        ModelAndView mav = new ModelAndView();

        mav.addObject("error", ex.getMessage());
        mav.setViewName("error");

        return mav;
    }
}

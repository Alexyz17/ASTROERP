package com.example.astroterrassa.controladors;

import com.example.astroterrassa.model.Sugerencia;
import com.example.astroterrassa.services.EmailService;
import com.example.astroterrassa.services.SugerenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;

@Controller
public class SugerenciaController {

    private final SugerenciaService sugerenciaService;

    @Autowired
    public SugerenciaController(SugerenciaService sugerenciaService) {
        this.sugerenciaService = sugerenciaService;
    }

    @Autowired
    private EmailService emailService;

    @GetMapping("/sugerencias")
    public String sugerencias(Model model) {
        model.addAttribute("sugerencia", new Sugerencia());
        return "sugerencias";
    }

    @PostMapping("/sugerencias")
    public String saveSugerencia(@ModelAttribute Sugerencia sugerencia) {
        sugerencia.setFecha_sugerencia(new Date());
        sugerenciaService.saveSugerencia(sugerencia);
        emailService.sendSugerencia(sugerencia);
        return "redirect:/";
    }

}
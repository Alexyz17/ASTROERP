package com.example.astroterrassa.controladors;

import com.example.astroterrassa.DAO.RevistaRepository;
import com.example.astroterrassa.model.Revista;
import com.example.astroterrassa.model.User;
import com.example.astroterrassa.services.RevistaService;
import com.example.astroterrassa.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class RevistaController {

    @Autowired
    private RevistaRepository revistaRepository;

    @Autowired
    private RevistaService revistaService;

    @Autowired
    private UserService userService;

    @GetMapping("/index")
    public String index(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (user.getPermissions() == 1 || user.getPermissions() == 2) {
            model.addAttribute("showDiv", true);
        } else {
            model.addAttribute("showDiv", false);
        }
        String url = revistaRepository.findById(1).map(revista -> revista.getUrl()).orElse("/");
        model.addAttribute("url", url);
        return "index";
    }

    @GetMapping("/cambioUrl")
    public String cambioUrlForm(Model model,  Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (user.getPermissions() == 1 || user.getPermissions() == 2) {
            model.addAttribute("showDiv", true);
        } else {
            model.addAttribute("showDiv", false);
        }
        Revista revista = revistaService.getRevistaById(1);
        model.addAttribute("revista", revista);
        return "cambioUrl";
    }

    @PostMapping("/cambioUrl")
    public String cambioUrl(@RequestParam String url) {
        revistaService.updateUrl(1, url);
        return "redirect:/index";
    }
}
package com.example.astroterrassa.controladors;

import com.example.astroterrassa.DAO.UserRepository;
import com.example.astroterrassa.model.Pago;
import com.example.astroterrassa.model.User;
import com.example.astroterrassa.services.EmailService;
import com.example.astroterrassa.services.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class PagoController {

    private final PagoService pagoService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @Autowired
    private EmailService emailService;

    @GetMapping("/pago")
    public String getPagoForm(Model model) {
        model.addAttribute("pago", new Pago());
        return "pago";
    }

    @PostMapping("/pago")
    public String submitPagoForm(@ModelAttribute Pago pago, @RequestParam Long userId, Principal principal) {
        if (pago.getProducto().equals("Joven")) {
            pago.setPrecio(20);
        } else if (pago.getProducto().equals("Normal")) {
            pago.setPrecio(40);
        }
        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        Long userId_pago = user.getId();
        pagoService.savePago(pago, userId_pago);
        Pago savedPago = pagoService.savePago(pago, userId_pago);
        emailService.sendPaymentDetailsEmail(savedPago, userId_pago);

        return "redirect:/index";
    }




}
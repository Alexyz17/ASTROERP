package com.example.astroterrassa.controladors;

import com.example.astroterrassa.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.astroterrassa.model.*;
import com.example.astroterrassa.DAO.*;
import com.example.astroterrassa.services.EmailService;
import com.example.astroterrassa.services.EmailService;


import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import java.time.LocalDate;
import java.util.List;

@Controller
public class AppController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UsersRolesRepository usersRolesRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private EmailService emailService;

    @Autowired
    private EventoRepository eventoService;

    @Autowired
    private UserService userService;

    @RequestMapping("/register")
    public String showRegistrationForm(Model model) {
        return "register";
    }

    @PostMapping("/makeRegistration")
    public String makeRegistration(@RequestParam("nombre") String nombre,
                                   @RequestParam("apellidos") String apellidos,
                                   @RequestParam("tlf") String tlf,
                                   @RequestParam("mail") String mail,
                                   @RequestParam("username") String username,
                                   @RequestParam("password") String password,
                                   @RequestParam("fecha_nt") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fecha_nt,
                                   @RequestParam(value = "notify_check", required = false) Boolean notify_check,
                                   @RequestParam("genero") int genero) {
        int notify = (notify_check != null && notify_check) ? 1 : 0;

        User u = new User();
        u.setNombre(nombre);
        u.setPassword(passwordEncoder.encode(password));
        u.setApellidos(apellidos);
        u.setTlf(tlf);
        u.setMail(mail);
        u.setUsername(username);
        u.setFecha_nt(fecha_nt);
        u.setRegister_dt(LocalDateTime.now());
        u.setLastDt(LocalDateTime.now());
        u.setGenero(genero);
        u.setPermisos(0);
        u.setNotify(notify);
        u.setIntents(3);
        u.setIngreso(2);
        userRepository.save(u); // Guarda el usuario primero

        if (notify == 1) {
            emailService.sendWelcomeEmail(u);
        }

        UsersRoles usersRoles = new UsersRoles();
        usersRoles.setUserId(u.getUser_id());
        usersRoles.setRoleId(0);
        usersRoles.setRolNombre("usuario");
        usersRolesRepository.save(usersRoles); // Guarda el rol del usuario

        System.out.println("Created user " + u.getUsername() + " with password " + u.getPassword());
        return "redirect:/listado";
    }

    @RequestMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password) {

        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setLastDt(LocalDateTime.from(ZonedDateTime.now().toInstant()));
            // Revisar si ha caducado la membresia y mandar email avisando al usuario si tiene el notify en 1
            if (user.getPermisos() == 3) {
                Date membresia = user.getMembresia();

                if (membresia != null) {
                    LocalDate fechaMembresia = membresia.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                    LocalDate fechaActual = LocalDate.now();

                    if (!fechaMembresia.plusYears(1).isAfter(fechaActual)) {
                        user.setPermisos(0);
                        userRepository.save(user);
                        if (user.getNotify() == 1) {
                            emailService.sendMembersiaCaducada(user);
                        }
                    }
                }
            }
            if (passwordEncoder.matches(password, user.getPassword())) {
                return "index";
            } else {
                return "redirect:/login";
            }
        } else {
            return "redirect:/login";
        }
    }

    // Método para asignar roles solo uno a cada usuario, accesible solo para administradores
    @PreAuthorize("hasRole('admin')")
    @PostMapping("/asignarRol")
    public String asignarRol(@RequestParam("username") String username,
                             @RequestParam("rol") String rol) {
        User user = userRepository.findByUsername(username);
        UsersRoles usersRoles = new UsersRoles();
        usersRoles.setRolNombre(rol);
        usersRolesRepository.save(usersRoles);
        userRepository.save(user);
        return "redirect:/index";
    }

    @RequestMapping("/index")
    public String showIndex() {
        return "index";
    }

    @GetMapping("/stats")
    public String showStats(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (user.getPermissions() == 1 || user.getPermissions() == 2) {
            model.addAttribute("showDiv", true);
        } else {
            model.addAttribute("showDiv", false);
        }
        return "stats";
    }

}
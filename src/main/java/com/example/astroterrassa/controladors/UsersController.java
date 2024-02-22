package com.example.astroterrassa.controladors;

import com.example.astroterrassa.DAO.UserRepository;
import com.example.astroterrassa.DAO.UsersRolesRepository;
import com.example.astroterrassa.model.User;
import com.example.astroterrassa.model.UsersRoles;
import com.example.astroterrassa.services.EmailService;
import com.example.astroterrassa.services.UserService;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


@Controller
@Slf4j
public class UsersController {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private UserService UsuariServices;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UsersRolesRepository UsersRolesRepository;

    @Autowired
    private EmailService emailService;

    @RequestMapping("/listado")
    @GetMapping
    public ModelAndView getAllUsers(Model model,  Principal principal) {
        User user = UsuariServices.findByUsername(principal.getName());
        if (user.getPermissions() == 1 || user.getPermissions() == 2) {
            model.addAttribute("showDiv", true);
        } else {
            model.addAttribute("showDiv", false);
        }
        ModelAndView mav = new ModelAndView("listado");
        mav.addObject("users", UsuariServices.getAllUsers());

        // Get the current user
        Object principal1 = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal1 instanceof UserDetails) {
            username = ((UserDetails) principal1).getUsername();
        } else {
            username = principal1.toString();
        }
        User currentUser = UsuariServices.getUserByUsername(username);

        // Get the date part of the register_dt
        //LocalDate registerDate = user.getRegister_dt().toLocalDate();

        // Add the current user and the register date to the model
        mav.addObject("currentUser", currentUser);
        //mav.addObject("registerDate", registerDate);

        return mav;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        return "register";
    }

    @PostMapping("/register")
    public String registre(User user, UsersRoles usersRoles) {
        UsuariServices.registrarPersona(user, usersRoles);
        return "redirect:/index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }

    @GetMapping("/assignRole/{id}")
    public String showAssignRoleForm(@PathVariable("id") Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("user", user);
        return "assignRole";
    }

    @PostMapping("/assignRole")
    public String submitAssignRoleForm(@ModelAttribute User user, UsersRoles usersRoles) {
        UsuariServices.guardarRol(user, usersRoles);
        return "redirect:/listado";
    }

    @GetMapping("/bloquejar/{id}")
    public String bloquejarUsuari(@PathVariable Long id, User user) {
        UsuariServices.bloquejarUsuari(id, user);
        return "redirect:/listado";
    }

    @GetMapping("/bloquejats")
    public String llistaUsuarisBloquejats(User user, Model model) {
        List<User> listaUsers = UsuariServices.getBlockedUsers();
        model.addAttribute("usuaris", listaUsers);
        return "listado";
    }

    @GetMapping("/desbloqueja/{id}")
    public String desbloquejarUsuari(@PathVariable Long id, User user) {
        UsuariServices.desbloquejarUsuari(id, user);
        return "redirect:/listado";

    }

    @GetMapping("/editUser/{id}")
    public String showEditUserForm(@PathVariable("id") Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("user", user);
        return "editUser";
    }

    @PostMapping("/editUser")
    public String submitEditUserForm(@ModelAttribute User user) {
        userRepository.save(user);
        return "redirect:/listado";
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(Model model, Principal principal) {
        User user = UsuariServices.findByUsername(principal.getName());
        if (user.getPermissions() == 1 || user.getPermissions() == 2) {
            model.addAttribute("showDiv", true);
        } else {
            model.addAttribute("showDiv", false);
        }
        String username = principal.getName();
        User user1 = UsuariServices.getUserByUsername(username);
        model.addAttribute("user", user1);
        String formattedDate = UsuariServices.getFormattedRegisterDate(user1);
        model.addAttribute("formattedDate", formattedDate);
        return "perfil";
    }


    @RequestMapping("/forgot_password")
    public String showForgotPassword() {
        return "forgot_password";
    }

    @PostMapping("/forgot_password")
    public String forgotPassword(@RequestParam("username") String username) {

        User user = userRepository.findByUsername(username);
        // generador de contraseña aleatoria
        String password = UsuariServices.generateRandomPassword(10);
        // Codificar contraseña
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
        String email = user.getMail();
        emailService.sendForgotPasswordEmail(user, password, email);
        return "redirect:/login";

    }

    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/listado";
    }

    @GetMapping("/persons/pdf")
    public ResponseEntity<byte[]> getPersonsPdf() {
        // Aquí debes generar tu tabla HTML como una cadena
        String htmlTable = UsuariServices.generateHtmlTable();

        // Luego, llama a la función createPdfFromHtmlTable para convertir la tabla HTML a PDF
        byte[] pdfBytes = UsuariServices.createPdfFromHtmlTable(htmlTable);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("person-list.pdf", "person-list.pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }


    @GetMapping("/userDetails/{username}")
    public String getUserDetails2(@PathVariable String username, Model model, Principal principal) {
        User user2 = UsuariServices.findByUsername(principal.getName());
        if (user2.getPermissions() == 1 || user2.getPermissions() == 2) {
            model.addAttribute("showDiv", true);
        } else {
            model.addAttribute("showDiv", false);
        }
        User user = UsuariServices.getUserByUsername(username);
        model.addAttribute("user", user);
        return "userDetails";
    }


    @PostMapping("/userDetails/{username}")
    public String updateUserDetails2(@PathVariable String username, @RequestParam String dni, @RequestParam String nombre, @RequestParam String apellidos, @RequestParam String mail, @RequestParam String tlf, @RequestParam(required = false) String notify, @RequestParam int genero, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fecha_nt, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate register_dt, @RequestParam int ingreso, Model model) {
        int notifyInt = "on".equals(notify) ? 1 : 2;
        Date sqlDate = new Date(fecha_nt.getTime());
        LocalDateTime registerDateTime = register_dt.atStartOfDay();
        UsuariServices.updateUserDetails2(dni, nombre, apellidos, mail, tlf, notifyInt, genero, sqlDate, registerDateTime, ingreso, username);
        User user = UsuariServices.getUserByUsername(username);
        model.addAttribute("user", user);
        return "redirect:/listado";
    }

    @GetMapping("/editarPerfil/{username}")
    public String showEditProfileForm(@PathVariable String username, Model model, Principal principal) {
        User user = UsuariServices.findByUsername(principal.getName());
        if (user.getPermissions() == 1 || user.getPermissions() == 2) {
            model.addAttribute("showDiv", true);
        } else {
            model.addAttribute("showDiv", false);
        }
        User user1 = UsuariServices.getUserByUsername(username);
        model.addAttribute("user", user1);
        return "editarPerfil";
    }

    @PostMapping("/editarPerfil/{username}")
    public String submitEditProfileForm(@PathVariable String username, @RequestParam String dni, @RequestParam String nombre, @RequestParam String apellidos, @RequestParam String mail, @RequestParam String tlf, @RequestParam(required = false) String notify, @RequestParam int genero, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fecha_nt, Model model) {
        int notifyInt = "on".equals(notify) ? 1 : 2;
        Date sqlDate = new Date(fecha_nt.getTime());
        UsuariServices.updateUserDetails( dni, nombre, apellidos, mail, tlf, notifyInt, genero, sqlDate, username);
        return "redirect:/perfil";
    }

    @GetMapping("/cambiarPermiso/{username}")
    public String cambiarPermiso(@PathVariable String username, Model model, Principal principal) {
        User user2 = UsuariServices.findByUsername(principal.getName());
        if (user2.getPermissions() == 1 || user2.getPermissions() == 2) {
            model.addAttribute("showDiv", true);
        } else {
            model.addAttribute("showDiv", false);
        }
        User user = UsuariServices.getUserByUsername(username);
        model.addAttribute("user", user);
        return "cambiarPermiso";
    }

    @PostMapping("/cambiarPermiso/{username}")
    public String cambiarPermiso(@PathVariable String username, @RequestParam int permisos, Model model) {
        User user = userRepository.findByUsername(username);

        user.setPermisos(permisos);
        userRepository.save(user);
        return "redirect:/listado";
    }

    @PostMapping("/guardarDni")
    public String submitDniForm(@RequestParam String dni) {
        // Get the current user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        User currentUser = UsuariServices.getUserByUsername(username);

        // Save the dni
        UsuariServices.guardarDni(currentUser, dni);

        return "redirect:/pago";
    }

    @GetMapping("/sendUserList")
    public String sendUserList(@RequestParam String email) {
        List<User> users = UsuariServices.getAllUsers();
        emailService.sendUserList(users, email);
        return "redirect:/listado";
    }

}
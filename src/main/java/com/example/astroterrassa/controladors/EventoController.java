package com.example.astroterrassa.controladors;

import com.example.astroterrassa.DAO.EventoPersonaRepository;
import com.example.astroterrassa.DAO.UserRepository;
import com.example.astroterrassa.model.*;
import com.example.astroterrassa.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class EventoController {

    @Autowired
    private EventoService eventService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TipoEventoService tipoEventoService;

    @Autowired
    private EventoPersonaRepository eventoPersonaRepository;

    @Autowired
    private EventoPersonaService eventoPersonaService;

    @Autowired
    private UserEventService userEventService;

    @GetMapping("/eventos")
    public String showEventos(Model model, Principal principal) {
            User user = userService.findByUsername(principal.getName());
            if (user.getPermissions() == 1 || user.getPermissions() == 2) {
                model.addAttribute("showDiv", true);
            } else {
                model.addAttribute("showDiv", false);
            }
            List<Evento> eventos = eventService.getAllEventos();
            String username = principal.getName();
            User currentUser = userRepository.findByUsername(username);

            for (Evento evento : eventos) {
                boolean isSubscribed = eventoPersonaRepository.existsByUserAndEvento(currentUser, evento);
                evento.setUserInscribed(isSubscribed);
            }

            model.addAttribute("eventos", eventos);
            model.addAttribute("currentUser", currentUser);

            return "eventos";
    }

    @RequestMapping("/eventos")
    public String eventos(Model model, @PathVariable int tipoEvento) {

        Evento eventos = eventService.getEventosPorTipo(tipoEvento);
        model.addAttribute("eventos", eventos);
        model.addAttribute("tipoEvento", tipoEvento);


        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        User currentUser = userService.getCurrentUser(username);

        model.addAttribute("currentUser", currentUser);

        return "eventos";
    }

    @GetMapping("/nuevoEvento")
    public String nuevoEvento(Model model, Principal principal) {

        List<TipoEvento> tiposEvento = tipoEventoService.getAllTiposEvento();

        String username = principal.getName();

        User currentUser = userRepository.findByUsername(username);

        model.addAttribute("tiposEvento", tiposEvento);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("evento", new Evento());

        return "nuevoEvento";
    }

    @PostMapping("/nuevoEvento")
    public String saveEvento(@ModelAttribute Evento evento,
                             @RequestParam String fecha_taller_evento,
                             @RequestParam(value = "statusInt", required = false) Boolean statusInt,
                             @RequestParam String tituloTipoEvento) throws Exception {
        // Convert the fecha_taller_evento string to a Date object
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha = formatter.parse(fecha_taller_evento);
        evento.setFecha_taller_evento(fecha);

        // Convert the statusInt Boolean to an int
        int status = (statusInt != null && statusInt) ? 1 : 0;
        evento.setStatus(status);

        // Get the TipoEvento by its title
        TipoEvento tipoEvento = tipoEventoService.getTipoEventoByTitulo(tituloTipoEvento);
        if (tipoEvento != null) {
            // Set the tipo_te and titulo in the Evento
            evento.setTipo(tipoEvento.getId());
            evento.setTitulo(tituloTipoEvento);
        } else {
            throw new Exception("Tipo de evento no encontrado");
        }

        // Save the Evento object
        eventService.saveEvento(evento);

        return "redirect:/eventos";
    }

    @GetMapping("/listadoTipoEvento")
    public String showListado(Model model, Principal principal) {
        User currentUser = userService.getCurrentUser(principal.getName());
        List<TipoEvento> tipoEvento = tipoEventoService.getAllTiposEvento();
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("tiposEvento", tipoEvento);
        return "listadoTipoEvento";
    }

    @GetMapping("/crearTipoEvento")
    public String showForm(Model model) {
        model.addAttribute("tipoEvento", new TipoEvento());
        return "crearTipoEvento";
    }

    @PostMapping("/crearTipoEvento")
    public String crearTipoEvento(@ModelAttribute TipoEvento tipoEvento) {
        tipoEventoService.saveTipoEvento(tipoEvento);
        return "redirect:/listadoTipoEvento";
    }

    @PostMapping("/eliminarTipoEvento/{id}")
    public String deleteTipoEvento(@PathVariable("id") int id) {
        tipoEventoService.deleteTipoEvento(id);
        return "redirect:/eventos";
    }

    @GetMapping("/eventos/pdf")
    public ResponseEntity<byte[]> getEventosPdf() {
        // Generate the PDF bytes (this method should be implemented in your service)
        byte[] pdfBytes = eventService.generatePdf();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "eventos.pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/sendEventosList")
    public ResponseEntity<String> sendEventosList(@RequestParam String email) throws IOException {
        byte[] csvBytes = emailService.generarCsvEventos();
        emailService.sendEventosList(email, csvBytes);

        return new ResponseEntity<>("Lista de eventos enviada", HttpStatus.OK);
    }

    // inscribirse a un evento
    @PostMapping("/inscribirse")
    public String inscribirse(@RequestParam("idEvento") int idEvento, Principal principal) {

        User currentUser = userService.getCurrentUser(principal.getName());
        // pillamos la id de usuario y la id de evento
        int id_user = currentUser.getUser_id();

        // creamos un objeto de tipo EventoPersona
        EventoPersona eventoPersona = new EventoPersona();
        // le asignamos el id de usuario y el id de evento
        eventoPersona.setId_user(id_user);
        eventoPersona.setId_te(idEvento);
        // guardamos el objeto en la base de datos
        eventoPersonaRepository.save(eventoPersona);

        if (currentUser.getNotify() == 1) {
            emailService.sendInscripcionEvento(currentUser.getMail(), currentUser.getNombre(), currentUser.getApellidos(), idEvento);
        }

        return "redirect:/eventos";
    }

    @PostMapping("/desinscribirse")
    public String desinscribirse(@RequestParam("idEvento") int idEvento, Principal principal) {

        // pillamos la id del evento desde idEvento y el usuario desde principal
        User currentUser = userService.getCurrentUser(principal.getName());

        //Pillamos el idEvento
        Evento evento = eventService.getEventoById(idEvento);

        // creamos un objeto de tipo EventoPersona
        EventoPersona eventoPersona = eventoPersonaRepository.findByUserAndEvento(currentUser, evento);

        // borramos el objeto de la base de datos
        eventoPersonaRepository.delete(eventoPersona);

        // enviamos correo de desinscripción
        if (currentUser.getNotify() == 1) {
            emailService.sendDesinscripcionEvento(currentUser.getMail(), currentUser.getNombre(), currentUser.getApellidos(), idEvento);
        }
        return "redirect:/eventos";
    }
}
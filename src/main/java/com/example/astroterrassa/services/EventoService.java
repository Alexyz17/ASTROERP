package com.example.astroterrassa.services;

import com.example.astroterrassa.DAO.EventoRepository;
import com.example.astroterrassa.model.Evento;
import com.example.astroterrassa.model.EventoPersona;
import com.example.astroterrassa.DAO.EventoPersonaRepository ;
import com.example.astroterrassa.model.User;
import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.*;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private EventoPersonaRepository eventoUsuarioRepository;

    public Evento getEventosPorTipo(int tipo) {
        return eventoRepository.findByTipo(tipo);
    }

    public String generateHtmlTableEventos() {
        List<Evento> eventos = eventoRepository.findAll();
        StringBuilder htmlTable = new StringBuilder();
        htmlTable.append("<table border=\"1\">");
        htmlTable.append("<tr>");
        htmlTable.append("<th>Id</th>");
        htmlTable.append("<th>Titulo</th>");
        htmlTable.append("<th>Fecha</th>");
        htmlTable.append("<th>Descripción</th>");
        htmlTable.append("<th>Usuarios Inscritos</th>"); // Add a new column for the count of registered users
        htmlTable.append("</tr>");
        for (Evento evento : eventos) {
            htmlTable.append("<tr>");
            htmlTable.append("<td>" + evento.getId() + "</td>");
            htmlTable.append("<td>" + evento.getTitulo() + "</td>");
            htmlTable.append("<td>" + evento.getFecha_taller_evento() + "</td>");
            htmlTable.append("<td>" + evento.getDescripcion() + "</td>");
            htmlTable.append("<td>" + evento.getUser_inscribed() + "</td>"); // Add the count of registered users
            htmlTable.append("</tr>");
        }
        htmlTable.append("</table>");
        return htmlTable.toString();
    }

    public byte[] createPdfFromHtmlTableEvento(String htmlTable) {
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(htmlTable, target);
        return target.toByteArray();
    }

    public void saveEvento(Evento evento) {
        eventoRepository.save(evento);
    }

    public List<Evento> getAllEventos() {
        return eventoRepository.findAll();
    }

    public Evento getEventoById(int id) {
        return eventoRepository.findByTipo(id);
    }

    public byte[] generatePdf() {
        String htmlTable = generateHtmlTableEventos();
        return createPdfFromHtmlTableEvento(htmlTable);
    }
}
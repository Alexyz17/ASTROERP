package com.example.astroterrassa.services;

import com.example.astroterrassa.DAO.EventoPersonaRepository;
import com.example.astroterrassa.DAO.EventoRepository;
import com.example.astroterrassa.model.Evento;
import com.example.astroterrassa.model.EventoPersona;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventoPersonaService {

    @Autowired
    private EventoPersonaRepository eventoPersonaRepository; // Autowire the repository

    public List<EventoPersona> getAllEventoPersonas() {
        return eventoPersonaRepository.findAll();
    }

    public EventoPersona saveEventoPersona(EventoPersona eventoPersona) {
        return eventoPersonaRepository.save(eventoPersona); // Add a method to save an EventoPersona
    }
}
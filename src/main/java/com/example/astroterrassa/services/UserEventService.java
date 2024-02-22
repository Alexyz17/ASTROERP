package com.example.astroterrassa.services;

import com.example.astroterrassa.DAO.EventoPersonaRepository;
import com.example.astroterrassa.DAO.EventoRepository;
import com.example.astroterrassa.DAO.UserRepository;
import com.example.astroterrassa.model.Evento;
import com.example.astroterrassa.model.EventoPersona;
import com.example.astroterrassa.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserEventService {

    @Autowired
    private EventoPersonaRepository userEventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventoRepository eventoRepository;

    public void deleteUserEvent(String username, int id_te) {
        User user = userRepository.findByUsername(username);
        Evento evento = eventoRepository.findById((long) id_te).orElse(null);
        if (evento != null) {
            EventoPersona eventoPersona = userEventRepository.findByUserAndEvento(user, evento);
            if (eventoPersona != null) {
                userEventRepository.delete(eventoPersona);
            }
        }
    }

    public void saveUserEvent(String username, int id_te) {
        User user = userRepository.findByUsername(username);
        Evento evento = eventoRepository.findById((long) id_te).orElse(null);
        if (evento != null) {
            EventoPersona eventoPersona = new EventoPersona();
            eventoPersona.setId_user(user.getUser_id());
            eventoPersona.setId_te(evento.getId());
            userEventRepository.save(eventoPersona);
        }
    }

}
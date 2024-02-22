package com.example.astroterrassa.services;

import com.example.astroterrassa.model.Pago;
import com.example.astroterrassa.model.User;
import com.example.astroterrassa.DAO.PagoRepository;
import com.example.astroterrassa.DAO.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final UserRepository userRepository;

    @Autowired
    public PagoService(PagoRepository pagoRepository, UserRepository userRepository) {
        this.pagoRepository = pagoRepository;
        this.userRepository = userRepository;
    }

    public Pago savePago(Pago pago, Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            pago.setUser(user);
        }
        pago.setFechaPago(LocalDateTime.now());
        return pagoRepository.save(pago);
    }



}
package com.example.astroterrassa.services;

import com.example.astroterrassa.DAO.SugerenciaRepository;
import com.example.astroterrassa.model.Sugerencia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SugerenciaService {

    private final SugerenciaRepository sugerenciaRepository;

    @Autowired
    public SugerenciaService(SugerenciaRepository sugerenciaRepository) {
        this.sugerenciaRepository = sugerenciaRepository;
    }

    public Sugerencia saveSugerencia(Sugerencia sugerencia) {
        return sugerenciaRepository.save(sugerencia);
    }
}
package com.example.astroterrassa.services;

import com.example.astroterrassa.DAO.TipoEventoRepository;
import com.example.astroterrassa.model.TipoEvento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoEventoService {

    @Autowired
    private TipoEventoRepository tipoEventoRepository;

    public List<TipoEvento> getAllTiposEvento() {
        return tipoEventoRepository.findAll();
    }

    public void saveTipoEvento(TipoEvento tipoEvento) {
        tipoEventoRepository.save(tipoEvento);
    }

    public void deleteTipoEvento(int id) {
        tipoEventoRepository.deleteById(id);
    }

    public TipoEvento getTipoEventoByTitulo(String titulo) {
        return tipoEventoRepository.findByTitulo(titulo);
    }

}
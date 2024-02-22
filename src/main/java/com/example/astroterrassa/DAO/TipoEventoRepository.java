package com.example.astroterrassa.DAO;

import com.example.astroterrassa.model.TipoEvento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoEventoRepository extends JpaRepository<TipoEvento, Integer> {
    TipoEvento findByTitulo(String titulo);
}

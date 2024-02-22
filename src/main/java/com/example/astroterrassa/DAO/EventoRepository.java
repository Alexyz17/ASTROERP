package com.example.astroterrassa.DAO;

import com.example.astroterrassa.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<Evento, Long> {
    Evento findByTipo(int tipo);

}
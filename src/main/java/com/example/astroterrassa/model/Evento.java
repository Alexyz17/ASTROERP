package com.example.astroterrassa.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "taller_evento")
@Data
public class Evento {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "descripcion")
    private String descripcion;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_taller_evento")
    private Date fecha_taller_evento;

    @Column(name = "status")
    private int status;

    @Column(name = "tipo_te")
    private int tipo;

    @Column(name = "user_inscribed")
    private int user_inscribed;

    @Transient
    private boolean isUserInscribed;

    public boolean isUserInscribed() {
        return isUserInscribed;
    }

    public void setUserInscribed(boolean userInscribed) {
        isUserInscribed = userInscribed;
    }

}
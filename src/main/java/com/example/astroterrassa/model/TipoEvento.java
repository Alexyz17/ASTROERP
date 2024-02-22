package com.example.astroterrassa.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "te_tipos")
@Data
public class TipoEvento {

    @Id
    @Column(name = "id_te")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "descripcion")
    private String descripcion;

    @OneToMany(mappedBy = "tipo")
    private List<Evento> eventos;

}

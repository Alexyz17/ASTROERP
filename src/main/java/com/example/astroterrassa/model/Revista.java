package com.example.astroterrassa.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Table(name = "revista")
@Data
public class Revista implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_revista")
    private int id_revista;

    @Column(name = "url", nullable = false)
    private String url;
}
package com.example.astroterrassa.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "material")
@Data
public class Material implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_material")
    private int idMaterial;

    @Column(name = "material_nombre")
    private String material_nombre;

    @Column(name = "cantidad")
    private int cantidad;

    @Column(name = "ubicacion")
    private String ubicacion;

    @Column(name = "image_path")
    private String imagePath;

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }
}
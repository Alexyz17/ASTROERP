package com.example.astroterrassa.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
public class Pago implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private int idPago;

    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "user_id")
    private User user;

    @Column(name = "precio")
    private int precio;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "producto")
    private String producto;

    @Column(name = "Cuenta")
    private String cuenta;
}
package com.example.astroterrassa.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "users")
@Data
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int user_id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellidos")
    private String apellidos;

    @Column(name = "mail")
    private String mail;

    @Column(name = "tlf")
    private String tlf;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "notify")
    private int notify;

    @Column(name = "intentos")
    private int intents;

    @Column(name = "permisos")
    private Integer permisos;

    //Recuperamos de la tabla users_roles el rol que tiene el usuario
    @OneToOne(mappedBy = "user")
    private UsersRoles usersRoles;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type")
    private AuthenticationType authType;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "genero")
    private int genero;


    @Column(name = "register_dt")
    private LocalDateTime register_dt;

    @Column(name = "last_dt")
    private LocalDateTime lastDt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_nt")
    private Date fecha_nt;

    @Column(name = "membresia")
    private Date membresia;

    @Column(name = "dni")
    private String dni;

    @Column(name = "ingreso")
    private Integer ingreso;

    public long getId() {
        return user_id;
    }

    @OneToMany(mappedBy = "id_user")
    private List<EventoPersona> eventoPersonas;

    public int getPermissions() {
        return this.getPermisos();
    }

    public LocalDate getRegisterDate() {
        return this.register_dt.toLocalDate();
    }
}
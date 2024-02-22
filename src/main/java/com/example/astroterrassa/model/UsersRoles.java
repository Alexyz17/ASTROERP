package com.example.astroterrassa.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users_roles")
public class UsersRoles {

    @Id
    @Column(name = "usrol_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int usrolId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "role_id")
    private int roleId;

    @Column(name = "rol_nombre")
    private String rolNombre;

    // Asociación uno a uno con la entidad User mediante la columna 'user_id'
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

}
package com.example.astroterrassa.DAO;

import com.example.astroterrassa.model.AuthenticationType;
import com.example.astroterrassa.model.User;
import jakarta.persistence.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;



import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByDni(String dni);

    @Query("SELECT u FROM User u WHERE u.username = :username")
    User getUserByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.intents = 0")
    List<User> blockedUsers();

    @Modifying
    @Query("UPDATE User u SET u.authType = ?2 WHERE u.username = ?1")
    void updateAuthenticationType(String username, AuthenticationType authType);

    @Modifying
    @Query("INSERT INTO User (username, password, authType) VALUES (?1, ?2, ?3)")
    void createUser(String username, String password, AuthenticationType authType);

    @Query("SELECT u FROM User u WHERE u.mail = ?1")
    User findByMail(String email);

    @Modifying
    @Query("UPDATE User u SET u.dni = ?1, u.nombre = ?2, u.apellidos = ?3, u.mail = ?4, u.tlf = ?5, u.notify = ?6, u.genero = ?7, u.fecha_nt = ?8 WHERE u.username = ?9")
    void updateUserDetails(String dni, String nombre, String apellidos, String mail, String tlf, int notify, int genero, Date fecha_nt, String username);

    @Modifying
    @Query("UPDATE User u SET u.dni = ?1, u.nombre = ?2, u.apellidos = ?3, u.mail = ?4, u.tlf = ?5, u.notify = ?6, u.genero = ?7, u.fecha_nt = ?8 , u.register_dt = ?9, u.ingreso = ?10 WHERE u.username = ?11")
    void updateUserDetails2(String dni, String nombre, String apellidos, String mail, String tlf, int notify, int genero, Date fecha_nt, LocalDateTime register_dt, int ingreso, String username);


    @Modifying
    @Query("UPDATE User u SET u.permisos = ?2 WHERE u.username = ?1")
    void cambiarPermiso(String username, int permisos);

}
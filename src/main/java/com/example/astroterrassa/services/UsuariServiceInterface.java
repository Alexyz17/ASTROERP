package com.example.astroterrassa.services;

import com.example.astroterrassa.model.User;
import com.example.astroterrassa.model.UsersRoles;
import com.example.astroterrassa.security.oauth.CustomOAuth2User;

import java.util.List;

public interface UsuariServiceInterface {

    void guardarUser(User user);

    void guardarRol(User user, UsersRoles rol);

    List<User> getAllUsers();

    User getUserById(Long id);

    void processOAuthPostLogin(CustomOAuth2User oAuth2User);

    void saveUserAfterOAuthLoginSuccess(CustomOAuth2User oAuth2User);

    void logoutUser(String username);

    User getUserByUsername(String username);

    void registrarPersona(User user, UsersRoles usersRoles);

    void desbloquejarUsuari(Long id,User user);

}
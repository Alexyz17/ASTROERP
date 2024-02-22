package com.example.astroterrassa.services;

import com.example.astroterrassa.DAO.UserRepository;
import com.example.astroterrassa.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler  {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,Authentication authentication) throws IOException, ServletException {
        String username = request.getParameter("username");
        log.info(username);
        authentication.getAuthorities().forEach(authority -> {
            if(authority.getAuthority().equals("admin")){
                log.info("L'usuari " + username + " ha iniciat sessió com a administrador");
            } else if (authority.getAuthority().equals("user")){
                log.info("L'usuari " + username + " ha iniciat sessió com a usuari");
            } else if (authority.getAuthority().equals("visitant")){
                log.info("L'usuari " + username + " ha iniciat sessió com a visitant");
            }
        });
        User user = userRepository.findByUsername(username);

        if(user.getIntents()>0){
            log.info("S'han restablert els intents per accedir correctament");
            user.setIntents(3);
            userRepository.save(user);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
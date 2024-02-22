package com.example.astroterrassa.services;

import com.example.astroterrassa.DAO.UserRepository;
import com.example.astroterrassa.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");
        log.info(username);
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("No user found with username: " + username);
        } else {
            if (user.getUsername().equals("admin")) {
                log.info("El administrador no pot perdre intents");
            } else {
                if (user.getIntents() > 0) {
                    int intents = user.getIntents() - 1;
                    user.setIntents(intents);
                    userRepository.save(user);
                    exception = new LockedException("La seva contrasenya es incorrecta, ha perdut un intent et queden: " + user.getIntents());
                }
                else if(user.getIntents()== 0){
                    exception = new LockedException("El seu usuari s'ha quedat sin intents y esta bloquejat, contacti amb un administrador per desbloquejar-lo");
                }
            }

        }super.setDefaultFailureUrl("/error403");
        super.onAuthenticationFailure(request, response, exception);
    }
}
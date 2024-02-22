package com.example.astroterrassa.security.oauth;

import com.example.astroterrassa.model.AuthenticationType;
import com.example.astroterrassa.model.User;
import com.example.astroterrassa.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuthLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
        String oauth2ClientName = oauth2User.getOauth2ClientName();
        String username = oauth2User.getEmail();

        if (userService.userExists(username)) {
            userService.updateAuthenticationType(username, oauth2ClientName);
            super.onAuthenticationSuccess(request, response, authentication);
        } else {
            System.out.println("User does not exist. Creating new user.");
            User user = new User();
            user.setUsername(username);
            user.setAuthType(AuthenticationType.valueOf(oauth2ClientName.toUpperCase()));
            userService.create(user);
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

}

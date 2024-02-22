package com.example.astroterrassa.controladors;

import com.example.astroterrassa.DAO.UserRepository;
import com.example.astroterrassa.security.DatabaseLoginSuccessHandler;
import com.example.astroterrassa.services.AuthFailureHandler;
import com.example.astroterrassa.services.AuthSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration //Indica al sistema que és una classe de configuració
@EnableWebSecurity //Habilita la seguretat web
public class ConfiguracioAutenticacio {

    @Autowired
    private DatabaseLoginSuccessHandler databaseLoginSuccessHandler;

    @Autowired
    private UserDetailsService userDetailsService; //Objecte per recuperar l'usuari

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService;

    @Autowired
    public void autenticacio(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean //L'indica al sistema que el mètode és un Bean, en aquest cas perquè crea un objecte de la classe HttpSecurity
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests (
                (requests) -> requests
                        .requestMatchers("/desinscribirse/{idEvento}", "/desinscribirse", "/forgot_password", "/subEvento", "/inscribirse/{idEvento}", "/inscribirse", "/sugerencias", "/eventos", "fragments", "/pago", "/editar", "/perfil", "/register", "/makeRegistration", "/login", "/error403", "/","/editarPerfil").permitAll() //Permitir acceso a todos
                        .requestMatchers( "/material", "/stats", "/nuevoEvento", "/listado").hasAnyRole("admin", "junta") //Permitir acceso a admin i moderador
                        .requestMatchers("/eliminarTipoEvento", "/crearTipoEvento", "/listadoTipoEvento", "/cambiarPermiso", "/bloquejats", "/desbloqueja/{id}", "/assignRole", "/deleteUser", "/deleteUser/{id}", "/userDetails/", "/editUser").hasRole("admin") //Permitir acceso a admin
                        .anyRequest().authenticated() //Permet accedir a tothom que estigui autenticat
                )
                .formLogin((form) -> form //Objecte que representa el formulari de login personalitzat que utilitzarem
                        .loginPage("/login")//Pàgina on es troba el formulari per fer login personalitzat
                        .failureHandler(authenticationFailureHandler())
                        .successHandler(authenticationSuccessHandler())
                        .defaultSuccessUrl("/index", true)
                        .permitAll() //Permet acceddir a tothom
                )
                .logout((logout) -> logout //Objecte que representa el formulari de logout personalitzat que utilitzarem
                    .logoutUrl("/logout") //URL on es troba el formulari per fer logout
                    .logoutSuccessUrl("/logout.html") //Pàgina on es redirigeix després de fer logout
                    .invalidateHttpSession(true) //Invalida la sessió
                    .deleteCookies("JSESSIONID") //Elimina la cookie de la sessió
                    .permitAll() //Permet accedir a tothom
                )
                .oauth2Login( //Objecte que representa el formulari de login amb Google que utilitzarem
                        oauth2 -> { //Configuració del formulari de login amb Google
                            oauth2.loginPage("/login"); //Pàgina on es troba el formulari per fer login amb Google
                            oauth2.userInfoEndpoint( //Objecte que representa el formulari de login amb Google que utilitzarem
                                    userInfo -> userInfo.userService(oauth2UserService) //Configuració del formulari de login amb Google
                            );
                        }
                )
                .exceptionHandling((exception) -> exception //Quan es produeix una excepcció 403, accés denegat, mostrem el nostre missatge
                        .accessDeniedPage("/error403"))
                .build();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler(){
        return new AuthFailureHandler();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return new AuthSuccessHandler();
    }

}
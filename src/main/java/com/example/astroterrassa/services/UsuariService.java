package com.example.astroterrassa.services;

import com.example.astroterrassa.DAO.UserRepository;
import com.example.astroterrassa.DAO.UsersRolesRepository;
import com.example.astroterrassa.model.User;
import com.example.astroterrassa.model.UsersRoles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userDetailsService")
@Slf4j
public class UsuariService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    public UsuariService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private UsersRolesRepository usersRolesRepository;

    public boolean userHasRole(String username, String role) {
        User user = userRepository.findByUsername(username);
        UsersRoles usersRoles = usersRolesRepository.findByUser(user);
        return usersRoles.getRolNombre().equals(role);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User usuari = userRepository.findByUsername(username);

        if (usuari == null) {
            throw new UsernameNotFoundException("User not found");
        }

        UsersRoles usersRoles = usersRolesRepository.findByUser(usuari);

        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority("ROLE_" + usersRoles.getRolNombre()));

        log.info(usuari.getUsername());
        log.info(usuari.getPassword());

        LocalDateTime lastLogin = LocalDateTime.now();
        usuari.setLastDt(lastLogin);
        userRepository.save(usuari);

        return new org.springframework.security.core.userdetails.User(usuari.getUsername(), usuari.getPassword(), roles);
    }
}
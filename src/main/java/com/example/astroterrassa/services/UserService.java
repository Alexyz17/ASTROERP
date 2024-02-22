package com.example.astroterrassa.services;

import com.example.astroterrassa.DAO.UserRepository;
import com.example.astroterrassa.DAO.UsersRolesRepository;
import com.example.astroterrassa.model.AuthenticationType;
import com.example.astroterrassa.model.User;
import com.example.astroterrassa.model.UsersRoles;
import com.example.astroterrassa.security.oauth.CustomOAuth2User;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.html2pdf.HtmlConverter;

@Service
@Transactional
public class UserService implements UsuariServiceInterface {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRepository repo;

    @Autowired
    private UsersRolesRepository usersRolesRepository;

    @Setter
    @Getter
    private User currentUser;

    public void updateAuthenticationType(String username, String oauth2ClientName) {
        AuthenticationType authType = AuthenticationType.valueOf(oauth2ClientName.toUpperCase());
        repo.updateAuthenticationType(username, authType);
        System.out.println("Updated user's authentication type to " + authType);
    }

    @Override
    @Transactional
    public void guardarUser(User usuari) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        usuari.setPassword(passwordEncoder.encode(usuari.getPassword()));
        usuari.setIntents(3);
        this.userRepository.save(usuari);

    }

    // Método para guardar el rol del usuario
    @Override
    public void guardarRol(User usuari, UsersRoles rol) {
        rol.setUserId(usuari.getUser_id());
        rol.setRoleId(rol.getRoleId());
        rol.setRolNombre(rol.getRolNombre());
        usersRolesRepository.save(rol);
        System.out.println("Rol guardado");
    }

    public List<User> getAllUsers()  {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            Optional<UsersRoles> role = usersRolesRepository.findById(user.getPermisos());
            role.ifPresent(user::setUsersRoles);
        }
        return users;
    }

    @Transactional(readOnly = true)
    public List<User> getBlockedUsers() {
        return userRepository.blockedUsers();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);

    }

    @Override
    public void processOAuthPostLogin(CustomOAuth2User oAuth2User) {
        String email = oAuth2User.getEmail();
        User user = userRepository.findByMail(email);

        if (user == null) {
            // Si el usuario no existe en la base de datos, lo guardamos
            saveUserAfterOAuthLoginSuccess(oAuth2User);
            user.setRegister_dt(LocalDateTime.from(ZonedDateTime.now().toInstant()));
        } else {
            user.setLastDt(LocalDateTime.from(ZonedDateTime.now().toInstant()));
            userRepository.save(user);
        }
    }

    @Override
    public void saveUserAfterOAuthLoginSuccess(CustomOAuth2User oAuth2User) {

        User user = new User();
        user.setNombre(oAuth2User.getName());
        user.setMail(oAuth2User.getEmail());
        user.setLastDt(LocalDateTime.from(ZonedDateTime.now().toInstant()));
        user.setIntents(3);
        user.setUsername(oAuth2User.getEmail()); // or any other field you want to use for username
        user.setAuthType(AuthenticationType.GOOGLE);
        user.setEnabled(true);
        user.setNotify(1);
        userRepository.save(user); // Guarda el usuario en la base de datos

        UsersRoles usersRoles = new UsersRoles();
        usersRoles.setUserId(user.getUser_id());
        usersRoles.setRoleId(0);
        usersRoles.setRolNombre("usuario");
        usersRolesRepository.save(usersRoles);

    }

    @Override
    public void logoutUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setLastDt(LocalDateTime.from(ZonedDateTime.now().toInstant()));
            userRepository.save(user);
        }
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void registrarPersona(User user, UsersRoles usersRoles) {
        // Crea y guarda el usuario
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPermisos(1);
        user.setIntents(3);
        user.setRegister_dt(LocalDateTime.from(ZonedDateTime.now().toInstant()));
        user.setLastDt(LocalDateTime.from(ZonedDateTime.now().toInstant()));
        User savedUser = userRepository.save(user); // Guarda el usuario en la base de datos

        // Asigna el User_id y el Role_id a UsersRoles y lo guarda
        usersRoles.setUserId(savedUser.getUser_id());
        usersRoles.setRoleId(1);
        usersRoles.setRolNombre("socio");
        usersRolesRepository.save(usersRoles); // Guarda el rol del usuario

        System.out.println("User guardado");
    }

    @Override
    public void desbloquejarUsuari(Long id, User user) {
        user = getUserById(id);
        user.setIntents(3);
        userRepository.save(user);
        System.out.println("S'ha desbloquejat l'usuari");
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public UsersRoles save(UsersRoles usersRoles) {
        return usersRolesRepository.save(usersRoles);
    }

    public boolean userExists(String username) {
        return repo.getUserByUsername(username) != null;
    }

    public void create(User user) {
        repo.createUser(user.getUsername(), user.getPassword(), user.getAuthType());
    }

    public void bloquejarUsuari(Long id, User user) {
        user = getUserById(id);
        user.setIntents(0);
        userRepository.save(user);
        System.out.println("S'ha bloquejat l'usuari");
    }

    public String generateHtmlTable() {
        List<User> users = getAllUsers();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        StringBuilder htmlTable = new StringBuilder("<div style='width: 100%; height: 100%; overflow-x: auto;'><table style='border: 1px solid black; border-collapse: collapse; min-width: 100%;'><tr><th style='border: 1px solid black; padding: 10px;'>Username</th><th style='border: 1px solid black; padding: 10px;'>Nombre</th><th style='border: 1px solid black; padding: 10px;'>Apellidos</th><th style='border: 1px solid black; padding: 10px;'>Telefono</th><th style='border: 1px solid black; padding: 10px;'>Email</th><th style='border: 1px solid black; padding: 10px;'>DNI</th><th style='border: 1px solid black; padding: 10px;'>Notify</th><th style='border: 1px solid black; padding: 10px;'>Genero</th><th style='border: 1px solid black; padding: 10px;'>Fecha Nacimiento</th><th style='border: 1px solid black; padding: 10px;'>Fecha Registro</th><th style='border: 1px solid black; padding: 10px;'>Tipo Ingreso</th></tr>");
        for (User user : users) {
            String notify = user.getNotify() == 1 ? "SI" : "NO";
            String fecha_nt = sdf.format(user.getFecha_nt());
            String fecha_registro = dtf.format(user.getRegisterDate());
            htmlTable.append("<tr><td style='border: 1px solid black; padding: 10px;'>")
                    .append(user.getUsername())
                    .append("</td><td style='border: 1px solid black; padding: 10px;'>")
                    .append(user.getNombre())
                    .append("</td><td style='border: 1px solid black; padding: 10px;'>")
                    .append(user.getApellidos())
                    .append("</td><td style='border: 1px solid black; padding: 10px;'>")
                    .append(user.getTlf())
                    .append("</td><td style='border: 1px solid black; padding: 10px;'>")
                    .append(user.getMail())
                    .append("</td><td style='border: 1px solid black; padding: 10px;'>")
                    .append(user.getDni())
                    .append("</td><td style='border: 1px solid black; padding: 10px;'>")
                    .append(notify)
                    .append("</td><td style='border: 1px solid black; padding: 10px;'>")
                    .append(user.getGenero())
                    .append("</td><td style='border: 1px solid black; padding: 10px;'>")
                    .append(fecha_nt)
                    .append("</td><td style='border: 1px solid black; padding: 10px;'>")
                    .append(fecha_registro)
                    .append("</td><td style='border: 1px solid black; padding: 10px;'>")
                    .append(user.getIngreso())
                    .append("</td></tr>");
        }
        htmlTable.append("</table></div>");
        return htmlTable.toString();
    }

    public byte[] createPdfFromHtmlTable(String htmlTable) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(htmlTable, outputStream);
        return outputStream.toByteArray();
    }


    public void updateUserDetails(String dni, String nombre, String apellidos, String mail, String tlf, int notify, int genero, Date fecha_nt, String username) {
        userRepository.updateUserDetails(dni, nombre, apellidos, mail, tlf, notify, genero, fecha_nt, username);
    }

    public void updateUserDetails2(String dni, String nombre, String apellidos, String mail, String tlf, int notify, int genero, Date fecha_nt,LocalDateTime register_dt, int ingreso, String username) {
        userRepository.updateUserDetails2(dni, nombre, apellidos, mail, tlf, notify, genero, fecha_nt,register_dt, ingreso, username);
    }

    public void cambiarPermiso(String username, int permisos) {
        userRepository.cambiarPermiso(username, permisos);
    }

    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username);
    }

    public User guardarDni(User user, String dni) {
        user.setDni(dni);
        return userRepository.save(user);
    }

    public String generateRandomPassword(int i) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder pass = new StringBuilder();
        for (int x = 0; x < i; x++) {
            int j = (int) (Math.random() * chars.length());
            pass.append(chars.charAt(j));
        }
        return pass.toString();
    }

    public String getFormattedRegisterDate(User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return user.getRegisterDate().format(formatter);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }



}
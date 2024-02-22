package com.example.astroterrassa.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Triggers {
    private String dbUrl = "jdbc:mysql://localhost:3306/erpastro";
    private String username = "root";
    private String password = "1234";

    public void createTriggers() {
        try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SHOW TRIGGERS LIKE 'users'");
            if (!rs.next()) {
                String trigger1SQL = "CREATE DEFINER=`root`@`localhost` TRIGGER `after_update_users` AFTER UPDATE ON `users` FOR EACH ROW BEGIN\n" +
                        "    DECLARE rol_nombre_val VARCHAR(50);\n" +
                        "\n" +
                        "    IF NEW.permisos = 0 THEN\n" +
                        "        SET rol_nombre_val = 'usuario';\n" +
                        "    ELSEIF NEW.permisos = 1 THEN\n" +
                        "        SET rol_nombre_val = 'junta';\n" +
                        "    ELSEIF NEW.permisos = 2 THEN\n" +
                        "        SET rol_nombre_val = 'admin';\n" +
                        "    END IF;\n" +
                        "\n" +
                        "    UPDATE users_roles\n" +
                        "    SET role_id = NEW.permisos, rol_nombre = rol_nombre_val\n" +
                        "    WHERE user_id = NEW.user_id;\n" +
                        "END";
                stmt.execute(trigger1SQL);
            }

            rs = stmt.executeQuery("SHOW TRIGGERS LIKE 'users_roles'");
            if (!rs.next()) {
                String trigger2SQL = "CREATE DEFINER=`root`@`localhost` TRIGGER `before_update_users_roles` BEFORE UPDATE ON `users_roles` FOR EACH ROW BEGIN\n" +
                        "    IF NEW.role_id = 1 THEN\n" +
                        "        SET NEW.rol_nombre = 'junta';\n" +
                        "    ELSEIF NEW.role_id = 2 THEN\n" +
                        "        SET NEW.rol_nombre = 'admin';\n" +
                        "    ELSEIF NEW.role_id = 0 THEN\n" +
                        "        SET NEW.rol_nombre = 'usuario';\n" +
                        "    END IF;\n" +
                        "END";
                stmt.execute(trigger2SQL);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
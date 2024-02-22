package com.example.astroterrassa.DAO;

import com.example.astroterrassa.model.User;
import com.example.astroterrassa.model.UsersRoles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRolesRepository extends JpaRepository<UsersRoles, Integer>{
    UsersRoles findByUser(User user);
}
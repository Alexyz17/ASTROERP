package com.example.astroterrassa.DAO;

import com.example.astroterrassa.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Material m SET m.material_nombre = ?2, m.cantidad = ?3, m.ubicacion = ?4 WHERE m.idMaterial = ?1")
    void updateMaterial2(int idMaterial, String material_nombre, int cantidad, String ubicacion);

    @Modifying
    @Transactional
    @Query("DELETE FROM Material m WHERE m.idMaterial = ?1")
    void deleteById(int idMaterial);
}

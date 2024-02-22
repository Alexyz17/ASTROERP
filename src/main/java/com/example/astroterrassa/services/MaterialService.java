package com.example.astroterrassa.services;

import com.example.astroterrassa.DAO.MaterialRepository;
import com.example.astroterrassa.model.Material;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;

    @Autowired
    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    public Material saveMaterial(Material material) {
        return materialRepository.save(material);
    }

    public void updateMaterial(Material material) {
        Material existingMaterial = materialRepository.findById(Long.valueOf(material.getIdMaterial())).orElseThrow(() -> new IllegalArgumentException("Invalid material Id:" + material.getIdMaterial()));
        existingMaterial.setMaterial_nombre(material.getMaterial_nombre());
        existingMaterial.setCantidad(material.getCantidad());
        existingMaterial.setUbicacion(material.getUbicacion());
        materialRepository.save(existingMaterial);
    }

    public Material newMaterial(Material material) {
        return materialRepository.save(material);
    }


    public void saveImage(Material material, MultipartFile imagen) {
        Path directrioImagenes = Paths.get("src//main//resources//static/images");
        String rutaAbsoluta = directrioImagenes.toFile().getAbsolutePath();
        try {
            byte[] bytes = imagen.getBytes();
            Path rutaCompleta = Paths.get(rutaAbsoluta + "//" + imagen.getOriginalFilename());
            Files.write(rutaCompleta, bytes);
            material.setImagePath(imagen.getOriginalFilename());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    public byte[] generarCsvMateriales() throws IOException {
        List<Material> materiales = materialRepository.findAll();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);

        pw.println("idMaterial,material_nombre,cantidad,ubicacion");

        for (Material material : materiales) {
            pw.printf("%d,%s,%d,%s\n", material.getIdMaterial(), material.getMaterial_nombre(), material.getCantidad(), material.getUbicacion());
        }

        pw.close();
        return baos.toByteArray();
    }

    public byte[] generarPdfMateriales() throws Exception {
        List<Material> materiales = materialRepository.findAll();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();

        for (Material material : materiales) {
            document.add(new Paragraph("ID: " + material.getIdMaterial()));
            document.add(new Paragraph("Nombre: " + material.getMaterial_nombre()));
            document.add(new Paragraph("Cantidad: " + material.getCantidad()));
            document.add(new Paragraph("Ubicación: " + material.getUbicacion()));
            document.add(new Paragraph("\n"));
        }

        document.close();
        return baos.toByteArray();
    }

    public String saveImage(MultipartFile image) throws IOException {
        String fileName = StringUtils.cleanPath(image.getOriginalFilename());
        Path path = Paths.get("uploads/" + fileName);
        Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return path.toString();
    }

    public Material getMaterialById(int idMaterial) {
        return materialRepository.findById((long) idMaterial).orElse(null);
    }

    public void updateMaterial2(int idMaterial, String material_nombre, int cantidad, String ubicacion) {
        materialRepository.updateMaterial2(idMaterial, material_nombre, cantidad, ubicacion);
    }

    public void deleteById(int idMaterial) {
        materialRepository.deleteById(idMaterial);
    }
}
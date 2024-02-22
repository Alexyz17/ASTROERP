package com.example.astroterrassa.controladors;

import com.example.astroterrassa.model.Material;
import com.example.astroterrassa.model.User;
import com.example.astroterrassa.services.EmailService;
import com.example.astroterrassa.services.MaterialService;
import com.example.astroterrassa.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.astroterrassa.DAO.MaterialRepository;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Date;
import java.util.List;


@Controller
public class MaterialController {

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService UsuariServices;

    @GetMapping("/material")
    public String showMaterialForm(@RequestParam(required = false) Long id, Model model, Principal principal) {
        User user2 = UsuariServices.findByUsername(principal.getName());
        if (user2.getPermissions() == 1 || user2.getPermissions() == 2) {
            model.addAttribute("showDiv", true);
        } else {
            model.addAttribute("showDiv", false);
        }

        List<Material> materiales = materialRepository.findAll();
        model.addAttribute("materiales", materiales);

        Material material;
        if (id != null) {
            material = materialRepository.findById(id).orElse(new Material());
        } else {
            material = new Material();
        }
        model.addAttribute("material", material);

        return "material";

    }

    @PostMapping("/material")
    public String saveMaterial(@ModelAttribute Material material, Model model, @RequestParam("imagen") MultipartFile imagen) {
        if(!imagen.isEmpty()){
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
        if (material.getIdMaterial() == 0) {
            if(!imagen.isEmpty()){
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
            materialService.saveMaterial(material);
        } else {
            Material existingMaterial = materialService.getMaterialById(material.getIdMaterial());
            existingMaterial.setImagePath(material.getImagePath());
            materialService.updateMaterial(existingMaterial);
        }
        return "redirect:/listadoMaterial";
    }

    /*@GetMapping("/newMaterial")
    public String newMaterial(Model model) {
        model.addAttribute("material", new Material());
        return "newMaterial";
    }

    @PostMapping("/newMaterial")
    public String newMaterial(@ModelAttribute Material material, Model model, @RequestParam("imagen") MultipartFile imagen) {
        if(!imagen.isEmpty()){
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
        materialService.saveMaterial(material);
        return "redirect:/listadoMaterial";
    }*/

    @GetMapping("/updateMaterial/{idMaterial}")
    public String updateMaterial2(@PathVariable int idMaterial, Model model, Principal principal) {
        User user2 = UsuariServices.findByUsername(principal.getName());
        if (user2.getPermissions() == 1 || user2.getPermissions() == 2) {
            model.addAttribute("showDiv", true);
        } else {
            model.addAttribute("showDiv", false);
        }
        Material material = materialService.getMaterialById(idMaterial);
        model.addAttribute("material", material);
        return "updateMaterial";
    }

    @PostMapping("/updateMaterial/{idMaterial}")
    public String updateMaterial2(@PathVariable int idMaterial, @RequestParam String material_nombre, @RequestParam int cantidad, @RequestParam String ubicacion, @RequestParam("imagen") MultipartFile imagen) {
        Material material = materialService.getMaterialById(idMaterial);
        if(!imagen.isEmpty()){
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
        materialService.updateMaterial2(idMaterial, material_nombre, cantidad, ubicacion);
        return "redirect:/listadoMaterial";
    }

    @GetMapping("/deleteMaterial/{idMaterial}")
    public String deleteUser(@PathVariable int idMaterial) {
        materialService.deleteById(idMaterial);
        return "redirect:/listadoMaterial";
    }

    @GetMapping("/materialDetails")
    @ResponseBody
    public Material getMaterialDetails(@RequestParam Long id) {
        return materialRepository.findById(id).orElse(new Material());
    }

    @GetMapping("/sendMaterialesList")
    public ResponseEntity<String> sendMaterialesList(@RequestParam String email) throws IOException {
        byte[] csvBytes = new byte[0];
        try {
            csvBytes = materialService.generarCsvMateriales();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        emailService.sendMaterialesList(email, csvBytes);

        return new ResponseEntity<>("Lista de materiales enviada", HttpStatus.OK);
    }

    @GetMapping("/downloadMaterialesList")
    public ResponseEntity<byte[]> downloadMaterialesList() throws Exception {
        byte[] pdfBytes = materialService.generarPdfMateriales();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "materiales.pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }


    @RequestMapping("/listadoMaterial")
    public ModelAndView getAllMaterials(Model model,  Principal principal) {
        User user = UsuariServices.findByUsername(principal.getName());
        if (user.getPermissions() == 1 || user.getPermissions() == 2) {
            model.addAttribute("showDiv", true);
        } else {
            model.addAttribute("showDiv", false);
        }
        ModelAndView mav = new ModelAndView("listadoMaterial");
        List<Material> materials = materialService.getAllMaterials();
        mav.addObject("materials", materials);
        // Obtén el usuario actual
        Object principal1 = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal1 instanceof UserDetails) {
            username = ((UserDetails) principal1).getUsername();
        } else {
            username = principal1.toString();
        }
        User currentUser = UsuariServices.getUserByUsername(username);

        // Añade el usuario actual al modelo
        mav.addObject("currentUser", currentUser);
        return mav;
    }


}
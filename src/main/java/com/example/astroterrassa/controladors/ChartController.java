package com.example.astroterrassa.controladors;

import com.example.astroterrassa.DAO.EventoRepository;
import com.example.astroterrassa.services.ChartService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.astroterrassa.services.EmailService;
import com.example.astroterrassa.model.Evento;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class ChartController {

    @Autowired
    private EmailService emailService;
    @Autowired
    private ChartService chartService;



    @GetMapping("/chartData")
    public Map<String, Long> getChartData(@RequestParam String dataType, @RequestParam(required = false) String year, @RequestParam(required = false) String month) {
        return chartService.getChartData(dataType, year, month);
    }

    @GetMapping("/sendChart")
    public ResponseEntity<String> sendChart(@RequestParam String email, @RequestParam String data) throws IOException {
        byte[] csvBytes = chartService.generarCsv(data);
        emailService.sendChartEmail(email, csvBytes);

        return new ResponseEntity<>("Email sent", HttpStatus.OK);
    }


}
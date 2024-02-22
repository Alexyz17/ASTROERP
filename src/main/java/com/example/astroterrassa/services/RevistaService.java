package com.example.astroterrassa.services;

import com.example.astroterrassa.DAO.RevistaRepository;
import com.example.astroterrassa.model.Revista;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RevistaService {

    @Autowired
    private RevistaRepository revistaRepository;

    public Revista getRevistaById(int id) {
        return revistaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid revista Id:" + id));
    }

    public void updateUrl(int id, String url) {
        Revista revista = getRevistaById(id);
        revista.setUrl(url);
        revistaRepository.save(revista);
    }
}
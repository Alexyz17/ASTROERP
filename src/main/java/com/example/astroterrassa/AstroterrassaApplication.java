package com.example.astroterrassa;

import com.example.astroterrassa.model.Triggers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AstroterrassaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AstroterrassaApplication.class, args);

		Triggers triggers = new Triggers();
		triggers.createTriggers();
	}
}
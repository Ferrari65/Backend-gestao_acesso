package com.gestaoacesso.trackpassgestao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "com")
@EnableJpaRepositories(basePackages = "com.repositories")
@EntityScan(basePackages = "com.domain")


public class TrackPassGestaoDeAcessoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrackPassGestaoDeAcessoApplication.class, args);
		System.out.println("Aplicação iniciada com sucesso!");
	}

}

package tech.criasystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import tech.criasystem.controller.InicializacaoSistema;

@SpringBootApplication
public class EndpointdefaultApplication {

	public static void main(String[] args) {
		InicializacaoSistema.contextInitialized();
		SpringApplication.run(EndpointdefaultApplication.class, args);
	}

}

package tech.criasystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EndpointdefaultApplication {

	public static void main(String[] args) {
		InitPrivate.contextInitialized();
		SpringApplication.run(EndpointdefaultApplication.class, args);
	}

}

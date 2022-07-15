package tech.criasystem;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class InitPrivate extends SpringBootServletInitializer {
	
	public static void contextInitialized() {
		criarChavePrivadaAutenticacao();
	}
	
	private static void criarChavePrivadaAutenticacao() {
		try {
			KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
			keyGenerator.initialize(2048);
			KeyPair kp = keyGenerator.genKeyPair();
			PrivateKey privateKey = (PrivateKey) kp.getPrivate();
			Path resourceDirectory = Paths.get("src","main","resources");
			String absolutePath = resourceDirectory.toFile().getAbsolutePath();
			String outFile = "private";
			FileOutputStream out = new FileOutputStream(absolutePath+"/"+outFile + ".key");
			out.write(privateKey.getEncoded());
			out.close();
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
	}

}

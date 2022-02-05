package tech.criasystem.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import tech.criasystem.dto.res.UsuariotResDTO;
import tech.criasystem.model.Usuario;

@RestController
@RequestMapping("/api/login")
public class LoginController {
	
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping
    public ResponseEntity<UsuariotResDTO> post(@Valid @RequestBody Usuario usuario){
		try{
			Path resourceDirectory = Paths.get("src","main","resources");
			String absolutePath = resourceDirectory.toFile().getAbsolutePath();			
			Path path = Paths.get(absolutePath+"/"+"private.key");
			byte[] bytes = Files.readAllBytes(path);

			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
	        KeyFactory kf = KeyFactory.getInstance("RSA");
	        RSAPrivateKey privateKey = (RSAPrivateKey) kf.generatePrivate(spec);
			
			if(usuario.getUsuario().equals("teste@treinaweb.com.br") 
						&& 
				usuario.getSenha().equals("1234")){
	                String jwtToken = Jwts.builder()
	                	.claim("userId", usuario.getId())
	                    .setSubject(usuario.getUsuario())
	                    .setIssuer("localhost:8080")
	                    .setIssuedAt(new Date())
	                    .setExpiration(
							Date.from(
								LocalDateTime.now().plusMinutes(15L)
									.atZone(ZoneId.systemDefault())
								.toInstant()))
	                    .signWith(privateKey, SignatureAlgorithm.RS512)
	                    .compact();
	                
	                try {
	                	Jwts.parserBuilder()
						.setSigningKey(privateKey)
						.build()
						.parseClaimsJws(jwtToken);
	                	System.out.println("VALIDADO");
					} catch (Exception e) {
						System.out.println("NÃO VALIDADO");
					}
	                
	                return new ResponseEntity<UsuariotResDTO>(new UsuariotResDTO(new Usuario("","",jwtToken)),HttpStatus.OK);
	            }
	            else
	                return new ResponseEntity<UsuariotResDTO>(HttpStatus.UNAUTHORIZED);
	        }
	        catch(Exception ex)
	        {
	        	return new ResponseEntity<UsuariotResDTO>(HttpStatus.BAD_REQUEST);
	        } 
	   }
}

package tech.criasystem.authentication;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import tech.criasystem.model.UserLogin;

@Component
public class JwtTokenUtil implements Serializable {

	private static final long serialVersionUID = -5691522683311369609L;
	
	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

	private RSAPrivateKey getPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		Path resourceDirectory = Paths.get("src","main","resources");
		String absolutePath = resourceDirectory.toFile().getAbsolutePath();			
		Path path = Paths.get(absolutePath+"/"+"private.key");
		byte[] bytes = Files.readAllBytes(path);

		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateKey privateKey = (RSAPrivateKey) kf.generatePrivate(spec);
        return privateKey;
	}

	//retrieve username from jwt token
	public String getUsernameFromToken(String token) throws SignatureException, ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		return getClaimFromToken(token, Claims::getSubject);
	}

	//retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) throws SignatureException, ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) throws SignatureException, ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}
    //for retrieveing any information from token we will need the secret key
	private Claims getAllClaimsFromToken(String token) throws SignatureException, ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {
			return Jwts.parserBuilder()
					.setSigningKey(getPrivateKey())
					.build()
					.parseClaimsJws(token).getBody();
	}

	//check if the token has expired
	private Boolean isTokenExpired(String token) throws SignatureException, ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	//generate token for user
	public String generateToken(UserLogin user) {
		try {
			Map<String, Object> claims = new HashMap<>();
			claims.put("userId", user.getId());
			claims.put("userName", user.getUsername());
			String jwtToken = Jwts.builder()
	            	.setClaims(claims)
	                .setSubject(user.getUsername())
	                .setIssuer("localhost:8080")
	                .setIssuedAt(new Date())
	                .setExpiration(
						Date.from(
							LocalDateTime.now().plusMinutes(15L)
								.atZone(ZoneId.systemDefault())
							.toInstant()))
	                .signWith(getPrivateKey(), SignatureAlgorithm.RS512)
	                .compact();
			
			return jwtToken;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
		
	}

	//validate token
	public Boolean validateToken(String token) throws SignatureException, ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		try {
			Jwts.parserBuilder()
			.setSigningKey(getPrivateKey())
			.build()
			.parseClaimsJws(token);
			return !isTokenExpired(token);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

}

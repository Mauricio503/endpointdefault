package tech.criasystem.authentication;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.criasystem.model.Usuario;
import tech.criasystem.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	public Boolean autenticacao(Usuario usuario) throws NoSuchAlgorithmException {
		Usuario usuarioBD = usuarioRepository.findByUsuario(usuario.getUsuario());
		if(usuarioBD != null) {
			if(usuarioBD.getSenha().equals(encryptPassword(usuario.getSenha()))) {
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
	}
	
	private String encryptPassword(String senha) throws NoSuchAlgorithmException {
		return toHexString(getSHA(senha));
	}
	
	public static byte[] getSHA(String input) throws NoSuchAlgorithmException  
	    {  
	        MessageDigest md = MessageDigest.getInstance("SHA-512");  
	        return md.digest(input.getBytes(StandardCharsets.UTF_8));  
	    }  
	      
	public static String toHexString(byte[] hash)  
	    {  
	        BigInteger number = new BigInteger(1, hash);  
	        StringBuilder hexString = new StringBuilder(number.toString(16));  
	        while (hexString.length() < 32)  
	        {  
	            hexString.insert(0, '0');  
	        }  
	  
	        return hexString.toString();  
	    }  
	
	
}

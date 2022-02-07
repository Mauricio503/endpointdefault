package tech.criasystem.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import tech.criasystem.model.Usuario;
import tech.criasystem.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	public Boolean autenticacao(Usuario usuario) {
		Usuario usuarioBD = usuarioRepository.findByUsuario(usuario.getUsuario());
		if(usuarioBD != null) {
			if(usuarioBD.getSenha().equals(criptografarSenha(usuario.getSenha()))) {
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
	}
	
	private String criptografarSenha(String senha) {
		BCryptPasswordEncoder criptografar = new BCryptPasswordEncoder();
		return criptografar.encode(senha);
	}
	
	
}

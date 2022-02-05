package tech.criasystem.dto.res;

import java.io.Serializable;

import tech.criasystem.model.Usuario;

public class UsuariotResDTO implements Serializable{

	
	private static final long serialVersionUID = 1329653095132152345L;

	private String name;
	private String usuario;
	private String token;
	
	
	public UsuariotResDTO() {
	}

	public UsuariotResDTO(Usuario usuario) {
		super();
		this.name = usuario.getName();
		this.usuario = usuario.getUsuario();
		this.token = usuario.getToken();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
}
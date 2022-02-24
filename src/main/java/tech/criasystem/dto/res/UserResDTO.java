package tech.criasystem.dto.res;

import java.io.Serializable;

import tech.criasystem.model.UserLogin;

public class UserResDTO implements Serializable{

	
	private static final long serialVersionUID = 1329653095132152345L;

	private String name;
	private String username;
	private String token;
	
	
	public UserResDTO() {
	}

	public UserResDTO(UserLogin user) {
		super();
		this.name = user.getName();
		this.username = user.getUsername();
		this.token = user.getToken();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
}
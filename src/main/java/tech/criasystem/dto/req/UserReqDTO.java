package tech.criasystem.dto.req;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import tech.criasystem.model.UserLogin;

public class UserReqDTO implements Serializable {
	
	private static final long serialVersionUID = -3976244092928391770L;
	@NotBlank
	private String username;
	@NotBlank
	private String password;

	public UserReqDTO() {
	}
	
	public UserLogin toModel(UserLogin user) {
		user.setUsername(this.username);
		user.setPassword(this.password);
		return user;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
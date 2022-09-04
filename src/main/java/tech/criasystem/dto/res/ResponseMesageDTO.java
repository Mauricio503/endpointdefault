package tech.criasystem.dto.res;

public class ResponseMesageDTO {
	
	private String mesage;

	public ResponseMesageDTO() {
		super();
	}

	public ResponseMesageDTO(String mesage) {
		super();
		this.mesage = mesage;
	}

	public String getMesage() {
		return mesage;
	}

	public void setMesage(String mesage) {
		this.mesage = mesage;
	}
	
}

package tech.criasystem.dto;

import javax.validation.constraints.NotBlank;

public class TenantDTO{

	@NotBlank
	private String schema;

	public TenantDTO() {
		super();
	}

	public TenantDTO(@NotBlank String schema) {
		super();
		this.schema = schema;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}
	
}

package tech.criasystem.model;

import javax.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import tech.criasystem.DTOToModel;

import java.io.Serializable;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Tenant extends DTOToModel implements Serializable {

	private static final long serialVersionUID = -8779513307461561204L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
    private String schema;
	public Tenant() {
		super();
	}
	
	public Tenant(Long id, String schema) {
		super();
		this.id = id;
		this.schema = schema;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSchema() {
		return schema;
	}
	public void setSchema(String schema) {
		this.schema = schema;
	}
}
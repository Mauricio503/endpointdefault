package tech.criasystem.model;

import javax.persistence.*;
import javax.validation.constraints.Size;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class MasterTenant implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    private String schema;

    private String url;

    private String userName;
    private String password;
    private String driverClass;
    private String status;

    public MasterTenant() {
    }

    public MasterTenant(Long id, String schema, String url, String userName, String password, String driverClass,
			String status) {
		super();
		this.id = id;
		this.schema = schema;
		this.url = url;
		this.userName = userName;
		this.password = password;
		this.driverClass = driverClass;
		this.status = status;
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

	public String getUrl() {
        return url;
    }

    public MasterTenant setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public MasterTenant setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public MasterTenant setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public MasterTenant setDriverClass(String driverClass) {
        this.driverClass = driverClass;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public MasterTenant setStatus(String status) {
        this.status = status;
        return this;
    }
}

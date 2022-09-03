package tech.criasystem.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import tech.criasystem.model.Tenant;
import tech.criasystem.repository.TenantRepository;
import tech.criasystem.service.SchemaDDLService;

@Configuration
public class UpdateDataBase{

	@Autowired
	private TenantRepository tenantRepository;
	
	@Autowired
	private SchemaDDLService schemaDDLService;
	
	@EventListener(ApplicationReadyEvent.class)
	public void runUpdateAllSchema() {
	    try {
	    	Tenant tenantPublic = new Tenant();
	    	tenantPublic.setSchema("public");
	    	schemaDDLService.updateSchema(tenantPublic);
	    	tenantRepository.findAll().forEach(tenant -> {
	    		schemaDDLService.updateSchema(tenant);
		    });
		} catch (Exception e) {
			e.getStackTrace();
		}
	}
	
	
}

package tech.criasystem.config;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import tech.criasystem.model.MasterTenant;
import tech.criasystem.repository.MasterTenantRepository;
import tech.criasystem.util.DataSourceUtil;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Configuration
public class DataSourceBasedMultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceBasedMultiTenantConnectionProviderImpl.class);

    private static final long serialVersionUID = 1L;

    private Map<String, DataSource> dataSourcesMtApp = new TreeMap<>();
    
    @Autowired
    ApplicationContext applicationContext;

    @Override
    protected DataSource selectAnyDataSource() {
        if (dataSourcesMtApp.isEmpty()) {
        	List<MasterTenant> masterTenants =new ArrayList<>();
        	LOG.info("selectAnyDataSource() method call...Total tenants:" + masterTenants.size());
            MasterTenant masterTenant = new MasterTenant();
            masterTenant.setUserName("postgres");
            masterTenant.setPassword("postgres");
            masterTenant.setUrl("jdbc:postgresql://localhost:5434/endpointdefault");
            masterTenant.setDriverClass("org.postgresql.Driver");
            masterTenant.setSchema("public");
        	//for (MasterTenant masterTenant : masterTenants) {
                dataSourcesMtApp.put(masterTenant.getSchema(), DataSourceUtil.createAndConfigureDataSource(masterTenant));
                masterTenant.setSchema("test");
                dataSourcesMtApp.put(masterTenant.getSchema(), DataSourceUtil.createAndConfigureDataSource(masterTenant));
            //}
        }
        return this.dataSourcesMtApp.values().iterator().next();
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        tenantIdentifier = initializeTenantIfLost(tenantIdentifier);
        if (!this.dataSourcesMtApp.containsKey(tenantIdentifier)) {
        	MasterTenant masterTenant = new MasterTenant();
            masterTenant.setUserName("postgres");
            masterTenant.setPassword("postgres");
            masterTenant.setUrl("jdbc:postgresql://localhost:5434/endpointdefault");
            masterTenant.setDriverClass("org.postgresql.Driver");
            masterTenant.setSchema("public");
            //for (MasterTenant masterTenant : masterTenants) {
                dataSourcesMtApp.put(masterTenant.getSchema(), DataSourceUtil.createAndConfigureDataSource(masterTenant));
                masterTenant.setSchema("test");
                dataSourcesMtApp.put(masterTenant.getSchema(), DataSourceUtil.createAndConfigureDataSource(masterTenant));
            //}
        }
       if (!this.dataSourcesMtApp.containsKey(tenantIdentifier)) {
            LOG.warn("Trying to get tenant:" + tenantIdentifier + " which was not found in master db after rescan");
            throw new UsernameNotFoundException(String.format("Tenant not found after rescan, " + " tenant=%s", tenantIdentifier));
        }
        return this.dataSourcesMtApp.get(tenantIdentifier);
    }

    private String initializeTenantIfLost(String tenantIdentifier) {
        if (tenantIdentifier != DBContextHolder.getCurrentSchema()) {
            tenantIdentifier = DBContextHolder.getCurrentSchema();
        }
        if(tenantIdentifier == null) {
        	tenantIdentifier = "public";
        }
        return tenantIdentifier;
    }
}

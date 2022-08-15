package tech.criasystem.util;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.criasystem.model.MasterTenant;

import javax.sql.DataSource;

public final class DataSourceUtil {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceUtil.class);

    public static DataSource createAndConfigureDataSource(MasterTenant masterTenant) {
        HikariDataSource ds = new HikariDataSource();
        ds.setUsername(masterTenant.getUserName());
        ds.setPassword(masterTenant.getPassword());
        ds.setJdbcUrl(masterTenant.getUrl());
        ds.setDriverClassName(masterTenant.getDriverClass());
        ds.setConnectionTimeout(20000);
        ds.setMinimumIdle(3);
        ds.setMaximumPoolSize(500);
        ds.setIdleTimeout(300000);
        ds.setConnectionTimeout(20000);
        ds.setSchema(masterTenant.getSchema());
        //String tenantConnectionPoolName = masterTenant.getSchema() + "-connection-pool";
        //ds.setPoolName(tenantConnectionPoolName);
        //LOG.info("Configured datasource:" + masterTenant.getSchema() + ". Connection pool name:" + tenantConnectionPoolName);
        return ds;
    }
}

package tech.criasystem.multitenancy;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.service.spi.ServiceRegistryAwareService;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


public class MultitenantConnectionProvider implements MultiTenantConnectionProvider, ServiceRegistryAwareService {
	private static final long serialVersionUID = 694086551940351604L;
	private DatasourceConnectionProviderImpl connectionProvider = null;
	private static final Logger log = Logger.getLogger(MultitenantConnectionProvider.class.getName());
	
	@Qualifier("dataSource")
	@Autowired
	private DataSource dataSource;
	@Override
	public boolean supportsAggressiveRelease() {
		return false;
	}

	@Override
	public void injectServices(ServiceRegistryImplementor serviceRegistry) {
		Map<?, ?> lSettings = serviceRegistry.getService(ConfigurationService.class).getSettings();
		connectionProvider = new DatasourceConnectionProviderImpl();
		connectionProvider.setDataSource(dataSource);
		connectionProvider.configure(lSettings);
	}

	@Override
	public boolean isUnwrappableAs(@SuppressWarnings("rawtypes") Class clazz) {
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> clazz) {
		return null;
	}

	@Override
	public Connection getAnyConnection() throws SQLException {
		final Connection connection = connectionProvider.getConnection();
		return connection;
	}

	@Override
	public Connection getConnection(String tenantIdentifier) throws SQLException {
		log.debug("getting connection for tenant " + tenantIdentifier);
		final Connection connection = getAnyConnection();
		connection.setSchema(tenantIdentifier);
		return connection;
	}

	@Override
	public void releaseAnyConnection(Connection connection) throws SQLException {
		log.debug("releasing connection");
		connection.close();
	}

	@Override
	public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
		connection.setSchema(tenantIdentifier);
		releaseAnyConnection(connection);
	}
}
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
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;


public class MultitenantConnectionProvider implements MultiTenantConnectionProvider, ServiceRegistryAwareService {
	private static final String templateAlterSchema = "SET SCHEMA '%s'"; //USE + tenant para MySQL
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
		/*final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
		//DataSource dataSource = dsLookup.getDataSource("java:jboss/datasources/GerenciadorPRDDS");
		DataSource dataSource = DataSourceBuilder
		        .create()
		        .username("postgres")
		        .password("postgres")
		        .url("jdbc:postgresql://localhost:5434/endpointdefault")
		        .driverClassName("org.postgresql.Driver")
		        .build();*/
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
		/*Statement stmt = connection.createStatement();
		try {
			stmt.execute(String.format(templateAlterSchema, tenantIdentifier));
		} catch (SQLException e) {
			throw new HibernateException("Could not alter JDBC connection to specified schema [" + tenantIdentifier + "]", e);
		} finally {
			stmt.close();
		}*/
		return connection;
	}

	@Override
	public void releaseAnyConnection(Connection connection) throws SQLException {
		log.debug("releasing connection");
		connection.close();
		/*Statement stmt = connection.createStatement();
		try {
			stmt.execute("SET SCHEMA 'public'");
		} catch (SQLException e) {
			throw new HibernateException("Could not alter JDBC connection to specified schema [public]", e);
		} finally {
			stmt.close();
		}
		connectionProvider.closeConnection(connection);*/
	}

	@Override
	public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
		connection.setSchema(tenantIdentifier);
		releaseAnyConnection(connection);
	}
}
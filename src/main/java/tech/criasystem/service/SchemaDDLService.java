package tech.criasystem.service;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.sql.DataSource;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.schema.TargetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import tech.criasystem.config.UpdateDataBase;
import tech.criasystem.model.Tenant;
import tech.criasystem.multitenancy.NoTablesCreatedException;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class SchemaDDLService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SchemaDDLService.class);
	private static final String templateCreateSchema = "CREATE SCHEMA %s";
	private static final String templateDeleteSchema = "DROP SCHEMA %s CASCADE";
	public static final String schemaPublic = "public";
	private static final String templateSelectSchemas = "select schema_name from information_schema.schemata";
	private static final String templateSelectCountTables = "SELECT count(*) as count FROM information_schema.tables WHERE table_schema = ?";
	private static final String schameNameField = "schema_name";
	private static Logger log = LoggerFactory.getLogger(UpdateDataBase.class);
	private final String entityPackage = "tech.criasystem*";
	
	@Autowired
	private Environment environment;
	@Autowired
	private DataSource dataSource;

	public void createSchema(Tenant entidade) throws SQLException {
		Connection connection = dataSource.getConnection();
		Statement stmt = connection.createStatement();
		try {
			final String schema = entidade.getSchema();			
			boolean existeSchema = isSchemaPresent(schema, connection);

			if (!existeSchema) {
				LOGGER.info("Creating schema " + schema);
				stmt.execute(String.format(templateCreateSchema, schema));
				LOGGER.info("Schema " + schema + " created.");
			}
			updateSchema(entidade);
			boolean tablesCreated = containsTablesInSchema(schema, connection);
			if (!tablesCreated) {
				throw new NoTablesCreatedException("Nao foi possivel criar as tabelas no novo schema.");
			}
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void updateSchema(Tenant entidade) {
		Map<String, String> settings = new HashMap();
		settings.put("connection.driver_class", environment.getProperty("endpointdefault.datasource.driverClassName"));
		settings.put("dialect", environment.getProperty("endpointdefault.datasource.dialect"));
		settings.put("hibernate.connection.url", environment.getProperty("endpointdefault.datasource.url"));
		settings.put("hibernate.connection.username", environment.getProperty("endpointdefault.datasource.username"));
		settings.put("hibernate.connection.password", environment.getProperty("endpointdefault.datasource.password"));
		settings.put("hibernate.hbm2ddl.auto", "update");
		settings.put(AvailableSettings.DEFAULT_SCHEMA, entidade.getSchema());
		MetadataSources metadata = metaDataClasses(settings);
		EnumSet<TargetType> enumSet = EnumSet.of(TargetType.DATABASE);
		SchemaUpdate schemaUpdate = new SchemaUpdate();
		schemaUpdate.execute(enumSet, metadata.buildMetadata());
	}
	
	private MetadataSources metaDataClasses(Map<String,String> settings) {
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
		provider.addIncludeFilter(new AnnotationTypeFilter(MappedSuperclass.class));
	    Set<BeanDefinition> components = provider.findCandidateComponents(entityPackage);

		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
	            .applySettings(settings).build();
		
		MetadataSources metadata = new MetadataSources(serviceRegistry);
		for (BeanDefinition beanDefinition : components) {
			try {
				Class<?> cls = Class.forName(beanDefinition.getBeanClassName());
				metadata.addAnnotatedClass(cls);
				log.info("Mapped = " + cls.getName());
			} catch (ClassNotFoundException e) {
				log.error("Erro", e);
			}
		}
		return metadata;
	}

	public void deleteSchema(Tenant entidade) throws SQLException {
		final String schema = entidade.getSchema();
		Connection connection = dataSource.getConnection();
		try {
			boolean existeSchema = isSchemaPresent(schema, connection);

			if (existeSchema) {
				LOGGER.info("Creating schema " + schema);
				try(Statement stmt = connection.createStatement()){
					stmt.execute(String.format(templateDeleteSchema, schema));
				}
				LOGGER.info("Schema " + schema + " excluido.");
			}
		} finally {
			connection.close();
		}

	}
	public boolean isSchemaPresent(String schemaName, Connection connection) throws SQLException {
		try(ResultSet resultSet = connection.prepareStatement(templateSelectSchemas).executeQuery()){
			while (resultSet.next()) {
				if (resultSet.getString(schameNameField).equalsIgnoreCase(schemaName)) {
					return true;
				}
			}
			return false;
		}
	}

	public boolean containsTablesInSchema(String schemaName, Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(templateSelectCountTables);
		statement.setString(1, schemaName);
		
		try(ResultSet resultSet = statement.executeQuery()){
			while (resultSet.next()) {
				long count = resultSet.getLong("count");
				if (count != 0) {
					return true;
				}
			}
			return false;
		} finally {
			statement.close();
		}
	}
}

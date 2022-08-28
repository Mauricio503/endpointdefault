package tech.criasystem.multitenancy;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import org.hibernate.HibernateException;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
//import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
//import org.hibernate.tool.hbm2ddl.SchemaUpdateScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

@Component
public class HibernatePostgreDDLExporter implements DDLExporter {
	private static Logger log = LoggerFactory.getLogger(HibernatePostgreDDLExporter.class);

	private final String dialect;
	private final String entityPackage;

	private Configuration hibernateConfiguration;

	public HibernatePostgreDDLExporter() {
		this.dialect = "org.hibernate.dialect.PostgreSQLDialect";
		this.entityPackage = "tech.criasystem*";
	}

	@Override
	public List<SQLExportCommand> exportCreate(String schemaName) {
		hibernateConfiguration = createHibernateConfig();
		List<SQLExportCommand> commands = new ArrayList<>();
		hibernateConfiguration.getProperties().setProperty(AvailableSettings.DEFAULT_SCHEMA, schemaName);

//		Dialect hibDialect = Dialect.getDialect(hibernateConfiguration.getProperties());
//		String[] createSQL = hibernateConfiguration.generateSchemaCreationScript(hibDialect);

		//for (String line : createSQL) {
		//	commands.add(new SQLExportCommand(line));
		//}
		//return commands;
		return null;
	}

	@Override
	public List<SQLExportCommand> exportUpdate(Connection connection, String schemaName) {
		/*hibernateConfiguration = createHibernateConfig();
		List<SQLExportCommand> commands = new ArrayList<>();
		Dialect hibDialect = Dialect.getDialect(hibernateConfiguration.getProperties());
		hibernateConfiguration.getProperties().setProperty(AvailableSettings.DEFAULT_SCHEMA, schemaName);

		try {
			List<SchemaUpdateScript> update = hibernateConfiguration.generateSchemaUpdateScriptList(hibDialect, new DatabaseMetadata(connection, hibDialect,
					hibernateConfiguration));

			for (SchemaUpdateScript schemaUpdateScript : update) {
				commands.add(new SQLExportCommand(schemaUpdateScript.getScript()));
			}
		} catch (HibernateException | SQLException e) {
			e.printStackTrace();
		}

		return commands;*/
		return null;
	}

	private Configuration createHibernateConfig() {
		hibernateConfiguration = new Configuration();
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
		provider.addIncludeFilter(new AnnotationTypeFilter(MappedSuperclass.class));
		Set<BeanDefinition> components = provider.findCandidateComponents(entityPackage);

		for (BeanDefinition beanDefinition : components) {
			try {
				Class<?> cls = Class.forName(beanDefinition.getBeanClassName());
				hibernateConfiguration.addAnnotatedClass(cls);
				log.info("Mapped = " + cls.getName());
			} catch (ClassNotFoundException e) {
				log.error("Erro", e);
			}
		}

		hibernateConfiguration.setProperty(AvailableSettings.DIALECT, dialect);
		return hibernateConfiguration;
	}

	public Configuration getHibernateConfiguration() {
		return hibernateConfiguration;
	}

}
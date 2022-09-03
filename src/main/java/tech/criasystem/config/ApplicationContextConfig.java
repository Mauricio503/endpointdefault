package tech.criasystem.config;


import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan("tech.criasystem*")
@EnableTransactionManagement
@EnableAspectJAutoProxy
@EnableJpaRepositories(basePackages = { "tech.criasystem.repository"})
public class ApplicationContextConfig {
	
	@Autowired
	private Environment environment;
	
	@Bean(name = "dataSource")
    public DataSource dataSource() throws NamingException {
		return DataSourceBuilder
		        .create()
		        .username(environment.getProperty("endpointdefault.datasource.username"))
		        .password(environment.getProperty("endpointdefault.datasource.password"))
		        .url(environment.getProperty("endpointdefault.datasource.url"))
		        .driverClassName(environment.getProperty("endpointdefault.datasource.driverClassName"))
		        .build();
    }
	
	@Bean(name = "entityManagerFactory")
	public EntityManagerFactory entityManagerFactory() throws NamingException {
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		bean.setPersistenceUnitName("endpointdefault");
		bean.setDataSource(dataSource());
		bean.setJpaDialect(jpaDialect());
		bean.setJpaVendorAdapter(jpaVendorAdapter());
		configureMultitenancy(bean);

		bean.afterPropertiesSet();
		return bean.getObject();
	}

	private void configureMultitenancy(LocalContainerEntityManagerFactoryBean bean) {
		bean.getJpaPropertyMap().put("hibernate.hbm2ddl.auto", "update");
		bean.getJpaPropertyMap().put("hibernate.multiTenancy", "SCHEMA");
		bean.getJpaPropertyMap().put("hibernate.tenant_identifier_resolver", "tech.criasystem.multitenancy.MultitenantSchemaResolver");
		bean.getJpaPropertyMap().put("hibernate.multi_tenant_connection_provider", "tech.criasystem.multitenancy.MultitenantConnectionProvider");
		bean.getJpaPropertyMap().put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		bean.getJpaPropertyMap().put("hibernate.connection.show_sql", "true");
		bean.getJpaPropertyMap().put("hibernate.generate_statistics", "false");
		bean.getJpaPropertyMap().put("hibernate.default_batch_fetch_size", "500");
		bean.getJpaPropertyMap().put("hibernate.max_fetch_depth", "5");
		bean.getJpaPropertyMap().put("hibernate.jdbc.batch_size", "1000");
		bean.getJpaPropertyMap().put("hibernate.use_outer_join", "true");
		bean.getJpaPropertyMap().put("hibernate.archive.autodetection", "class");
		bean.getJpaPropertyMap().put("hibernate.enable_lazy_load_no_trans", "true");
	}

	@Bean(name = "transactionManager")
	public JpaTransactionManager transactionManager() throws NamingException {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager(entityManagerFactory());
		jpaTransactionManager.setJpaDialect(jpaDialect());
		return jpaTransactionManager;
	}
	
	@Bean
	public JpaDialect jpaDialect() {
		JpaDialect jpaDialect = new HibernateJpaDialect();

		return jpaDialect;
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		JpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		return jpaVendorAdapter;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslationPostProcessor() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	@Bean
	public HibernateExceptionTranslator exceptionTranslator() {
		return new HibernateExceptionTranslator();
	}
}

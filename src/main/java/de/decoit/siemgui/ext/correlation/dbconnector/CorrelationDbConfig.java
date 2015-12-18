/* 
 * Copyright (C) 2015 DECOIT GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.decoit.siemgui.ext.correlation.dbconnector;

import java.util.Properties;
import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;


/**
 *
 * @author Thomas Rix (rix@decoit.de)
 */
@Configuration
@ComponentScan(basePackages = { "de.decoit.siemgui.ext.correlation.dbconnector.services" })
@EnableJpaRepositories(basePackages = { "de.decoit.siemgui.ext.correlation.dbconnector.repositories" },
					   entityManagerFactoryRef = "correlationEntityManagerFactory",
					   transactionManagerRef = "correlationTransactionManager")
@PropertySource({ "classpath:correlation-db-connector.properties" })
public class CorrelationDbConfig {
	private final Logger LOG = LoggerFactory.getLogger(CorrelationDbConfig.class.getName());

	@Resource
	private Environment env;


	@Bean
	@Qualifier("correlationDataSource")
	public DataSource correlationDataSource() {
		BasicDataSource dataSource = new BasicDataSource();

		dataSource.setDriverClassName(env.getProperty("correlation.jdbc.driver"));
		dataSource.setUrl(env.getProperty("correlation.jdbc.url"));

		if(env.getProperty("correlation.jdbc.user") != null && env.getProperty("correlation.jdbc.user").length() > 0) {
			dataSource.setUsername(env.getProperty("correlation.jdbc.user"));
		}
		if(env.getProperty("correlation.jdbc.pass") != null && env.getProperty("correlation.jdbc.pass").length() > 0) {
			dataSource.setPassword(env.getProperty("correlation.jdbc.pass"));
		}

		return dataSource;
	}


	@Bean
	@Qualifier("correlationEntityManagerFactory")
	public EntityManagerFactory correlationEntityManagerFactory() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

		vendorAdapter.setDatabase(determineDatabase());
		vendorAdapter.setGenerateDdl(Boolean.parseBoolean(env.getProperty("correlation.vendoradapter.generateddl")));
		vendorAdapter.setShowSql(Boolean.parseBoolean(env.getProperty("correlation.vendoradapter.showsql")));

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setPersistenceUnitName("correlationPU");
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan("de.decoit.simu.incidents.entities");
		factory.setDataSource(correlationDataSource());
		factory.setJpaProperties(hibernateProperties());
		factory.afterPropertiesSet();

		return factory.getObject();
	}


	@Bean
	@Qualifier("correlationTransactionManager")
	public PlatformTransactionManager correlationTransactionManager() {
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(correlationEntityManagerFactory());

		return txManager;
	}


	private Database determineDatabase() {
		switch(env.getProperty("correlation.vendoradapter.database")) {
			case "mysql":
				LOG.debug("Using Database.MYSQL");
				return Database.MYSQL;
			case "postgresql":
				LOG.debug("Using Database.POSTGRESQL");
				return Database.POSTGRESQL;
			default:
				LOG.warn("Using Database.DEFAULT, please set 'correlation.vendoradapter.database' property according to your DBMS. Allowed values: 'mysql', 'postgresql'");
				return Database.DEFAULT;
		}
	}


	private Properties hibernateProperties() {
		Properties prop = new Properties();

		prop.setProperty("hibernate.globally_quoted_identifiers", env.getProperty("correlation.hibernate.globally_quoted_identifiers"));
		prop.setProperty("hibernate.hbm2ddl.auto", env.getProperty("correlation.hibernate.hbm2ddl.auto"));

		return prop;
	}
}

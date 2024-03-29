package com.hostfully.booking.config;

import lombok.extern.slf4j.Slf4j;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@Slf4j
@EnableTransactionManagement
public class DatasourceConfig {

	private final DataSource dataSource;

	public DatasourceConfig(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Bean
	public DataSourceConnectionProvider connectionProvider() {
		return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource));
	}

	@Bean
	public DefaultDSLContext dsl(@Value("${spring.datasource.dialect}") final String dialect) {
		log.info("Creating JOOQ config using dialect: {}", dialect);
		final DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
		jooqConfiguration.set(connectionProvider());
		jooqConfiguration.set(SQLDialect.valueOf(dialect));
		return new DefaultDSLContext(jooqConfiguration);
	}
}

package com.crediya.r2dbc.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import java.time.Duration;

@Configuration
public class PostgreSQLConnectionPool {
    /* Change these values for your project */
    public static final int INITIAL_SIZE = 12;
    public static final int MAX_SIZE = 15;
    public static final int MAX_IDLE_TIME = 30;
    public static final int DEFAULT_PORT = 5432;

	@Bean
	public ConnectionPool getConnectionConfig(PostgresqlConnectionProperties properties) {
		// Use ConnectionFactoryBuilder for proper Spring Boot integration
		String url = String.format("r2dbc:postgresql://%s:%d/%s?currentSchema=%s", 
			properties.host(), 
			properties.port(), 
			properties.database(),
			properties.schema());
		
		ConnectionFactory connectionFactory = ConnectionFactoryBuilder.withUrl(url)
				.username(properties.username())
				.password(properties.password())
				.build();

        ConnectionPoolConfiguration poolConfiguration = ConnectionPoolConfiguration.builder()
                .connectionFactory(connectionFactory)
                .name("api-postgres-connection-pool")
                .initialSize(INITIAL_SIZE)
                .maxSize(MAX_SIZE)
                .maxIdleTime(Duration.ofMinutes(MAX_IDLE_TIME))
                .validationQuery("SELECT 1")
                .build();

		return new ConnectionPool(poolConfiguration);
	}

    @Bean
    public ReactiveTransactionManager reactiveTransactionManager(ConnectionPool connectionPool) {
        return new R2dbcTransactionManager(connectionPool);
    }

    @Bean
    public TransactionalOperator transactionalOperator(ReactiveTransactionManager transactionManager) {
        return TransactionalOperator.create(transactionManager);
    }
}
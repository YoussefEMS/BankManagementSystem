package com.bms.persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * DataSourceFactory - provides HikariCP connection pool
 * Singleton pattern to manage database connection pooling
 */
public class DataSourceFactory {
    private static DataSource dataSource;
    private static final String PROPERTIES_FILE = "application.properties";

    private DataSourceFactory() {
        // Private constructor to prevent instantiation
    }

    /**
     * Get the shared DataSource instance (lazy initialization)
     */
    public static synchronized DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = createDataSource();
        }
        return dataSource;
    }

    /**
     * Create HikariCP DataSource from application.properties
     */
    private static DataSource createDataSource() {
        Properties props = loadProperties();
        
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("jdbc.url"));
        config.setUsername(props.getProperty("jdbc.user"));
        config.setPassword(props.getProperty("jdbc.password"));
        config.setDriverClassName(props.getProperty("jdbc.driver"));
        
        // HikariCP settings
        config.setMaximumPoolSize(Integer.parseInt(
            props.getProperty("hikari.maximumPoolSize", "10")));
        config.setMinimumIdle(Integer.parseInt(
            props.getProperty("hikari.minimumIdle", "2")));
        config.setIdleTimeout(Long.parseLong(
            props.getProperty("hikari.idleTimeout", "300000")));
        config.setMaxLifetime(Long.parseLong(
            props.getProperty("hikari.maxLifetime", "1200000")));
        config.setConnectionTimeout(Long.parseLong(
            props.getProperty("hikari.connectionTimeout", "30000")));

        return new HikariDataSource(config);
    }

    /**
     * Load properties from application.properties file
     */
    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = DataSourceFactory.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {
            if (input != null) {
                props.load(input);
            } else {
                throw new RuntimeException("application.properties not found in classpath");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
        return props;
    }

    /**
     * Shutdown the data source (call on application exit)
     */
    public static void shutdown() {
        if (dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).close();
        }
    }
}

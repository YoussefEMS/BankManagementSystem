package com.bms.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * DataSourceFactory - provides HikariCP connection pool
 * Singleton pattern to manage database connection pooling
 */
public class DataSourceFactory {

    // UML: -instance: DataSourceFactory  (static + private)
    private static volatile DataSourceFactory instance;

    // UML: -dataSource: DataSource (private, NOT static)
    private final DataSource dataSource;

    private static final String PROPERTIES_FILE = "application.properties";

    // UML: -DataSourceFactory() (private constructor)
    private DataSourceFactory() {
        this.dataSource = createDataSource();
    }

    // UML: +getInstance(): DataSourceFactory (static, public)
    public static DataSourceFactory getInstance() {
        if (instance == null) {
            synchronized (DataSourceFactory.class) {
                if (instance == null) {
                    instance = new DataSourceFactory();
                }
            }
        }
        return instance;
    }

    // UML: +getDataSource(): DataSource (public, non-static)
    public DataSource getDataSource() {
        return dataSource;
    }

    // keep as helper (private static is fine)
    private static DataSource createDataSource() {
        Properties props = loadProperties();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("jdbc.url"));
        config.setUsername(props.getProperty("jdbc.user"));
        config.setPassword(props.getProperty("jdbc.password"));
        config.setDriverClassName(props.getProperty("jdbc.driver"));

        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("hikari.maximumPoolSize", "10")));
        config.setMinimumIdle(Integer.parseInt(props.getProperty("hikari.minimumIdle", "2")));
        config.setIdleTimeout(Long.parseLong(props.getProperty("hikari.idleTimeout", "300000")));
        config.setMaxLifetime(Long.parseLong(props.getProperty("hikari.maxLifetime", "1200000")));
        config.setConnectionTimeout(Long.parseLong(props.getProperty("hikari.connectionTimeout", "30000")));

        return new HikariDataSource(config);
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = DataSourceFactory.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
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

    // Optional: keep shutdown, but it now closes the instance-owned pool.
    public static synchronized void shutdown() {
        if (instance != null && instance.dataSource instanceof HikariDataSource) {
            ((HikariDataSource) instance.dataSource).close();
        }
    }
}
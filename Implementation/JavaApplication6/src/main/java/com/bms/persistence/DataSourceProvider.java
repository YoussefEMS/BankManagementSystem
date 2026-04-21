package com.bms.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import com.bms.persistence.DatabaseConnectionProvider;
import com.bms.persistence.DatabaseConnectionProviderSelector;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * DataSourceProvider - provides HikariCP connection pool
 * Singleton pattern to manage database connection pooling
 */
public class DataSourceProvider {

    // UML: -instance: DataSourceProvider  (static + private)
    private static volatile DataSourceProvider instance;

    // UML: -dataSource: DataSource (private, NOT static)
    private final DataSource dataSource;
    private final DatabaseConnectionProvider databaseConnectionProvider;

    private static final String PROPERTIES_FILE = "application.properties";

    // UML: -DataSourceProvider() (private constructor)
    private DataSourceProvider() {
        Properties props = loadProperties();
        this.databaseConnectionProvider = DatabaseConnectionProviderSelector.resolve(
                props.getProperty("jdbc.databaseType"),
                props.getProperty("jdbc.url"),
                props.getProperty("jdbc.driver"));
        this.dataSource = createDataSource(props);
    }

    // UML: +getInstance(): DataSourceProvider (static, public)
    public static DataSourceProvider getInstance() {
        if (instance == null) {
            synchronized (DataSourceProvider.class) {
                if (instance == null) {
                    instance = new DataSourceProvider();
                }
            }
        }
        return instance;
    }

    // UML: +getDataSource(): DataSource (public, non-static)
    public DataSource getDataSource() {
        return dataSource;
    }

    public DatabaseConnectionProvider getDatabaseConnectionProvider() {
        return databaseConnectionProvider;
    }

    // keep as helper (private static is fine)
    private static DataSource createDataSource(Properties props) {
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
        try (InputStream input = DataSourceProvider.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
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

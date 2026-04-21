package com.bms.persistence;

/**
 * Factory for database adapter instances.
 */
public final class DatabaseConnectionProviderSelector {

    public enum DatabaseType {
        MYSQL, ORACLE, POSTGRESQL, SQLSERVER
    }

    private DatabaseConnectionProviderSelector() {
    }

    public static DatabaseConnectionProvider getProvider(DatabaseType dbType) {
        switch (dbType) {
            case MYSQL:
                return new MySQLConnectionProvider();
            case ORACLE:
                return new OracleConnectionProvider();
            case POSTGRESQL:
                return new PostgreSQLConnectionProvider();
            case SQLSERVER:
                return new SqlServerConnectionProvider();
            default:
                throw new IllegalArgumentException("Unknown database type: " + dbType);
        }
    }

    public static DatabaseConnectionProvider getProvider(String databaseType) {
        return getProvider(DatabaseType.valueOf(databaseType.trim().toUpperCase()));
    }

    public static DatabaseConnectionProvider resolve(String explicitType, String jdbcUrl, String jdbcDriver) {
        if (explicitType != null && !explicitType.isBlank()) {
            return getProvider(explicitType);
        }

        String normalizedDriver = jdbcDriver == null ? "" : jdbcDriver.toLowerCase();
        String normalizedUrl = jdbcUrl == null ? "" : jdbcUrl.toLowerCase();

        if (normalizedDriver.contains("sqlserver") || normalizedUrl.startsWith("jdbc:sqlserver:")) {
            return getProvider(DatabaseType.SQLSERVER);
        }
        if (normalizedDriver.contains("postgresql") || normalizedUrl.startsWith("jdbc:postgresql:")) {
            return getProvider(DatabaseType.POSTGRESQL);
        }
        if (normalizedDriver.contains("oracle") || normalizedUrl.startsWith("jdbc:oracle:")) {
            return getProvider(DatabaseType.ORACLE);
        }
        if (normalizedDriver.contains("mysql") || normalizedUrl.startsWith("jdbc:mysql:")) {
            return getProvider(DatabaseType.MYSQL);
        }

        throw new IllegalArgumentException("Unable to resolve database adapter from configuration");
    }
}

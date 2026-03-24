package com.bms.persistence.adapter;

/**
 * Factory for database adapter instances.
 */
public final class DatabaseAdapterFactory {

    public enum DatabaseType {
        MYSQL, ORACLE, POSTGRESQL, SQLSERVER
    }

    private DatabaseAdapterFactory() {
    }

    public static DatabaseAdapter getAdapter(DatabaseType dbType) {
        switch (dbType) {
            case MYSQL:
                return new MySQLAdapter();
            case ORACLE:
                return new OracleAdapter();
            case POSTGRESQL:
                return new PostgreSQLAdapter();
            case SQLSERVER:
                return new SqlServerAdapter();
            default:
                throw new IllegalArgumentException("Unknown database type: " + dbType);
        }
    }

    public static DatabaseAdapter getAdapter(String databaseType) {
        return getAdapter(DatabaseType.valueOf(databaseType.trim().toUpperCase()));
    }

    public static DatabaseAdapter resolve(String explicitType, String jdbcUrl, String jdbcDriver) {
        if (explicitType != null && !explicitType.isBlank()) {
            return getAdapter(explicitType);
        }

        String normalizedDriver = jdbcDriver == null ? "" : jdbcDriver.toLowerCase();
        String normalizedUrl = jdbcUrl == null ? "" : jdbcUrl.toLowerCase();

        if (normalizedDriver.contains("sqlserver") || normalizedUrl.startsWith("jdbc:sqlserver:")) {
            return getAdapter(DatabaseType.SQLSERVER);
        }
        if (normalizedDriver.contains("postgresql") || normalizedUrl.startsWith("jdbc:postgresql:")) {
            return getAdapter(DatabaseType.POSTGRESQL);
        }
        if (normalizedDriver.contains("oracle") || normalizedUrl.startsWith("jdbc:oracle:")) {
            return getAdapter(DatabaseType.ORACLE);
        }
        if (normalizedDriver.contains("mysql") || normalizedUrl.startsWith("jdbc:mysql:")) {
            return getAdapter(DatabaseType.MYSQL);
        }

        throw new IllegalArgumentException("Unable to resolve database adapter from configuration");
    }
}

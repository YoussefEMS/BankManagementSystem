package com.bms.persistence.adapter;

import java.sql.Connection;

/**
 * Interface for database-specific SQL operations.
 */
public interface DatabaseAdapter {

    String getDatabaseName();

    String buildPaginatedQuery(String baseQuery, int offset, int limit);

    String buildInsertQuery(String tableName, String[] columns);

    long getLastInsertedId(Connection connection, String tableName) throws Exception;

    String getTransactionIsolationLevelName();

    boolean supportsFeature(String feature);

    String getCurrentTimestampFunction();

    String formatDate(String date, String format);
}

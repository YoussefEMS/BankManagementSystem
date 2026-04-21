package com.bms.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PostgreSQLConnectionProvider implements DatabaseConnectionProvider {

    @Override
    public String getDatabaseName() {
        return "PostgreSQL";
    }

    @Override
    public String buildPaginatedQuery(String baseQuery, int offset, int limit) {
        return baseQuery + " LIMIT " + limit + " OFFSET " + offset;
    }

    @Override
    public String buildInsertQuery(String tableName, String[] columns) {
        StringBuilder query = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                query.append(", ");
            }
            query.append(columns[i]);
        }
        query.append(") VALUES (");
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                query.append(", ");
            }
            query.append("?");
        }
        query.append(") RETURNING id");
        return query.toString();
    }

    @Override
    public long getLastInsertedId(Connection connection, String tableName) throws Exception {
        String sequenceName = tableName.toLowerCase() + "_id_seq";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT currval('" + sequenceName + "')")) {
            rs.next();
            return rs.getLong(1);
        }
    }

    @Override
    public String getTransactionIsolationLevelName() {
        return "READ_COMMITTED";
    }

    @Override
    public boolean supportsFeature(String feature) {
        switch (feature.toLowerCase()) {
            case "sequences":
            case "window_functions":
            case "cte":
            case "json":
            case "arrays":
            case "hstore":
            case "full_text_search":
            case "materialized_views":
            case "partitioning":
                return true;
            default:
                return false;
        }
    }

    @Override
    public String getCurrentTimestampFunction() {
        return "CURRENT_TIMESTAMP";
    }

    @Override
    public String formatDate(String date, String format) {
        return "TO_CHAR(" + date + ", '" + convertFormatToPostgreSQL(format) + "')";
    }

    private String convertFormatToPostgreSQL(String genericFormat) {
        return genericFormat
                .replace("HH", "HH24")
                .replace("mm", "MI");
    }
}

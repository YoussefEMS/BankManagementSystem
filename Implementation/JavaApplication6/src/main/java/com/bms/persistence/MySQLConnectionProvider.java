package com.bms.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MySQLConnectionProvider implements DatabaseConnectionProvider {

    @Override
    public String getDatabaseName() {
        return "MySQL";
    }

    @Override
    public String buildPaginatedQuery(String baseQuery, int offset, int limit) {
        return baseQuery + " LIMIT " + offset + ", " + limit;
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
        query.append(")");
        return query.toString();
    }

    @Override
    public long getLastInsertedId(Connection connection, String tableName) throws Exception {
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID()")) {
            rs.next();
            return rs.getLong(1);
        }
    }

    @Override
    public String getTransactionIsolationLevelName() {
        return "REPEATABLE_READ";
    }

    @Override
    public boolean supportsFeature(String feature) {
        switch (feature.toLowerCase()) {
            case "window_functions":
            case "cte":
            case "json":
            case "generated_columns":
                return true;
            case "sequences":
            default:
                return false;
        }
    }

    @Override
    public String getCurrentTimestampFunction() {
        return "NOW()";
    }

    @Override
    public String formatDate(String date, String format) {
        return "DATE_FORMAT(" + date + ", '" + convertFormatToMySQL(format) + "')";
    }

    private String convertFormatToMySQL(String genericFormat) {
        return genericFormat
                .replace("YYYY", "%Y")
                .replace("MM", "%m")
                .replace("DD", "%d")
                .replace("HH", "%H")
                .replace("mm", "%i")
                .replace("ss", "%s");
    }
}

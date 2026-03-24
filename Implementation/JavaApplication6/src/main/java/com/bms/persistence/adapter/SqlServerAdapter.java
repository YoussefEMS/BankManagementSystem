package com.bms.persistence.adapter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SqlServerAdapter implements DatabaseAdapter {

    @Override
    public String getDatabaseName() {
        return "SQLSERVER";
    }

    @Override
    public String buildPaginatedQuery(String baseQuery, int offset, int limit) {
        return baseQuery + " OFFSET " + offset + " ROWS FETCH NEXT " + limit + " ROWS ONLY";
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
                ResultSet rs = stmt.executeQuery("SELECT IDENT_CURRENT('" + tableName + "')")) {
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
            case "identity":
            case "window_functions":
            case "cte":
            case "json":
            case "offset_fetch":
                return true;
            default:
                return false;
        }
    }

    @Override
    public String getCurrentTimestampFunction() {
        return "GETDATE()";
    }

    @Override
    public String formatDate(String date, String format) {
        return "FORMAT(" + date + ", '" + format + "')";
    }
}

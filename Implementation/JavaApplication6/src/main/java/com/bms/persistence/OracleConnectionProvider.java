package com.bms.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class OracleConnectionProvider implements DatabaseConnectionProvider {

    @Override
    public String getDatabaseName() {
        return "Oracle";
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
            query.append(i == 0 ? tableName.toUpperCase() + "_SEQ.NEXTVAL" : "?");
        }
        query.append(")");
        return query.toString();
    }

    @Override
    public long getLastInsertedId(Connection connection, String tableName) throws Exception {
        String sequenceName = tableName.toUpperCase() + "_SEQ";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT " + sequenceName + ".CURRVAL FROM DUAL")) {
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
            case "json_path":
            case "partition_by":
                return true;
            default:
                return false;
        }
    }

    @Override
    public String getCurrentTimestampFunction() {
        return "SYSDATE";
    }

    @Override
    public String formatDate(String date, String format) {
        return "TO_CHAR(" + date + ", '" + convertFormatToOracle(format) + "')";
    }

    private String convertFormatToOracle(String genericFormat) {
        return genericFormat
                .replace("HH", "HH24")
                .replace("mm", "MI");
    }
}

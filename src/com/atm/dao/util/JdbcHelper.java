package com.atm.dao.util;

import com.atm.dao.DB;
import com.atm.exception.DatabaseException;
import com.atm.exception.RecordNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcHelper {
    // Helper for better maintenance. Deals with simple sql updates.
    @FunctionalInterface
    public interface PreparedStatementSetter {
        void setValues(PreparedStatement ps) throws SQLException;
    }
    // This helper is for when there is an output
    @FunctionalInterface
    public interface RowMapper<Output> {
        Output mapRow(ResultSet rs) throws SQLException;
    }

    public static void executeUpdate(String sql, PreparedStatementSetter binder, String errorMessage) {
        try (Connection con = DB.connect();
             PreparedStatement myStmt = con.prepareStatement(sql)) {

            binder.setValues(myStmt);

            int wasUpdated = myStmt.executeUpdate();
            if (wasUpdated == 0){
                throw new RecordNotFoundException(errorMessage);
            }
            if (wasUpdated > 1) {
                throw new DatabaseException(
                        "Unexpected update: multiple rows affected (" + wasUpdated + "). Check SQL or database integrity.");
            }
        }
        catch (SQLException e) {
            throw new DatabaseException("There was a problem with the database: ", e);
        }
    }

    public static <Output> Output executeQueryReturnOne(String sql, PreparedStatementSetter binder, RowMapper<Output> mapper, String errorMessage) {
        try (Connection con = DB.connect();
             PreparedStatement myStmt = con.prepareStatement(sql)) {

            binder.setValues(myStmt);

            try(ResultSet rs = myStmt.executeQuery()){
                if (rs.next()) {
                    return mapper.mapRow(rs);
                } else {
                    throw new RecordNotFoundException(errorMessage);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(errorMessage, e);
        }
    }

    public static boolean executeExists(String sql, PreparedStatementSetter binder, String errorMessage) {
        try (Connection con = DB.connect();
             PreparedStatement myStmt = con.prepareStatement(sql)) {

            binder.setValues(myStmt);

            try(ResultSet rs = myStmt.executeQuery()){
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException(errorMessage, e);
        }
    }
}

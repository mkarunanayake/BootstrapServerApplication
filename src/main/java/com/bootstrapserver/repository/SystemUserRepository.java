package com.bootstrapserver.repository;

import com.bootstrapserver.util.DBConnection;
import com.bootstrapserver.util.SystemUser;

import java.sql.*;

public class SystemUserRepository {
    private static SystemUserRepository systemUserRepository;

    public static SystemUserRepository getSystemUserRepository() {
        if (systemUserRepository == null) {
            synchronized (SystemUserRepository.class) {
                systemUserRepository = new SystemUserRepository();
            }
        }
        return systemUserRepository;
    }

    private DBConnection dbConnection;

    private SystemUserRepository() {
        dbConnection = DBConnection.getDbConnection();
    }

    public void updateSystemUserPassword(String password) {
        String updateUser = "UPDATE sys_user SET password = ? WHERE username = ?";
        Connection connection = dbConnection.getConnection();
        try {
            PreparedStatement updateStatement = connection.prepareStatement(updateUser);
            updateStatement.setString(1, password);
            updateStatement.setString(2, "admin");
            updateStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getSystemUser() {
        String getQuery = "SELECT * FROM sys_user";
        Connection connection = dbConnection.getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(getQuery);
            while (rs.next()) {
                SystemUser.setUsername(rs.getString("username"));
                SystemUser.setPassword(rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}

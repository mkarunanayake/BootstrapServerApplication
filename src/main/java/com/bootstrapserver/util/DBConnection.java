/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bootstrapserver.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Mevan
 */
public class DBConnection {

    private static DBConnection dbConnection;

    private Properties prop = new Properties();

    private DBConnection() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream("properties/db.properties");
        try {
            prop.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DBConnection getDbConnection() {
        if (dbConnection == null) {
            synchronized (DBConnection.class) {
                dbConnection = new DBConnection();
            }
        }

        return dbConnection;
    }

    public Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName(prop.getProperty("db.driver"));
            conn = DriverManager.getConnection(prop.getProperty("db.url"),
                    prop.getProperty("db.username"), prop.getProperty("db.password"));
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return conn;
    }

}

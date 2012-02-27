package me.SamV522.dBUB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by IntelliJ IDEA.
 * Author: SamV522
 * Date: 9/02/12
 * Time: 12:26 AM
 */
public class Database {
    private static Connection dbCon;
    private static boolean dbConnected;
    dBUBLogger pluginLogger = Main.pluginLogger;

    public Boolean connect(String dbHost, String databaseName, String username, String password, String port){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            if(dbConnected){
                if(closeConnection()){
                    connect(dbHost, databaseName, username, password, port);   
                }
            }else{
                    String url = "jdbc:mysql://%s:%s/%s?autoReconnect=true";
                    url = String.format(url, dbHost, port, databaseName);
                    pluginLogger.info("Connecting to database...");
                    pluginLogger.info(url);
                    Database.dbCon = DriverManager.getConnection(url, username, password);
                    dbConnected = true;
            }
        }catch(SQLException e){
            pluginLogger.warning("Database Error: " + e.getMessage());
        }catch(ClassNotFoundException e){
            pluginLogger.warning("Driver Error: "+ e.getMessage());
        }finally{
            if(dbConnected){
                pluginLogger.info("Connected to database successfully!");    
            }else{
                pluginLogger.info("Unable to connect to database :(");
            }
        }

        return dbConnected;
    }

    public Connection getConnection()
    {
        return dbCon;
    }

    public Boolean isConnected()
    {
        return dbConnected;
    }

    public Object sendQuery(String dbQuery)
    {
        return sendQuery(dbQuery, false);
    }

    public Object sendQuery(String dbQuery, Boolean update)
    {
        Object retVal = null;
        if(update==null)
        {
            return sendQuery(dbQuery, false);
        }else{
            try{
                Statement stmt = dbCon.createStatement();
                if(update)
                {
                    retVal = stmt.executeUpdate(dbQuery);
                }else{
                    retVal = stmt.executeQuery(dbQuery);
                }
            }catch(SQLException e){
                pluginLogger.warning("Database Error: ");
                pluginLogger.warning(e.getMessage());
            }
        }
        return retVal;
    }

    public Boolean closeConnection()
    {
        Boolean retBool = false;
        if(dbConnected){
            try{
                if(dbCon != null)
                {
                    dbCon.close();
                    if(dbCon.isClosed()){
                        dbConnected = false;
                        retBool = true;
                    }
                }else{
                    pluginLogger.warning("Could not close active Database connection [null]");
                }
            }catch(SQLException e){
                pluginLogger.warning("Database error:");
                pluginLogger.warning(e.getMessage());
            }
        }else{
            pluginLogger.warning("Could not close active Database connection!  Is it already closed?");
        }
        return retBool;
    }

}
package me.SamV522.dBUB;

import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 * Author: SamV522
 * Date: 9/02/12
 * To change this template use File | Settings | File Templates.
 */
public class GroupMapping {

    public static String getGroupFromDb(Player base)
    {
        String retString = null;
        String dbGroupID = null;
        Database db = Main.db;
        dBUBLogger pluginLogger = Main.pluginLogger;

        
        if(db.isConnected()){
            String queryString = "";
            if(Main.Config.getBoolean("database.users.usernames.sametable")){
                queryString = "SELECT "+Main.Config.getString("database.users.group-column")+
                        " FROM "+ Main.Config.getString("database.users.table")+" WHERE "+
                        Main.Config.getString("database.users.usernames.column")+" = "+
                        base.getDisplayName().replace('"', '\"');
            }else if(!Main.Config.getBoolean(("database.usernames.sametable"))){
                queryString = "SELECT * "
            }
            ResultSet rs = db.sendQuery(queryString);
            try{
                while(rs.next())
                {
                    dbGroupID = rs.getString(Main.Config.getString("database.users.group-column"));
                }
            }catch(SQLException e){
                pluginLogger.log(Level.WARNING, "Database error:");
                pluginLogger.log(Level.WARNING, e.getMessage());
            }
            retString = Main.Config.getString("group-mapping."+ dbGroupID);
        }else{
            pluginLogger.log(Level.WARNING, "User \""+base.getDisplayName()+
                    "\" connected while Database is not connected!");
        }
        return retString;
    }
    
    public static void setdBGroup(Player base, String dbGroupID, String newGroupID)
    {
        String queryString="UPDATE "+ Main.Config.getString("database.users.table")+
        "SET "+ Main.Config.getString("database.users.group-column")+"="+newGroupID+
        "WHERE "+ Main.Config.getString("database.users.usernames.column")+"="+base.getDisplayName();
    }
}

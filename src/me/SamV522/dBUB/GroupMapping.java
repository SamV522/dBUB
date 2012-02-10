package me.SamV522.dBUB;

import org.bukkit.entity.Player;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 * Author: SamV522
 * Date: 9/02/12
 * To change this template use File | Settings | File Templates.
 */
public class GroupMapping {
    static Database db = Main.db;
    static dBUBLogger pluginLogger = Main.pluginLogger;
    
    private static ResultSet getDbMinecraftUser(Player base, Boolean sameTable)
    {
        ResultSet rs = null;
        String queryString = "";
        if(db.isConnected())
        {
            //  Which table do the usernames come from?
            String userTable = Main.Config.getString("database.users.table");
            //  Which column the username comes from
            String userColumn = Main.Config.getString("database.users.column");
            //  Which column the group id comes from
            String groupColumn =Main.Config.getString("database.users.group-column");
            String mcIgUser = base.getDisplayName().replace('"', '\"');
            if(sameTable){
                queryString = "SELECT "+groupColumn+" FROM "+userTable+" WHERE "+userColumn+" = "+ mcIgUser;
                rs = db.sendQuery(queryString);
                return rs;
            }else if(!sameTable){
                //  This value needs to be pulled from the user table in order to match up tables later
                String matchValue = "";

                //  Which column do we want to match ID's up with from the config 
                String columnToMatch = Main.Config.getString("database.alt.match");

                //  Select the usernames, and the match column from the alternate table, where columnToMatch = matchValue
                String altTable = Main.Config.getString("database.alt.table");

                //  Which column does the database use to attach the value to the user?
                String altUserID = Main.Config.getString("databause.alt.id-column");

                //  Which column in the main user table contains the user id?
                String userID = Main.Config.getString("database.users.id-column");

                //  Match the usernames in the alt table!
                try
                {
                    queryString = "SELECT "+altUserID+","+columnToMatch+
                            " FROM "+altTable+" WHERE "+columnToMatch+"="+mcIgUser;
                    rs = db.sendQuery(queryString);

                    // Recycle the queryString now to get the users group ID!
                    queryString = "SELECT "+groupColumn+" FROM "+userTable+" WHERE "+userID+"= "+rs.getString("altUserID");
                    rs = db.sendQuery(queryString);
                }catch(SQLException e){
                    pluginLogger.warning("Database error:");
                    pluginLogger.warning(e.getMessage());
                }
            }else{
                getDbMinecraftUser(base, true);
            }
        }
        
        return rs;
    }

    public static String getGroupFromDb(Player base)
    {
        String retString = null;
        String dbGroupID = null;
        dBUBLogger pluginLogger = Main.pluginLogger;

        
        if(db.isConnected()){
            String queryString = "";
            ResultSet rs = getDbMinecraftUser(base, Main.Config.getBoolean("database.users.sametable"));
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

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
    static Database db = Main.db;
    static dBUBLogger pluginLogger = Main.pluginLogger;
    
    public static void updateMinecraftUser(Player base)
    {
        String preGroup = GMHook.getGroup(base);
        String newGroup = GroupMapping.getGroupFromDb(base);
        if(newGroup!=null&&preGroup != newGroup){
            if(newGroup != null)
            {
                GMHook.setGroup(base, newGroup);
                Main.pluginLogger.log(Level.INFO, "Successfully set user \""+ base.getDisplayName()+
                        "\" to group \""+newGroup+"\"");
            }else{
                GMHook.setGroup(base,  Main.Config.getString("group-mapping.default"));
                Main.pluginLogger.log(Level.INFO, "User \"" + base.getDisplayName() + "\" was not found in the database. " +
                        " setting them to the \"" + Main.Config.getString("group-mapping.default") + "\" group");
            }
        }
    }
    
    private static ResultSet getDbMinecraftUser(Player base, Boolean sameTable)
    {
        ResultSet rs = null;
        String queryString = "";
        if(db.isConnected())
        {
            //  Which table do the usernames come from?
            String userTable = Main.Config.getString("database.users.table");

            //  Which column the username comes from
            String userColumn = Main.Config.getString("database.users.name-column");

            //  Which column the group id comes from
            String groupColumn =Main.Config.getString("database.users.group-column");

            //  The players name as seen in-game.
            String mcIgUser = base.getDisplayName().replace('"', '\"');

            if(sameTable){
                queryString = "SELECT "+groupColumn+" FROM "+userTable+" WHERE "+userColumn+" = \""+mcIgUser+"\"";
                rs = db.sendQuery(queryString);
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
                            " FROM "+altTable+" WHERE "+columnToMatch+"=\""+mcIgUser+"\"";
                    rs = db.sendQuery(queryString);

                    //  Get the ALT user id from the query we just ran, if it exists

                    if(rs.next())
                    {
                        // Recycle the queryString now to get the users group ID!
                        queryString = "SELECT "+groupColumn+" FROM "+userTable+" WHERE "+userID+"= "+rs.getString("altUserID");
                        rs = db.sendQuery(queryString);
                    }else{
                        //  Either the player doesn't exist in the database, or
                    }
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
                if(rs.next())
                {
                    //  The user exists!  Get their group! :D
                    dbGroupID = rs.getString(Main.Config.getString("database.users.group-column"));
                    retString = Main.Config.getString("group-mapping."+ dbGroupID);
                }else{
                    //  Either the user doesn't exist in the database, you're doing something wrong, retrn a null string.
                    retString = null;
                }
            }catch(SQLException e){
                pluginLogger.log(Level.WARNING, "Database error:");
                pluginLogger.log(Level.WARNING, e.getMessage());
            }
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

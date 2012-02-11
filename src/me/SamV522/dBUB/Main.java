package me.SamV522.dBUB; /**
 * Created by IntelliJ IDEA.
 * Author: SamV522
 * Date: 8/02/12
 * Purpose: Bridge user permissions between minecraft servers and an online database for use with forums or other
 *		    systems that utilize user databases
 * To change this template use File | Settings | File Templates.
 */

import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class Main extends JavaPlugin {
    public static dBUBLogger pluginLogger = new dBUBLogger();
    File cfgFile;
    public static FileConfiguration Config = null;
    public static Database db;
    public static boolean reload = false;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
        boolean retBool = false;
        if(cmd.getName().equalsIgnoreCase("dbub"))
        {
            if(args.length>0)
            {
                if(args[0].equalsIgnoreCase("reload")){
                    if(sender.hasPermission("dbub.reload"))
                        reload = true;
                    this.onDisable();
                    this.onEnable();
                    if(!reload){
                        pluginLogger.info(" Successfully reloaded!");
                        sender.sendMessage("dBUB v"+this.getDescription().getVersion()+" reloaded!");
                    }
                }else if(args[0].equalsIgnoreCase("version")){
                    if(sender.hasPermission("dbub.version"))
                    {
                        sender.sendMessage("This server is running "+this.getDescription().getName()+" v"+this.getDescription().getVersion());
                    }
                }else if(args[0].equalsIgnoreCase("help")||args[0].equalsIgnoreCase("?")){
                    if(sender.hasPermission("dbub.help"))
                    {
                        sender.sendMessage("Commands for dBUB v"+ this.getDescription().getVersion());
                        sender.sendMessage("/dbub reload - Reloads dBUB");
                        sender.sendMessage("/dbub version - Shows the version of dBUB currently running");
                        sender.sendMessage("/dbub help - Shows this menu");
                        sender.sendMessage("/dbub sync - Manually synchronize groups from the database");
                    }
                }else if(args[0].equalsIgnoreCase("sync"))
                {
                    if(sender.hasPermission("dbub.sync"))
                    {
                        Player[] onlinePlayers = getServer().getOnlinePlayers();
                        for(int i=0;i!=onlinePlayers.length;i++)
                        {
                            GroupMapping.updateMinecraftUser(onlinePlayers[i]);
                        }
                    }
                }else{
                    sender.sendMessage("You did not issue a valid command!");
                }
            }else{
                sender.sendMessage("Please enter one of the commands!");
            }

        retBool = true;
        }
        return retBool;
    }

    public void onEnable() {
        db = new Database();
        cfgFile = new File(getDataFolder(), "config.yml");
        Config = YamlConfiguration.loadConfiguration(cfgFile);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(), this);
        Main.db.connect(Config.getString("database.host"), Config.getString("database.database-name"),
                Config.getString("database.username"),Config.getString("database.password"),
                Config.getString("database.port"));
        pluginLogger.info("has been enabled successfully!");
        Plugin GMplugin = pm.getPlugin("GroupManager");

        if (GMplugin != null && GMplugin.isEnabled())
        {
            GMHook.groupManager = (GroupManager)GMplugin;
            Main.pluginLogger.log(Level.INFO, "hooked into Group Manager successfully!");
        }
        reload = false;
    }

    public void onDisable() {
        pluginLogger.info("has been disabled successfully.");
        Main.db.closeConnection();
    }

}
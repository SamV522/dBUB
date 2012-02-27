package me.SamV522.dBUB; /**
 * Created by IntelliJ IDEA.
 * Author: SamV522
 * Date: 8/02/12
 * Purpose: Bridge user permissions between minecraft servers and an online database for use with forums or other
 *		    systems that utilize user databases
 */



import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {
    public static dBUBLogger pluginLogger = new dBUBLogger();
    File cfgFile;
    public static FileConfiguration Config = null;
    public static PluginManager pm = null;
    public static Database db;
    public static Permission perms = null;
    public static boolean reload = false;

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

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
                        sender.sendMessage(ChatColor.GOLD+"[dBUB]"+ChatColor.WHITE+" v"+this.getDescription().getVersion()+" reloaded!");
                    }
                }else if(args[0].equalsIgnoreCase("version")){
                    if(sender.hasPermission("dbub.version"))
                    {
                        sender.sendMessage(ChatColor.GOLD+"[dBUB]"+ChatColor.WHITE+" This server is running version "+this.getDescription().getVersion());
                    }
                }else if(args[0].equalsIgnoreCase("help")||args[0].equalsIgnoreCase("?")){
                    if(sender.hasPermission("dbub.help"))
                    {
                        sender.sendMessage(ChatColor.GOLD+"[dBUB]"+ChatColor.WHITE+" Commands for version "+ this.getDescription().getVersion());
                        sender.sendMessage("reload - Reloads dBUB");
                        sender.sendMessage("version - Shows the version of dBUB currently running");
                        sender.sendMessage("help/? - Shows this menu");
                        sender.sendMessage("sync - Manually synchronize groups from the database");
                        sender.sendMessage("syncdb - Manually synchronize groups to the database");
                    }
                }else if(args[0].equalsIgnoreCase("sync"))
                {
                    if(sender.hasPermission("dbub.sync"))
                    {
                        pluginLogger.info("Synchronizing...");
                        sender.sendMessage(ChatColor.GOLD+"[dBUB]"+ChatColor.WHITE+" Synchronizing from Database...");
                        Player[] onlinePlayers = getServer().getOnlinePlayers();
                        for(int i=0;i!=onlinePlayers.length;i++)
                        {
                            GroupMapping.updateMinecraftUser(onlinePlayers[i]);
                        }
                        sender.sendMessage(ChatColor.GOLD+"[dBUB]"+ChatColor.WHITE+" Finished synchronizing!");
                        pluginLogger.info("Finished synchronizing users");
                    }
                }else if(args[0].equalsIgnoreCase("syncdb"))
                {
                    if(sender.hasPermission("dbub.syncdb"))
                    {
                        pluginLogger.info("Synchronizing to Database...");
                        sender.sendMessage(ChatColor.GOLD+"[dBUB]"+ChatColor.WHITE+" Synchronizing to Database...");
                        Player[] onlinePlayers = getServer().getOnlinePlayers();
                        for(int i=0;i!=onlinePlayers.length;i++)
                        {
                            GroupMapping.setdBGroup(onlinePlayers[i], GroupMapping.getdbGroupID(Main.perms.getPrimaryGroup(onlinePlayers[i])));
                        }
                        sender.sendMessage(ChatColor.GOLD+"[dBUB]"+ChatColor.WHITE+" Finished synchronizing!");
                    }
                }else{
                    sender.sendMessage("You did not issue a valid command!");
                }
            }else{
                String[] newArgs = new String[1];
                newArgs[0] = "?";
                onCommand(sender, cmd, commandLabel,newArgs);
            }
        retBool = true;
        }
        return retBool;
    }

    public void onEnable() {
        if (!setupPermissions())
        {
            pluginLogger.info("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }else{
            pluginLogger.info("Successfully hooked into Vault!");
        }
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        db = new Database();
        cfgFile = new File(getDataFolder(), "config.yml");
        Config = YamlConfiguration.loadConfiguration(cfgFile);
        pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(), this);
        Main.db.connect(Config.getString("database.host"), Config.getString("database.database-name"),
                Config.getString("database.username"),Config.getString("database.password"),
                Config.getString("database.port"));
        pluginLogger.info("Successfully enabled!");
        reload = false;
    }

    public void onDisable() {
        if(db.isConnected())
        {
            Main.db.closeConnection();
        }
        pluginLogger.info("Successfully disabled.");
    }

}
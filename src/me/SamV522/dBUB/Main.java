package me.SamV522.dBUB; /**
 * Created by IntelliJ IDEA.
 * Author: SamV522
 * Date: 8/02/12
 * Purpose: Bridge user permissions between minecraft servers and an online database for use with forums or other
 *		    systems that utilize user databases
 * To change this template use File | Settings | File Templates.
 */

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {
    public static dBUBLogger pluginLogger = new dBUBLogger();
    File cfgFile;
    public static FileConfiguration Config = null;
    public static Database db = new Database();
    private final PlayerListener listener = null;

    public void onEnable() {
        cfgFile = new File(getDataFolder(), "config.yml");
        Config = YamlConfiguration.loadConfiguration(cfgFile);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(), this);
        db.connect(Config.getString("database.host"), Config.getString("database.database-name"),
                Config.getString("database.username"),Config.getString("database.password"),
                Config.getString("database.port"));
        pluginLogger.info("has been enabled successfully!");
    }

    public void onDisable() {
        pluginLogger.info("has been disabled successfully.");
        db.closeConnection();
        db = null;
        pluginLogger = null;
    }

}

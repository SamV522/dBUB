package me.SamV522.dBUB;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Author: SamV522
 * Date: 8/02/12
 * Time: 11:36 PM
 */
public class PlayerListener implements Listener{
    private dBUBLogger pluginLogger = Main.pluginLogger;

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        boolean syncFrom = Main.Config.getBoolean("database.syncFrom");
        Player base = event.getPlayer();
        if(syncFrom)
        {
            Map<String, Object> ignorePlayers = Main.Config.getConfigurationSection("ignored-users").getValues(true);
            for(Map.Entry<String, Object> entry : ignorePlayers.entrySet())
            {
                if(!base.getName().equalsIgnoreCase((String) entry.getValue()))
                {
                    GroupMapping.updateMinecraftUser(event.getPlayer());
                }
            }
        }
    }
}
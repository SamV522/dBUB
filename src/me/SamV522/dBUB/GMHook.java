package  me.SamV522.dBUB;
/**
 * Credits to KHobbits for this!
 * Date: 9/02/12
 */

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;


public class GMHook implements Listener {
    private static GroupManager groupManager;
    private Main plugin;

    public GMHook(final Main instance)
    {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(final PluginEnableEvent event)
    {
        final PluginManager pluginManager = plugin.getServer().getPluginManager();
        if(event.getPlugin() == pluginManager.getPlugin("GroupManager"))
        {
            final Plugin GMplugin = pluginManager.getPlugin("GroupManager");

            if (GMplugin != null && GMplugin.isEnabled())
            {
                groupManager = (GroupManager)GMplugin;
                Main.pluginLogger.log(Level.INFO, "Successfully hooked into Group Manager!");
            }
        }
    }

    public static String getGroup(final Player base)
    {
        final AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(base);
        if (handler == null)
        {
            return null;
        }
        return handler.getGroup(base.getName());
    }

    public static boolean setGroup(final Player base, final String group)
    {
        final OverloadedWorldHolder handler = groupManager.getWorldsHolder().getWorldData(base);
        if (handler == null)
        {
            return false;
        }
        handler.getUser(base.getName()).setGroup(handler.getGroup(group));
        return true;
    }

    public static List<String> getGroups(final Player base)
    {
        final AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(base);
        if (handler == null)
        {
            return null;
        }
        return Arrays.asList(handler.getGroups(base.getName()));
    }

    public static String getPrefix(final Player base)
    {
        final AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(base);
        if (handler == null)
        {
            return null;
        }
        return handler.getUserPrefix(base.getName());
    }

    public static String getSuffix(final Player base)
    {
        final AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(base);
        if (handler == null)
        {
            return null;
        }
        return handler.getUserSuffix(base.getName());
    }

    public static boolean hasPermission(final Player base, final String node)
    {
        final AnjoPermissionsHandler handler = groupManager.getWorldsHolder().getWorldPermissions(base);
        if (handler == null)
        {
            return false;
        }
        return handler.has(base, node);
    }
}
package me.SamV522.dBUB;

/**
 * Created by IntelliJ IDEA.
 * Author: Sam
 * Date: 9/02/12
 * To change this template use File | Settings | File Templates.
 */
import java.util.logging.Level;
import java.util.logging.Logger;

public class dBUBLogger {
    private static Logger log;
    private static String prefix;

    public void initialize() {
        dBUBLogger.log = Logger.getLogger("Minecraft");
        prefix = "[dBUB] ";
    }

    public Logger getLog() {
        return log;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        dBUBLogger.prefix = prefix;
    }

    public void info(String message) {
        log.info(prefix + message);
    }

    public void dbinfo(String message) {
        log.info(prefix + "[DB] " + message);
    }

    public void error(String message) {
        log.severe(prefix + message);
    }

    public void warning(String message) {
        log.warning(prefix + message);
    }

    public void config(String message) {
        log.config(prefix + message);
    }

    public void log(Level level, String message) {
        log.log(level, prefix + message);
    }
}

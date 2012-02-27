package me.SamV522.dBUB;

/**
 * Created by IntelliJ IDEA.
 * Author: SamV522
 * Date: 9/02/12
 */

import java.util.logging.Level;
import java.util.logging.Logger;

public class dBUBLogger {
    private Logger log;
    private String prefix;

    public dBUBLogger() {
        log = Logger.getLogger("Minecraft");
        prefix = "[dBUB] ";
    }

    public Logger getLog() {
        return log;
    }
    public void setPrefix(String prefix) {
        prefix = prefix;
    }

    public void info(String message) {
        log.info(prefix + message);
    }

    public void error(String message) {
        log.severe(prefix + message);
    }

    public void warning(String message) {
        log.warning(prefix + message);
    }

    public void log(Level level, String message) {
        log.log(level, prefix + message);
    }
}

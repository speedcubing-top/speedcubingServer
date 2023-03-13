package top.speedcubing.server.database;

import top.speedcubing.lib.utils.SQL.SQLConnection;
import top.speedcubing.server.config;

public class Database {
    public static SQLConnection connection;
    public static SQLConnection systemConnection;
    public static void init(){
        connection = new SQLConnection(config.DatabaseURL.replace("%db%",  "speedcubing"), config.DatabaseUser, config.DatabasePassword);
        systemConnection = new SQLConnection(config.DatabaseURL.replace("%db%", "speedcubingsystem"), config.DatabaseUser, config.DatabasePassword);
    }
}

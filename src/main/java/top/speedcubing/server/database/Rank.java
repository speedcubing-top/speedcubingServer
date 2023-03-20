package top.speedcubing.server.database;

import top.speedcubing.server.config;

public class Rank {

    public static String getRank(String rank, int id) {
        return rank.equals("default") && DataCenter.champs.contains(id) ? "champ" : rank;
    }

    public static String[] getFormat(String rank) {
        return config.colors.get(rank);
    }

    public static String[] getFormat(String rank, int id) {
        return getFormat(getRank(rank, id));
    }
}

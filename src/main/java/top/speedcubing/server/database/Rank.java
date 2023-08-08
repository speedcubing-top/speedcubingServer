package top.speedcubing.server.database;

import top.speedcubing.server.utils.config;

public class Rank {

    public static String getRank(String rank, int id) {
        return rank.equals("default") && DataCenter.champs.contains(id) ? "champ" : rank;
    }

    public static String[] getFormat(String rank, int id) {
        return config.colors.get(getRank(rank, id));
    }

    public static boolean isStaff(String realRank){
        return realRank.equals("builder") || realRank.equals("helper") || realRank.equals("admin") || realRank.equals("owner") || realRank.equals("mod");
    }
}

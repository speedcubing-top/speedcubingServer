package top.speedcubing.common.rank;

import com.google.common.collect.Sets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import top.speedcubing.common.database.DatabaseData;
import top.speedcubing.common.database.Database;

public class Rank {
    public static Map<String, Set<String>> grouppermissions = new HashMap<>();

    public static Map<String, Rank> rankByName = new HashMap<>();

    private final String rank;
    private final int weight;
    private final RankFormat format;
    private final long discord;
    private final Set<String> perms;
    private int orderCode;

    public Rank(String rank, int weight, String prefix, String chatColor, long discord, Set<String> perms) {
        this.rank = rank;
        this.weight = weight;
        this.format = new RankFormat(prefix, chatColor);
        this.discord = discord;
        this.perms = perms;
    }

    public static String getRank(String rank, int id) {
        return rank.equals("default") && DatabaseData.champs.contains(id) ? "champ" : rank;
    }

    public static int getCode(String rank) {
        return 10 + Rank.rankByName.get(rank).orderCode;
    }

    public static RankFormat getFormat(String rank, int id) {
        return rankByName.get(getRank(rank, id)).getFormat();
    }

    public static boolean isStaff(String realRank) {
        return realRank.equals("builder") || realRank.equals("helper") || realRank.equals("admin") || realRank.equals("owner") || realRank.equals("mod") || realRank.equals("developer");
    }

    public static void reloadRanks() {
        rankByName.clear();

        List<Rank> rankByOrder = new ArrayList<>();

        try {
            ResultSet r = Database.configConnection.select("*").from("mc_ranks").executeQuery();

            while (r.next()) {
                String name = r.getString("name");
                int weight = r.getInt("weight");
                String prefix = r.getString("prefix");
                String chatColor = r.getString("chatcolor");
                long discord = r.getLong("discord");
                Set<String> perms = Sets.newHashSet(r.getString("perms").split("\\|"));

                Rank rank = new Rank(name, weight, prefix, chatColor, discord, perms);
                rankByName.put(name, rank);
                rankByOrder.add(rank);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        rankByOrder.sort((o1, o2) -> Integer.compare(o2.getWeight(), o1.getWeight()));

        for (int i = 0; i < rankByOrder.size(); i++) {
            rankByOrder.get(i).orderCode = i;
        }

        grouppermissions.clear();
        for (String s : Database.systemConnection.select("name").from("groups").getStringArray())
            grouppermissions.put(s, Sets.newHashSet(Database.systemConnection.select("perms").from("groups").where("name='" + s + "'").getString().split("\\|")));
    }

    public String getRank() {
        return rank;
    }


    public int getWeight() {
        return weight;
    }

    public RankFormat getFormat() {
        return format;
    }

    public long getDiscord() {
        return discord;
    }

    public Set<String> getPerms() {
        return perms;
    }
}

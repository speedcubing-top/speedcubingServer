package speedcubing.server.libs;

import speedcubing.server.speedcubingServer;

import java.util.*;

public class User {


    public static List<String> ranks = new ArrayList<>();
    public static Map<String, String[]> colors = new HashMap<>();
    public static Map<String, Set<String>> permissions = new HashMap<>();

    public static Map<UUID, Integer> LangCache = new HashMap<>();
    public static Map<UUID, String> RankCache = new HashMap<>();


    public static Set<String> getPerms(String rank) {
        return permissions.get(rank);
    }

    public static String[] getFormat(String rank) {
        return colors.get(rank);
    }

    public static int getCode(String rank) {
        return 10 + ranks.indexOf(rank);
    }

    public static String getRank(UUID uuid) {
        return RankCache.get(uuid);
    }

    public static int getLang(UUID uuid) {
        if (LangCache.containsKey(uuid))
            return LangCache.get(uuid);
        else {
            int x = speedcubingServer.connection.selectInt("playersdata", "lang", "uuid='" + uuid + "'");
            LangCache.put(uuid, x);
            return x;
        }
    }

    public static int getVersion(UUID uuid) {
        return speedcubingServer.connection.selectInt("playersdata", "ver", "uuid='" + uuid + "'");
    }

    public static int getVersion(String uuid) {
        return speedcubingServer.connection.selectInt("playersdata", "ver", "uuid='" + uuid + "'");
    }

    public static String playerNameExtract(String name) {
        StringBuilder str = new StringBuilder();
        StringBuilder nameBuilder = new StringBuilder(name);
        while (nameBuilder.length() < 16) {
            nameBuilder.append(" ");
        }
        name = nameBuilder.toString();
        for (int i = 0; i < 16; i++) {
            int c = name.charAt(i);
            c = (c == 32 ? 0 : (c <= 57 ? c - 47 : (c <= 90 ? c - 54 : (c == 95 ? 37 : c - 59))));
            StringBuilder bin = new StringBuilder(Integer.toBinaryString(c));
            while (bin.length() < 6) {
                bin.insert(0, "0");
            }
            str.append(bin);
        }
        str.append("00");
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < 14; i++) {
            string.append((char) (Integer.parseInt(str.substring(i * 7, i * 7 + 6), 2) + 32));
        }
        return string.toString();
    }
}

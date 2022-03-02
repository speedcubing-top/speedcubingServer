package cubingserver.libs;


import java.util.UUID;

public enum Rank {
    OWNER(10, new String[]{"§4", "[Owner] ", "§c"}),
    ADMIN(20, new String[]{"§4", "[Admin] ", "§c"}),
    MOD(30, new String[]{"§2", "[Mod] ", "§a"}),
    HELPER(40, new String[]{"§9", "[Helper] ", "§9"}),
    HEADBUILDER(50, new String[]{"§b", "[HeadBuilder] ", "§3"}),
    BUILDER(60, new String[]{"§3", "[Builder] ", "§b"}),
    YTPLUS(70, new String[]{"§4", "[YT+] ", "§c"}),
    YT(80, new String[]{"§5", "[YT] ", "§d"}),
    VIPPLUS(85, new String[]{"§d", "[VIP] ", "§5"}),
    PRIME(90, new String[]{"§6", "[Prime] ", "§6"}),
    DEFAULT(95, new String[]{"§7", "", "§f"});

    private final int id;
    private final String[] args;

    Rank(int id, String[] args) {
        this.id = id;
        this.args = args;
    }

    public int getId() {
        return id;
    }

    public String[] getFormat() {
        return args;
    }

    public static String[] format(UUID uuid) {
        return Rank.values()[rankToIndex(PlayerData.getRank(uuid))].getFormat();
    }

    public static String[] format(String uuid) {
        return Rank.values()[rankToIndex(PlayerData.getRank(uuid))].getFormat();
    }

    public static int rankToIndex(int i) {
        int index = 0;
        for (Rank rank : Rank.values()) {
            if (i == rank.getId())
                return index;
            index++;
        }
        return -1;
    }

    public static String playerNameExtract(String name) {
        String str = "";
        while (name.length() < 16) {
            name = name + " ";
        }
        for (int i = 0; i < 16; i++) {
            int c = name.charAt(i);
            c = (c == 32 ? 0 : (c <= 57 ? c - 47 : (c <= 90 ? c - 54 : (c == 95 ? 37 : c - 59))));
            String bin = Integer.toBinaryString(c);
            while (bin.length() < 6) {
                bin = "0" + bin;
            }
            str += bin;
        }
        str += "00";
        String string = "";
        for (int i = 0; i < 14; i++) {
            string += (char) (Integer.parseInt(str.substring(i * 7, i * 7 + 6), 2) + 32);
        }
        return string;
    }
}

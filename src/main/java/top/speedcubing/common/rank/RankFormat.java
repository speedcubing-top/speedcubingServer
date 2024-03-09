package top.speedcubing.common.rank;

public class RankFormat {
    private final String prefix;
    private final String nameColor;
    private final String chatColor;

    public RankFormat(String prefix, String chatColor) {
        this.prefix = prefix;
        this.nameColor = prefix.lastIndexOf('§') == -1 ? "" : ("§" + prefix.charAt(prefix.lastIndexOf('§') + 1));
        this.chatColor = chatColor;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getNameColor() {
        return nameColor;
    }

    public String getChatColor() {
        return chatColor;
    }
}

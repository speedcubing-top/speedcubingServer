package top.speedcubing.server.login;

import top.speedcubing.lib.utils.SQL.SQLRow;

public class LoginContext {
    private final SQLRow row;
    private final String realRank;
    private final BungeePacket bungePacket;

    public LoginContext(SQLRow row, String realRank, BungeePacket bungePacket) {
        this.row = row;
        this.realRank = realRank;
        this.bungePacket = bungePacket;
    }

    public SQLRow getRow() {
        return row;
    }

    public String getRealRank() {
        return realRank;
    }

    public BungeePacket getBungePacket() {
        return bungePacket;
    }
}

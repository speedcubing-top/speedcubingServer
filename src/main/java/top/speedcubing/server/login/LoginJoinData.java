package top.speedcubing.server.login;

import top.speedcubing.server.login.PreLoginData;

public class LoginJoinData {
    private final String realRank;
    private final String[] datas;
    private final PreLoginData bungeeData;

    public LoginJoinData(String realRank, String[] datas, PreLoginData bungeeData) {
        this.realRank = realRank;
        this.datas = datas;
        this.bungeeData = bungeeData;
    }

    public String getRealRank() {
        return realRank;
    }

    public String[] getDatas() {
        return datas;
    }

    public PreLoginData getBungeeData() {
        return bungeeData;
    }
}
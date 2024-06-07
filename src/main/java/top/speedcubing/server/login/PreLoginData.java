package top.speedcubing.server.login;

import top.speedcubing.lib.utils.internet.HostAndPort;

public class PreLoginData {
    public final HostAndPort proxy;
    public final String hor;
    public final String ver;
    public boolean cps;
    public final boolean vanished;

    public PreLoginData(String proxyHost, int proxyport, String hor, String ver, boolean vanished) {
        this.proxy = new HostAndPort(proxyHost, proxyport);
        this.hor = hor;
        this.ver = ver;
        this.vanished = vanished;
    }
}

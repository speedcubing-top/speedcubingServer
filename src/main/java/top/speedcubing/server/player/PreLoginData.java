package top.speedcubing.server.player;

public class PreLoginData {
    public final int port;
    public final String hor;
    public final String ver;
    public boolean cps;
    public final boolean vanished;

    public PreLoginData(int port, String hor, String ver,boolean vanished) {
        this.port = port;
        this.hor = hor;
        this.ver = ver;
        this.vanished = vanished;
    }
}

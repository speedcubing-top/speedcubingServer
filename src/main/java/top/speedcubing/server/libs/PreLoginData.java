package top.speedcubing.server.libs;

public class PreLoginData {
    public final int port;
    public final String hor;
    public final String ver;

    public PreLoginData(int port, String hor, String ver) {
        this.port = port;
        this.hor = hor;
        this.ver = ver;
    }
}

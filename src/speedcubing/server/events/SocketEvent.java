package speedcubing.server.events;

import java.net.Socket;

public class SocketEvent {
    public String[] rs;
    public int localPort;

    public SocketEvent(String[] rs, int localPort) {
        this.rs = rs;
        this.localPort = localPort;
    }
}

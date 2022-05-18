package speedcubing.server.events;

import java.net.Socket;

public class SocketEvent {
    public String[] rs;
    public Socket socket;

    public SocketEvent(String[] rs, Socket socket) {
        this.rs = rs;
        this.socket = socket;
    }
}

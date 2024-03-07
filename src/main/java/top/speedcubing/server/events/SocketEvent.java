package top.speedcubing.server.events;

import java.io.DataInputStream;
import top.speedcubing.lib.eventbus.CubingEvent;

public class SocketEvent extends CubingEvent {
    public final DataInputStream receive;
    public final String header;

    public SocketEvent(DataInputStream receive,String header) {
        this.receive = receive;
        this.header = header;
    }
}

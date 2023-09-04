package top.speedcubing.server.events;

import top.speedcubing.lib.eventbus.CubingEvent;

import java.io.DataInputStream;

public class SocketEvent extends CubingEvent {
    public final DataInputStream receive;
    public final String header;

    public SocketEvent(DataInputStream receive,String header) {
        this.receive = receive;
        this.header = header;
    }
}

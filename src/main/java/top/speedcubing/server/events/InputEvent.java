package top.speedcubing.server.events;

import top.speedcubing.lib.eventbus.LibEventManager;
import top.speedcubing.lib.utils.ByteArrayDataBuilder;

import java.io.DataInputStream;

public class InputEvent extends LibEventManager {
    public final DataInputStream receive;
    public final String header;
    public final ByteArrayDataBuilder respond = new ByteArrayDataBuilder();

    public InputEvent(DataInputStream receive, String packetID, String header) {
        this.receive = receive;
        this.header = header;
        respond.writeUTF("out").writeUTF(packetID);
    }
}

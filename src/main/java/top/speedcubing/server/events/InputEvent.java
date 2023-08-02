package top.speedcubing.server.events;

import top.speedcubing.lib.eventbus.LibEventManager;
import top.speedcubing.lib.utils.ByteArrayDataBuilder;

import java.io.DataInputStream;

public class InputEvent extends LibEventManager {
    public final DataInputStream receive;
    public final String subHeader;
    public final ByteArrayDataBuilder respond = new ByteArrayDataBuilder();

    public InputEvent(DataInputStream receive, String subHeader) {
        this.receive = receive;
        this.subHeader = subHeader;
        respond.writeUTF("out");
    }
}

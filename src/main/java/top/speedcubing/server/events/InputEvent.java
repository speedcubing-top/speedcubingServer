package top.speedcubing.server.events;

import java.io.DataInputStream;
import top.speedcubing.lib.eventbus.CubingEvent;
import top.speedcubing.lib.utils.ByteArrayDataBuilder;

public class InputEvent extends CubingEvent {
    public final DataInputStream receive;
    public final String subHeader;
    public final ByteArrayDataBuilder respond = new ByteArrayDataBuilder();

    public InputEvent(DataInputStream receive, String subHeader) {
        this.receive = receive;
        this.subHeader = subHeader;
        respond.writeUTF("out");
    }
}

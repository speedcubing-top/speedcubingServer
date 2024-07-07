package top.speedcubing.server.cubinglistener;

import java.io.IOException;
import top.speedcubing.common.events.SocketInputEvent;
import top.speedcubing.lib.eventbus.CubingEventHandler;
import top.speedcubing.server.login.PreLoginData;
import top.speedcubing.server.speedcubingServer;

public class SocketInput {

    @CubingEventHandler
    public void socketInputEvent(SocketInputEvent e) {
        try {
            switch (e.subHeader) {
                case "bungee":
                    int i = e.getData().readInt();
                    speedcubingServer.preLoginStorage.put(i, new PreLoginData(e.getData().readUTF(), e.getData().readInt(), e.getData().readUTF(), e.getData().readUTF(), e.getData().readBoolean()));
                    break;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}

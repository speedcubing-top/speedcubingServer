package top.speedcubing.server.events;

import top.speedcubing.lib.eventbus.LibEventManager;

public class SocketEvent extends LibEventManager {
    public String receive;

    public SocketEvent(String receive) {
        this.receive = receive;
    }
}

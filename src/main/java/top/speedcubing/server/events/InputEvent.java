package top.speedcubing.server.events;

import top.speedcubing.lib.eventbus.LibEventManager;

public class InputEvent extends LibEventManager {
    public String receive;

    public InputEvent(String receive) {
        this.receive = receive;
    }
}

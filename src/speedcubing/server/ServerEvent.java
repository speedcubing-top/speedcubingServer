package speedcubing.server;

import speedcubing.lib.event.LibEventHandler;
import speedcubing.server.events.packet.PlayInTabCompleteEvent;

public class ServerEvent {
    @LibEventHandler
    public void PlayInTabCompleteEvent(PlayInTabCompleteEvent e) {
        if (speedcubingServer.blockedTab.contains(e.packet.a()))
            e.isCancelled = true;
    }
}
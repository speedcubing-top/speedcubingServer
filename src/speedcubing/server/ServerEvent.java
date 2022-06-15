package speedcubing.server;

import speedcubing.lib.bukkit.event.PlayInTabCompleteEvent;
import speedcubing.lib.eventbus.LibEventHandler;

public class ServerEvent {
    @LibEventHandler
    public void PlayInTabCompleteEvent(PlayInTabCompleteEvent e) {
        if (speedcubingServer.blockedTab.contains(e.packet.a()))
            e.isCancelled = true;
    }
}
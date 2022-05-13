package speedcubing.server;

import speedcubing.spigot.Event.ServerEventHandler;
import speedcubing.spigot.Event.events.TabCompleteCommandEvent;

public class ServerEvent {
    @ServerEventHandler
    public void TabCompleteCommandEvent(TabCompleteCommandEvent e) {
        if (speedcubingServer.blockedTab.contains(e.message))
            e.completions = null;
    }
}

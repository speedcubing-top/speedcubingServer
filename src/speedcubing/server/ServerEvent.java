package speedcubing.server;

import speedcubing.spigot.Event.ServerEventHandler;
import speedcubing.spigot.Event.events.TabCompleteCommandEvent;

public class ServerEvent {
    @ServerEventHandler
    public void TabCompleteCommandEvent(TabCompleteCommandEvent e) {
        if (e.message.equals("/") || e.message.equals("/ver ") || e.message.equals("/version "))
            e.completions = null;
    }
}

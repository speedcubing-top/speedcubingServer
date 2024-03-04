package top.speedcubing.server.commandoverrider;

import org.bukkit.command.CommandSender;
import top.speedcubing.lib.utils.collection.Sets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OverrideCommandManager {
    static Map<String, OverrideCommandManager> cmds = new HashMap<>();


    public static void register(OverrideCommandManager... commandManagers) {
        for (OverrideCommandManager s : commandManagers)
            for (String ss : s.getAlias())
                cmds.put(ss, s);
    }

    public static boolean dispatchOverride(CommandSender sender, String command, String[] strings) {
        OverrideCommandManager overridedCommand = cmds.get(command);
        if (overridedCommand != null) {
            overridedCommand.execute(sender, strings);
            return true;
        }
        return false;
    }

    private final Set<String> alias;

    public Set<String> getAlias() {
        return alias;
    }

    public OverrideCommandManager(String... command) {
        this.alias = Sets.hashSet(command);
    }

    public void execute(CommandSender sender, String[] strings) {

    }
}

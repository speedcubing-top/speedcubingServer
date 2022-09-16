package top.speedcubing.server.commandoverrider;

import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class OverrideCommandManager {
    static Map<String, OverridedCommand> cmds = new HashMap<>();

    public static void register(OverridedCommand overridedCommand, String... command) {
        for (String s : command) {
            cmds.put(s, overridedCommand);
        }
    }

    public static boolean dispatchOverride(CommandSender sender, String command, String[] strings) {
        OverridedCommand overridedCommand = cmds.get(command);
        if (overridedCommand != null) {
            overridedCommand.execute(sender, strings);
            return true;
        }
        return false;
    }

    public interface OverridedCommand {
        void execute(CommandSender sender, String[] strings);
    }
}

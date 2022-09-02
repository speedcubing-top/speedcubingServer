package top.speedcubing.server.commandoverrider;

import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class OverrideCommandManager {
    static Map<String, OverridedCommand> cmds = new HashMap<>();

    public static void register(String command, OverridedCommand overridedCommand) {
        cmds.put(command, overridedCommand);
    }

    public static boolean dispatchOverride(CommandSender sender, String command, String message) {
        OverridedCommand overridedCommand = cmds.get(command);
        if (overridedCommand != null) {
            overridedCommand.execute(sender, message);
            return true;
        }
        return false;
    }

    public interface OverridedCommand {
        void execute(CommandSender sender, String message);
    }
}

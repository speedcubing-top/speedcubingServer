package top.speedcubing.server.system.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.CommandSender;

public class CubingCommandManager {
    private static final Map<String, CubingCommand> cmds = new HashMap<>();

    public static void register(CubingCommand... commandManagers) {
        for (CubingCommand s : commandManagers) {
            for (String ss : s.getAlias()) {
                cmds.put(ss, s);
            }
        }
    }

    public static boolean execute(CommandSender sender, String command, String[] strings) {
        CubingCommand cubingCommand = cmds.get(command);
        if (cubingCommand != null) {
            cubingCommand.execute(sender, command, strings);
            return true;
        }
        return false;
    }
}

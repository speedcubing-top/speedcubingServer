package top.speedcubing.server.system.command;

import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import top.speedcubing.lib.utils.collection.Sets;

public abstract class CubingCommand {

    private final Set<String> alias;

    public Set<String> getAlias() {
        return alias;
    }

    public CubingCommand(String... command) {
        this.alias = Sets.hashSet(command);
    }

    public abstract void execute(CommandSender sender, String command, String[] args);

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args, Location location) {
        return null;
    }

    public boolean shouldLoad() {
        return true;
    }

    public void load() {
        CubingCommandManager.register(this);
    }
}

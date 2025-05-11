package top.speedcubing.server.system.command;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

public abstract class CubingCommand {

    private final Set<String> alias;

    public final Set<String> getAlias() {
        return alias;
    }

    public CubingCommand(String... command) {
        this.alias = Sets.newHashSet(command);
    }

    public abstract void execute(CommandSender sender, String command, String[] args);

    public List<String> onTabComplete(CommandSender sender, String command, String[] args, Location location) {
        return Collections.emptyList();
    }

    public boolean shouldLoad() {
        return true;
    }

    final void load() {
        CubingCommandManager.register(this);
    }
}

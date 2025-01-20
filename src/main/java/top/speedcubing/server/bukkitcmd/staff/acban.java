package top.speedcubing.server.bukkitcmd.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class acban implements CommandExecutor {

    private final JavaPlugin plugin;

    public acban(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("acban") && args.length > 0) {
            String playerName = args[0];
            Player target = Bukkit.getPlayer(playerName);

            if (target != null) {
                broadcastBanToProxy(playerName);
                return true;
            } else {
                sender.sendMessage("Player not found.");
            }
        }
        return false;
    }

    private void broadcastBanToProxy(String playerName) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             DataOutputStream dataOut = new DataOutputStream(out)) {
            System.out.print("Banning " + playerName + " from proxy");
            dataOut.writeUTF(playerName.trim());
            Bukkit.getServer().sendPluginMessage(plugin, "velocity:ban", out.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
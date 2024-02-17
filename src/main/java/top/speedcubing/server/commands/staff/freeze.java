package top.speedcubing.server.commands.staff;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class freeze implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        // Check if the command has exactly one argument
        if (strings.length != 1) {
            commandSender.sendMessage("Usage: /freeze <player>");
            return true;
        }

        // Get the target player
        Player targetPlayer = Bukkit.getPlayer(strings[0]);

        // Check if the target player is online
        if (targetPlayer == null) {
            commandSender.sendMessage("Player '" + strings[0] + "' not found or not online.");
            return true;
        }

        // Get the NMS player
        EntityPlayer nmsPlayer = ((CraftPlayer) targetPlayer).getHandle();
        nmsPlayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(7, 25));
        commandSender.sendMessage("Frozen " + nmsPlayer.getName() + "!");
        return true;
    }
}

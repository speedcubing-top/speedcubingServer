package top.speedcubing.server.commands.staff;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutGameStateChange;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class deepfry implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        // Check if the command has exactly one argument
        if (strings.length != 1) {
            commandSender.sendMessage("Usage: /deepfry <player>");
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
        targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE / 2, 9));
        nmsPlayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(2, 0));
        nmsPlayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(8, 1000f));
        nmsPlayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(7, 4));
        targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 9));
        targetPlayer.removePotionEffect(PotionEffectType.NIGHT_VISION);
        commandSender.sendMessage("Deep fried " + targetPlayer.getName() + "!");
        //nmsPlayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(7, 0));


        return true;
    }
}
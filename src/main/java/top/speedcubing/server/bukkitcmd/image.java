package top.speedcubing.server.bukkitcmd;

import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;
import top.speedcubing.server.authenticator.ImageRenderer;
import top.speedcubing.server.speedcubingServer;

public class image implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        String url;
        Player target;
        if (commandSender instanceof Player) {
            if (strings.length != 1) {
                commandSender.sendMessage("§cUsage: /image <url>");
                return true;
            }
            target = (Player) commandSender;
            url = strings[0];
        } else {
            if (strings.length != 2) {
                commandSender.sendMessage("§cUsage: /image <player> <url>");
                return true;
            }
            target = Bukkit.getPlayerExact(strings[0]);
            url = strings[1];
        }
        render(commandSender, target, url);
        return true;
    }

    private void render(CommandSender sender, Player player, String url) {
        if (player == null) {
            sender.sendMessage("§cPlayer not found!");
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                MapView view = Bukkit.createMap(player.getWorld());
                view.getRenderers().forEach(view::removeRenderer);
                try {
                    ImageRenderer renderer = new ImageRenderer(url);
                    view.addRenderer(renderer);
                    ItemStack mapItem = new ItemStack(Material.MAP, 1, view.getId());
                    ItemMeta mapMeta = mapItem.getItemMeta();
                    mapItem.setItemMeta(mapMeta);
                    player.getInventory().addItem(mapItem);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    sender.sendMessage("§cAn error occurred! Is the URL correct?");
                }
            }
        }.runTaskAsynchronously(speedcubingServer.instance);
    }
}

package top.speedcubing.server.commands;

import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;
import top.speedcubing.server.authenticator.utils.ImageRenderer;
import top.speedcubing.server.speedcubingServer;

public class image implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (strings.length != 1) {
                player.sendMessage("§cUsage: /image <url>");
            } else {
                String url = strings[0];
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
                        } catch (IOException ee) {
                            ee.printStackTrace();
                            player.sendMessage(ChatColor.RED + "An error occurred! Is the URL correct?");
                        }
                    }
                }.runTaskAsynchronously(speedcubingServer.instance);
            }
        } else {
            if (strings.length != 2) {
                commandSender.sendMessage("§cUsage: /image <player> <url>");
                return true;
            }
            String targetName = strings[0];
            Player target = Bukkit.getPlayerExact(targetName);
            if (target == null) {
                commandSender.sendMessage("§cPlayer not found!");
                return true;
            }
            String url = strings[1];
            new BukkitRunnable() {
                @Override
                public void run() {
                    MapView view = Bukkit.createMap(target.getWorld());
                    view.getRenderers().forEach(view::removeRenderer);
                    try {
                        ImageRenderer renderer = new ImageRenderer(url);
                        view.addRenderer(renderer);
                        ItemStack mapItem = new ItemStack(Material.MAP, 1, view.getId());
                        ItemMeta mapMeta = mapItem.getItemMeta();
                        mapItem.setItemMeta(mapMeta);
                        target.getInventory().addItem(mapItem);
                    } catch (IOException ee) {
                        ee.printStackTrace();
                        target.sendMessage("§cAn error occurred! Is the URL correct?");
                    }
                }
            }.runTaskAsynchronously(speedcubingServer.instance);
        }
        return true;
    }
}

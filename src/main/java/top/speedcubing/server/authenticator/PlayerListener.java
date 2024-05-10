package top.speedcubing.server.authenticator;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import java.io.IOException;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;
import top.speedcubing.server.player.User;
import top.speedcubing.server.speedcubingServer;

public class PlayerListener implements Listener {
    private final String qrCodeURL = "https://www.google.com/chart?chs=128x128&cht=qr&chl=otpauth://totp/%%label%%?secret=%%key%%";

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        User user = User.getUser(p);

        AuthData auth = new AuthData(user);

        if (auth.isAuthBypass())
            return;

        if (!auth.isAuthEnable()) {
            if (user.isStaff) {
                auth.setAuthEnable();
                p.kickPlayer("§aForced turn on 2FA\nPlease rejoin server!");
            }
            return;
        }

        if (Bukkit.getServerName().equalsIgnoreCase("limbo"))
            return;

        AuthData.map.put(user, auth);

        if (!auth.hasKey()) {
            if (auth.noKey == null) {
                GoogleAuthenticator authenticator = new GoogleAuthenticator();
                GoogleAuthenticatorKey key2 = authenticator.createCredentials();
                auth.noKey = key2.getKey();
                AuthMessenger.sendSetKeyMessage(p);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    String url = replaceLabel(qrCodeURL, "Speedcubing: " + user.bGetName());
                    String finallyUrl = replaceKey(url, auth.noKey);
                    MapView view = Bukkit.createMap(p.getWorld());
                    view.getRenderers().forEach(view::removeRenderer);
                    try {
                        ImageRenderer renderer = new ImageRenderer(finallyUrl);
                        view.addRenderer(renderer);
                        ItemStack mapItem = new ItemStack(Material.MAP, 1, view.getId());
                        ItemMeta mapMeta = mapItem.getItemMeta();
                        mapMeta.setDisplayName(ChatColor.GOLD + "QR Code");
                        mapItem.setItemMeta(mapMeta);
                        p.getInventory().addItem(mapItem);
                        p.sendMessage("§6Your QRCode URL: " + finallyUrl);
                    } catch (IOException ee) {
                        ee.printStackTrace();
                        p.sendMessage(ChatColor.RED + "An error occurred! Is the URL correct?");
                    }
                }
            }.runTaskAsynchronously(speedcubingServer.instance);
        }
        String oldIp = auth.getIp();

        String ip = p.getAddress().getAddress().getHostAddress();
        if (oldIp != null && !oldIp.isEmpty()) {
            if (!ip.equals(oldIp)) {
                auth.setSession(false);
                auth.setIp(ip);
            }
        } else {
            auth.setIp(ip);
        }
    }

    @EventHandler
    public void onInteraction(PlayerInteractEvent e) {
        User user = User.getUser(e.getPlayer());
        AuthData auth = AuthData.map.get(user);

        if(auth == null)
            return;

        if (auth.allowAction())
            return;

        e.setCancelled(true);

        if (auth.hasKey()) {
            AuthMessenger.sendEnterCodeMessage(e.getPlayer());
        } else {
            AuthMessenger.sendSetKeyMessage(e.getPlayer());
        }
    }

    //this event is blocked paper command, proxy command blocker in speedcubingProxy
    @EventHandler
    public void onCmdExecute(PlayerCommandPreprocessEvent e) {
        AuthData auth = AuthData.map.get(User.getUser(e.getPlayer()));

        if(auth == null)
            return;

        if (auth.allowAction())
            return;

        if (!e.getMessage().contains("2fa")) {
            e.setCancelled(true);
            if (auth.hasKey()) {
                AuthMessenger.sendEnterCodeMessage(e.getPlayer());
            } else {
                AuthMessenger.sendSetKeyMessage(e.getPlayer());
            }
        }
    }


    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        AuthData auth = AuthData.map.get(User.getUser(e.getPlayer()));

        if(auth == null)
            return;

        if (auth.allowAction())
            return;

        InventoryType inventoryType = e.getInventory().getType();
        if (inventoryType == InventoryType.CHEST) {
            e.setCancelled(true);
            if (auth.hasKey()) {
                AuthMessenger.sendEnterCodeMessage((Player) e.getPlayer());
            } else {
                AuthMessenger.sendSetKeyMessage((Player) e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        AuthData auth = AuthData.map.get(User.getUser(e.getPlayer()));

        if(auth == null)
            return;

        if (auth.allowAction()) {
            return;
        }

        Location oldLoc = e.getFrom();
        Location newLoc = e.getTo();
        if (newLoc.getX() != oldLoc.getX() || newLoc.getZ() != oldLoc.getZ()) {
            e.getPlayer().teleport(oldLoc);

            if (auth.hasKey()) {
                AuthMessenger.sendEnterCodeMessage(e.getPlayer());
            } else {
                AuthMessenger.sendSetKeyMessage(e.getPlayer());
            }
        }
    }

    public String replaceLabel(String qrCodeURL, String label) {
        return qrCodeURL.replace("%%label%%", label);
    }

    public String replaceKey(String qrCodeURL, String key) {
        return qrCodeURL.replace("%%key%%", key);
    }

}

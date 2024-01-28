package top.speedcubing.server.authenticator.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;
import top.speedcubing.server.authenticator.handlers.AuthHandler;
import top.speedcubing.server.authenticator.utils.ImageRenderer;
import top.speedcubing.server.player.User;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import top.speedcubing.server.speedcubingServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerListener implements Listener {
    public static Map<UUID, String> keyMap = new HashMap<>();
    private String qrCodeURL = "https://www.google.com/chart?chs=128x128&cht=qr&chl=otpauth://totp/%%label%%?secret=%%key%%";

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        User user = User.getUser(p);
        String ip = p.getAddress().getAddress().getHostAddress();
        boolean isAuthEnable = AuthHandler.isEnable(uuid);
        boolean isAuthBypass = AuthHandler.hasBypass(uuid);
        if (isAuthBypass) {
            return;
        }
        if (forcedAuthForStaff(user, isAuthEnable)) {
            p.kickPlayer("§aForced turn on 2FA\nPlease rejoin server!");
            return;
        }

        if (!isAuthEnable) {
            return;
        }

        String authKey = AuthHandler.getKey(uuid);
        if (authKey == null || authKey.isEmpty()) {
            if (!keyMap.containsKey(uuid)) {
                GoogleAuthenticator authenticator = new GoogleAuthenticator();
                GoogleAuthenticatorKey key = authenticator.createCredentials();
                String keyString = key.getKey();
                keyMap.put(uuid, keyString);
                AuthHandler.sendSetKeyMessage(p);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    String url = replaceLabel(qrCodeURL, "Speedcubing:" + user.bGetName());
                    String finallyUrl = replaceKey(url, keyMap.get(uuid));
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
                        p.sendMessage("Your QRCode URL: " + finallyUrl);
                    } catch (IOException ee) {
                        ee.printStackTrace();
                        p.sendMessage(ChatColor.RED + "An error occurred! Is the URL correct?");
                    }
                }
            }.runTaskAsynchronously(speedcubingServer.instance);
        } else if (AuthHandler.hasTrustedSessions(uuid)) {
            String oldIp = AuthHandler.getIp(uuid);
            if (oldIp != null && !oldIp.isEmpty()) {
                if (!ip.equals(oldIp)) {
                    AuthHandler.setTrustedSessions(p,false);
                    AuthHandler.setIp(uuid,ip);
                }
            } else {
                AuthHandler.setIp(uuid,ip);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (keyMap.containsKey(e.getPlayer().getUniqueId())) {
            keyMap.remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onInteraction(PlayerInteractEvent e) { //make enabl;e check
        if (AuthHandler.isEnable(e.getPlayer().getUniqueId())) {
            if (!AuthHandler.hasTrustedSessions(e.getPlayer().getUniqueId())) {
                e.setCancelled(true);
                if (AuthHandler.hasKey(e.getPlayer().getUniqueId())) {
                    AuthHandler.sendEnterCodeMessage(e.getPlayer());
                } else {
                    AuthHandler.sendSetKeyMessage(e.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onCmdExecute(PlayerCommandPreprocessEvent e) {
        if (AuthHandler.isEnable(e.getPlayer().getUniqueId())) {
            if (!AuthHandler.hasTrustedSessions(e.getPlayer().getUniqueId())) {
                if (!e.getMessage().contains("2fa")) {
                    e.setCancelled(true);
                    if (AuthHandler.hasKey(e.getPlayer().getUniqueId())) {
                        AuthHandler.sendEnterCodeMessage(e.getPlayer());
                    } else {
                        AuthHandler.sendSetKeyMessage(e.getPlayer());
                    }
                }
            }
        }
    }
    @EventHandler
    public void onProxyCommand(AsyncPlayerChatEvent e) {
        if (AuthHandler.isEnable(e.getPlayer().getUniqueId())) {
            if (!AuthHandler.hasTrustedSessions(e.getPlayer().getUniqueId())) {
                String cmd = e.getMessage();
                if (cmd.startsWith("/")) {
                    if (cmd.contains("2fa") || cmd.contains("l")) {
                        e.setCancelled(true);
                        if (AuthHandler.hasKey(e.getPlayer().getUniqueId())) {
                            AuthHandler.sendEnterCodeMessage(e.getPlayer());
                        } else {
                            AuthHandler.sendSetKeyMessage(e.getPlayer());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (AuthHandler.isEnable(e.getPlayer().getUniqueId())) {
            if (!AuthHandler.hasTrustedSessions(e.getPlayer().getUniqueId())) {
                Location oldLoc = e.getFrom();
                Location newLoc = e.getTo();
                if (newLoc.getX() != oldLoc.getX() || newLoc.getZ() != oldLoc.getZ()) {
                    e.getPlayer().teleport(oldLoc);
                    if (AuthHandler.hasKey(e.getPlayer().getUniqueId())) {
                        AuthHandler.sendEnterCodeMessage(e.getPlayer());
                    } else {
                        AuthHandler.sendSetKeyMessage(e.getPlayer());
                    }
                }
            }
        }
    }

    public String replaceLabel(String qrCodeURL, String label) {
        return qrCodeURL.replace("%%label%%", label);
    }

    public String replaceKey(String qrCodeURL, String key) {
        return qrCodeURL.replace("%%key%%", key);
    }

    private boolean forcedAuthForStaff(User user, boolean auth) {
        if (user.isStaff) {
            if (!auth) {
                user.dbUpdate("auth_enable=" + 1);
                return true;
            }
        }
        return false;
    }


}

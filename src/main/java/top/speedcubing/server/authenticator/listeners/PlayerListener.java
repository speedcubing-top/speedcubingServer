package top.speedcubing.server.authenticator.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;
import top.speedcubing.server.authenticator.events.AuthKeyChangeEvent;
import top.speedcubing.server.authenticator.events.AuthSessionChangeEvent;
import top.speedcubing.server.authenticator.events.AuthStatusChangeEvent;
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
    public static Map<UUID, String> keyMapForNoKey = new HashMap<>(); //this map is for no key
    public static Map<UUID, Boolean> twofaStatusMap = new HashMap<>();
    public static Map<UUID, Boolean> sessionStatusMap = new HashMap<>();
    public static Map<UUID, Boolean> hasKeyMap = new HashMap<>();
    public static Map<UUID, String> keyMapForHasKey = new HashMap<>();
    private final String qrCodeURL = "https://www.google.com/chart?chs=128x128&cht=qr&chl=otpauth://totp/%%label%%?secret=%%key%%";

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        User user = User.getUser(p);
        String ip = p.getAddress().getAddress().getHostAddress();
        boolean isAuthEnable = AuthHandler.isEnable(uuid);
        boolean hasSessions = AuthHandler.hasTrustedSessions(uuid);
        boolean isAuthBypass = AuthHandler.hasBypass(uuid);
        boolean hasKey = AuthHandler.hasKey(uuid);
        hasKeyMap.put(uuid, hasKey);
        twofaStatusMap.put(uuid, isAuthEnable);
        sessionStatusMap.put(uuid, hasSessions);
        if (isAuthBypass) {
            return;
        }
        if (forcedAuthForStaff(user, uuid, isAuthEnable)) {
            p.kickPlayer("§aForced turn on 2FA\nPlease rejoin server!");
            return;
        }

        if (!isAuthEnable) {
            return;
        }
        if (!hasKeyMap.containsKey(uuid)) {
            return;
        }
        if (!hasKeyMap.get(uuid)) {
            if (!keyMapForNoKey.containsKey(uuid)) {
                GoogleAuthenticator authenticator = new GoogleAuthenticator();
                GoogleAuthenticatorKey key = authenticator.createCredentials();
                String keyString = key.getKey();
                keyMapForNoKey.put(uuid, keyString);
                AuthHandler.sendSetKeyMessage(p);
            } else {
                keyMapForHasKey.put(uuid, AuthHandler.getKey(uuid));
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    String url = replaceLabel(qrCodeURL, "Speedcubing:" + user.bGetName());
                    String finallyUrl = replaceKey(url, keyMapForNoKey.get(uuid));
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
        } else if (AuthHandler.hasTrustedSessions(uuid)) {
            String oldIp = AuthHandler.getIp(uuid);
            if (oldIp != null && !oldIp.isEmpty()) {
                if (!ip.equals(oldIp)) {
                    AuthHandler.setTrustedSessions(p, false);
                    AuthHandler.setIp(uuid, ip);
                }
            } else {
                AuthHandler.setIp(uuid, ip);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (keyMapForNoKey.containsKey(e.getPlayer().getUniqueId())) {
            keyMapForNoKey.remove(e.getPlayer().getUniqueId());
        }
        if (twofaStatusMap.containsKey(e.getPlayer().getUniqueId())) {
            twofaStatusMap.remove(e.getPlayer().getUniqueId());
        }
        if (sessionStatusMap.containsKey(e.getPlayer().getUniqueId())) {
            sessionStatusMap.remove(e.getPlayer().getUniqueId());
        }
        if (hasKeyMap.containsKey(e.getPlayer().getUniqueId())) {
            hasKeyMap.remove(e.getPlayer().getUniqueId());
        }
        if (keyMapForHasKey.containsKey(e.getPlayer().getUniqueId())) {
            keyMapForHasKey.remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onInteraction(PlayerInteractEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if (!twofaStatusMap.containsKey(uuid)) {
            return;
        }
        if (!twofaStatusMap.get(uuid)) {
            return;
        }
        if (!sessionStatusMap.containsKey(uuid)) {
            return;
        }
        if (sessionStatusMap.get(uuid)) {
            return;
        }
        e.setCancelled(true);
        if (!hasKeyMap.containsKey(uuid)) {
            return;
        }
        if (hasKeyMap.get(uuid)) {
            AuthHandler.sendEnterCodeMessage(e.getPlayer());
        } else {
            AuthHandler.sendSetKeyMessage(e.getPlayer());
        }
    }

    @EventHandler
    public void onCmdExecute(PlayerCommandPreprocessEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if (!twofaStatusMap.containsKey(uuid)) {
            return;
        }
        if (!twofaStatusMap.get(uuid)) {
            return;
        }
        if (!sessionStatusMap.containsKey(uuid)) {
            return;
        }
        if (sessionStatusMap.get(uuid)) {
            return;
        }
        if (!e.getMessage().contains("2fa")) {
            e.setCancelled(true);
            if (!hasKeyMap.containsKey(uuid)) {
                return;
            }
            if (hasKeyMap.get(uuid)) {
                AuthHandler.sendEnterCodeMessage(e.getPlayer());
            } else {
                AuthHandler.sendSetKeyMessage(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onProxyCommand(AsyncPlayerChatEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if (!twofaStatusMap.containsKey(uuid)) {
            return;
        }
        if (!twofaStatusMap.get(uuid)) {
            return;
        }
        if (!sessionStatusMap.containsKey(uuid)) {
            return;
        }
        if (sessionStatusMap.get(uuid)) {
            return;
        }
        String cmd = e.getMessage();
        if (cmd.startsWith("/")) {
            if (cmd.contains("2fa") || cmd.contains("l")) {
                e.setCancelled(true);
                if (!hasKeyMap.containsKey(uuid)) {
                    return;
                }
                if (hasKeyMap.get(uuid)) {
                    AuthHandler.sendEnterCodeMessage(e.getPlayer());
                } else {
                    AuthHandler.sendSetKeyMessage(e.getPlayer());
                }
            }
        }
    }
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        InventoryType inventoryType = e.getInventory().getType();
        if (inventoryType == InventoryType.CHEST) {
            if (!twofaStatusMap.containsKey(uuid)) {
                return;
            }
            if (!twofaStatusMap.get(uuid)) {
                return;
            }
            if (!sessionStatusMap.containsKey(uuid)) {
                return;
            }
            if (sessionStatusMap.get(uuid)) {
                return;
            }
            if (!hasKeyMap.containsKey(uuid)) {
                return;
            }
            e.setCancelled(true);
            if (hasKeyMap.get(uuid)) {
                AuthHandler.sendEnterCodeMessage((Player) e.getPlayer());
            } else {
                AuthHandler.sendSetKeyMessage((Player) e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if (!twofaStatusMap.containsKey(uuid)) {
            return;
        }
        if (!twofaStatusMap.get(uuid)) {
            return;
        }
        if (!sessionStatusMap.containsKey(uuid)) {
            return;
        }
        if (sessionStatusMap.get(uuid)) {
            return;
        }

        Location oldLoc = e.getFrom();
        Location newLoc = e.getTo();
        if (newLoc.getX() != oldLoc.getX() || newLoc.getZ() != oldLoc.getZ()) {
            e.getPlayer().teleport(oldLoc);
            if (!hasKeyMap.containsKey(uuid)) {
                return;
            }
            if (hasKeyMap.get(uuid)) {
                AuthHandler.sendEnterCodeMessage(e.getPlayer());
            } else {
                AuthHandler.sendSetKeyMessage(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onAuthStatusChange(AuthStatusChangeEvent e) {
        boolean status = e.getStatus();
        UUID uuid = e.getUuid();
        if (uuid != null) {
            twofaStatusMap.put(uuid, status);
        }
    }

    @EventHandler
    public void onAuthSessionsChange(AuthSessionChangeEvent e) {
        boolean status = e.getStatus();
        UUID uuid = e.getPlayer().getUniqueId();
        User user = e.getUser();
        if (uuid != null) {
            sessionStatusMap.put(uuid, status);
        } else if (user != null) {
            sessionStatusMap.put(uuid, status);
        } else {
            System.out.println("An error occurred");
        }
    }

    @EventHandler
    public void onAuthKeyChange(AuthKeyChangeEvent e) {
        UUID uuid = e.getUuid();
        String key = e.getKey();
        if (uuid != null && key != null) {
            keyMapForHasKey.put(uuid, key);
            hasKeyMap.put(uuid, true);
        } else {
            hasKeyMap.put(uuid, false);
        }
    }

    public String replaceLabel(String qrCodeURL, String label) {
        return qrCodeURL.replace("%%label%%", label);
    }

    public String replaceKey(String qrCodeURL, String key) {
        return qrCodeURL.replace("%%key%%", key);
    }

    private boolean forcedAuthForStaff(User user, UUID uuid, boolean auth) {
        if (user.isStaff) {
            if (!auth) {
                AuthHandler.setEnable(uuid, true);
                //user.dbUpdate("auth_enable=" + 1);
                return true;
            }
        }
        return false;
    }


}

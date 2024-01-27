package top.speedcubing.server.authenticator.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;
import top.speedcubing.server.authenticator.utils.ImageRenderer;
import top.speedcubing.server.database.Database;
import top.speedcubing.server.player.User;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import top.speedcubing.server.speedcubingServer;

import java.io.IOException;
import java.lang.reflect.Member;
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
        boolean isAuthEnable = Database.connection.select("auth_enable").from("playersdata").where("uuid='" + uuid + "'").getBoolean();
        boolean isAuthBypass = Database.connection.select("auth_bypass").from("playersdata").where("uuid='" + uuid + "'").getBoolean();
        if (isAuthBypass) {
            return;
        }
        if (forcedAuthForStaff(user,isAuthEnable)) {
            p.kickPlayer("§aForced turn on 2FA\nPlease rejoin server!");
            return;
        }

        if (isAuthEnable) {
            String authKey = Database.connection.select("auth_key").from("playersdata").where("uuid='" + uuid + "'").getString();
            if (authKey.isEmpty()) {
                if (keyMap.containsKey(uuid)) {

                } else {
                    GoogleAuthenticator authenticator = new GoogleAuthenticator();
                    GoogleAuthenticatorKey key = authenticator.createCredentials();
                    String keyString = key.getKey();
                    keyMap.put(uuid,keyString);
                }
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    String url = replaceLabel(qrCodeURL,"Speedcubing:" + user.bGetName());
                    String finallyUrl = replaceKey(url,keyMap.get(uuid));
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
                    } catch (IOException ee) {
                        ee.printStackTrace();
                        p.sendMessage(ChatColor.RED + "An error occurred! Is the URL correct?");
                    }
                }
            }.runTaskAsynchronously(speedcubingServer.instance);
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

package top.speedcubing.server.libs;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;
import top.speedcubing.lib.bungee.TextBuilder;
import top.speedcubing.lib.utils.SQL.SQLConnection;
import top.speedcubing.server.config;
import top.speedcubing.server.speedcubingServer;

import java.util.*;
import java.util.regex.Pattern;

public class User {

    public static User getUser(int id) {
        return usersByID.get(id);
    }

    public static User getUser(CommandSender sender) {
        return usersByUUID.get(((Player) sender).getUniqueId());
    }

    public static Collection<User> getUsers() {
        return usersByID.values();
    }

    public static Map<Integer, User> usersByID = new HashMap<>();
    public static Map<UUID, User> usersByUUID = new HashMap<>();
    public final Player player;

    public Set<String> permissions;
    public double[] velocities;
    public int lang;
    public final int id;
    public String rank;
    public int tcpPort;
    public boolean allowOp;
    public boolean chatFilt;
    public boolean listened;
    public final String realName;

    public PacketPlayOutScoreboardTeam joinPacket;
    public PacketPlayOutScoreboardTeam leavePacket;
    public int leftClick;
    public int rightClick;
    public boolean vanished;
    public long lastInvClick;
    public static Pattern group = Pattern.compile("^group\\.[^|*.]+$");

    public User(Player player, String rank, Set<String> permissions, int lang, int id, boolean allowOp, PreLoginData bungeeData, boolean chatFilt, String realName) {
        this.player = player;
        Set<String> groups = new HashSet<>();
        for (String s : permissions) {
            if (group.matcher(s).matches() && config.grouppermissions.containsKey(s.substring(6)))
                groups.add(s.substring(6));
        }
        groups.forEach(a -> permissions.addAll(config.grouppermissions.get(a)));
        this.permissions = permissions;
        speedcubingServer.preLoginStorage.remove(id);
        this.listened = bungeeData.cps;
        if (!bungeeData.hor.equals("null"))
            this.velocities = new double[]{Double.parseDouble(bungeeData.hor), Double.parseDouble(bungeeData.ver)};
        this.lang = lang;
        this.id = id;
        this.chatFilt = chatFilt;
        this.realName = realName;
        this.rank = rank;
        this.vanished = bungeeData.vanished;
        this.tcpPort = bungeeData.port;
        this.allowOp = allowOp;
        usersByID.put(id, this);
        usersByUUID.put(bGetUniqueId(), this);
    }


    public Vector applyKnockback(Vector v) {
        double[] d = velocities;
        return d == null ? v : v.setX(v.getX() * d[0]).setY(v.getY() * d[1]).setZ(v.getZ() * d[0]);
    }

    public void sendLangMessage(String[] s) {
        bSendMessage(s[lang]);
    }

    public void openLangInventory(Inventory[] inventories) {
        bOpenInventory(inventories[lang]);
    }

    public void sendLangTextComp(TextBuilder[] s) {
        player.spigot().sendMessage(s[lang].build());
    }

    public LangMessage langMessageSender(String[] s) {
        return new LangMessage(s[lang], this);
    }

    public SQLConnection.SQLPrepare dbSelect(String field) {
        return speedcubingServer.connection.select(field).from("playersdata").where("id=" + id);
    }

    public PlayerConnection playerConn() {
        return toNMS().playerConnection;
    }

    public EntityPlayer toNMS() {
        return ((CraftPlayer) player).getHandle();
    }

    public void sendPacket(Packet<?>... packets) {
        for (Packet<?> p : packets)
            playerConn().sendPacket(p);
    }

    public void sound(Sound sound) {
        bPlaySound(player.getLocation(), sound, 1, 1);
    }

    public void dbUpdate(String field) {
        speedcubingServer.connection.update("playersdata", field, "id=" + id);
    }

    //bukkit

    public ItemStack bGetItemInHand() {
        return player.getItemInHand();
    }

    public boolean bTeleport(Location location) {
        return player.teleport(location);
    }

    public void bCloseInventory() {
        player.closeInventory();
    }

    public void bSetAllowFlight(boolean b) {
        player.setAllowFlight(b);
    }

    public void bSetFlying(boolean b) {
        player.setFlying(b);
    }

    public void bSetMaximumNoDamageTicks(int i) {
        player.setMaximumNoDamageTicks(i);
    }

    public void bUpdateInventory() {
        player.updateInventory();
    }

    public void bSetLevel(int i) {
        player.setLevel(i);
    }

    public void bSetExp(float v) {
        player.setExp(v);
    }


    public void bSetHealth(double v) {
        player.setHealth(v);
    }

    public double bGetHealth() {
        return player.getHealth();
    }

    public void bSetFireTicks(int i) {
        player.setFireTicks(i);
    }

    public String bGetName() {
        return player.getName();
    }

    public void bHidePlayer(Player player) {
        player.hidePlayer(player);
    }

    public void bShowPlayer(Player player) {
        player.showPlayer(player);
    }

    public void bSetGameMode(GameMode mode) {
        player.setGameMode(mode);
    }

    public InventoryView bOpenInventory(Inventory inventory) {
        return player.openInventory(inventory);
    }

    public void bPlaySound(Location location, Sound sound, float v1, float v2) {
        player.playSound(location, sound, v1, v2);
    }

    public PlayerInventory bGetInventory() {
        return player.getInventory();
    }

    public World bGetWorld() {
        return player.getWorld();
    }

    public Location bGetLocation() {
        return player.getLocation();
    }

    public UUID bGetUniqueId() {
        return player.getUniqueId();
    }


    public void bSendMessage(String s) {
        player.sendMessage(s);
    }

    public Player.Spigot bSpigot() {
        return player.spigot();
    }

    public Scoreboard bGetScoreboard() {
        return player.getScoreboard();
    }

    public boolean bIsSneaking() {
        return player.isSneaking();
    }
    //bukkit end

    public static class LangMessage {

        private String s;
        private final User user;

        public LangMessage(String s, User user) {
            this.s = s;
            this.user = user;
        }

        public LangMessage replace(String s1, String s2) {
            s = s.replace(s1, s2);
            return this;
        }

        public void send() {
            user.player.sendMessage(s);
        }
    }
}

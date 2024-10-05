package top.speedcubing.server.player;

import com.mojang.authlib.properties.Property;
import java.time.ZoneId;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityVelocity;
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
import top.speedcubing.common.database.Database;
import top.speedcubing.common.rank.IDPlayer;
import top.speedcubing.common.rank.Rank;
import top.speedcubing.common.rank.RankFormat;
import top.speedcubing.lib.api.mojang.Skin;
import top.speedcubing.lib.bukkit.PlayerUtils;
import top.speedcubing.lib.bukkit.entity.Hologram;
import top.speedcubing.lib.bukkit.packetwrapper.OutScoreboardTeam;
import top.speedcubing.lib.minecraft.text.ComponentText;
import top.speedcubing.lib.utils.SQL.SQLConnection;
import top.speedcubing.lib.utils.SQL.SQLRow;
import top.speedcubing.lib.utils.UUIDUtils;
import top.speedcubing.lib.utils.bytes.ByteArrayBuffer;
import top.speedcubing.lib.utils.bytes.NumberConversion;
import top.speedcubing.lib.utils.internet.HostAndPort;
import top.speedcubing.lib.utils.sockets.TCPClient;
import top.speedcubing.server.lang.Lang;
import top.speedcubing.server.lang.LangInv;
import top.speedcubing.server.lang.LangItem;
import top.speedcubing.server.login.PreLoginData;
import top.speedcubing.server.utils.RankSystem;

public class User extends IDPlayer {

    public static Map<Integer, User> usersByID = new HashMap<>();
    public static Map<UUID, User> usersByUUID = new HashMap<>();


    public static User getUser(CommandSender sender) {
        return getUser(((Player) sender).getUniqueId());
    }

    public static User getUser(int id) {
        return usersByID.get(id);
    }

    public static User getUser(UUID uuid) {
        return usersByUUID.get(uuid);
    }

    public static Collection<User> getUsers() {
        return usersByID.values();
    }

    public final Player player;
    public Hologram cpsHologram;
    public String lastTabbed;
    public Set<String> permissions;
    public double[] velocities;
    public int lang;
    public String displayRank;
    public HostAndPort proxy;
    public boolean chatFilt;
    public boolean listened;
    public PacketPlayOutScoreboardTeam joinPacket;
    public PacketPlayOutScoreboardTeam leavePacket;
    public Queue<Integer> leftClickQueue = new ArrayDeque<>(), rightClickQueue = new ArrayDeque<>();
    public int leftCPS, rightCPS, leftClickTick, rightClickTick;
    public boolean vanished;
    public final boolean isStaff;
    public long lastInvClick;
    public final String realRank;
    public long lastMove = System.currentTimeMillis();
    public String timeZone;
    public String status;
    public Skin defaultSkin;
    public boolean isCrashed;

    public User(Player player, String displayRank, String realRank, Set<String> permissions, SQLRow datas, PreLoginData bungeeData) {
        super(datas.getString("name"), player.getUniqueId(), datas.getInt("id"));
        this.player = player;
        this.permissions = permissions;
        this.listened = bungeeData.cps;
        this.lang = datas.getInt("lang");
        this.chatFilt = datas.getBoolean("chatfilt");
        this.realRank = realRank;
        this.displayRank = displayRank;
        this.vanished = bungeeData.vanished;
        this.proxy = bungeeData.proxy;
        this.isStaff = Rank.isStaff(realRank);
        this.timeZone = dbSelect("timezone").getString();
        this.status = dbSelect("status").getString() == null ? "null" : dbSelect("status").getString();
        this.defaultSkin = new Skin(datas.getString("profile_textures_value"), datas.getString("profile_textures_signature"));
        this.isCrashed = false;
        if (!bungeeData.hor.equals("null"))
            this.velocities = new double[]{Double.parseDouble(bungeeData.hor), Double.parseDouble(bungeeData.ver)};
        usersByID.put(id, this);
        usersByUUID.put(bGetUniqueId(), this);
    }

    public boolean nicked() { //if player is disguised now
        return !this.realName.equals(player.getName());
    }

    public boolean nickState() { //if player is switched to nick mode
        return dbSelect("nicked").getBoolean();
    }

    public Property getTextures() {
        return toNMS().getProfile().getProperties().get("textures").iterator().next();
    }

    public void updateSkin(Skin skin, String target) {
        if (target != null && target.equalsIgnoreCase(realName)) { //it is your own skin
            uploadSkin(new Skin("", ""));
        } else uploadSkin(skin);

        Property property = getTextures();
        if (property.getValue().equals(skin.getValue()) && property.getSignature().equals(skin.getSignature())) {
            return;
        }

        PlayerUtils.changeSkin(player, skin.getValue(), skin.getSignature());

        for (User u : User.getUsers()) {
            if (u.player.canSee(player)) {
                u.bHidePlayer(player);
                u.bShowPlayer(player);
            }
        }
    }

    public void uploadSkin(Skin skin) {
        dbUpdate("skinvalue='" + skin.getValue() + "',skinsignature='" + skin.getSignature() + "'");
    }

    public UUID calculateNickHashUUID() {
        //we use id as uuid, disguise everything
        int id = player.getEntityId();
        String hex = NumberConversion.toSizedHex(id, 32);
        return UUID.fromString(UUIDUtils.dash(hex));
    }

    //guild
    public String getGuild() {
        return dbSelect("guild").getString();
    }
    //perm

    public boolean hasPermission(String perm) {
        perm = perm.toLowerCase();
        if (permissions.contains(perm))
            return true;
        String[] s = perm.split("\\.");
        StringBuilder def = new StringBuilder(s[0]);
        if (permissions.contains(def + ".*"))
            return true;
        for (int i = 1; i < s.length - 1; i++) {
            def.append(".").append(s[i]);
            if (permissions.contains(def + ".*"))
                return true;
        }
        return false;
    }


    //RANK FORMAT
    public RankFormat getFormat(boolean real) {
        return Rank.getFormat(real ? realRank : displayRank, id);
    }

    public String getPrefix(boolean real) {
        return getFormat(real).getPrefix();
    }

    public String getChatColor(boolean real) {
        return getFormat(real).getChatColor();
    }

    public String getNameColor(boolean real) {
        return getFormat(real).getNameColor();
    }

    public String getColorName(boolean real) {
        return getFormat(real).getNameColor() + (real ? realName : bGetName());
    }

    public String getPrefixName(boolean real) {
        return getFormat(real).getPrefix() + (real ? realName : bGetName());
    }

    public String getColorNameChatColor(boolean real) {
        return getFormat(real).getNameColor() + (real ? realName : bGetName()) + getChatColor(real);
    }

    public String getPrefixNameChatColor(boolean real) {
        return getFormat(real).getPrefix() + (real ? realName : bGetName()) + getChatColor(real);
    }

    public void setInput(boolean add) {
        writeToProxy(new ByteArrayBuffer().writeUTF("inputmode").writeInt(id).writeBoolean(add).toByteArray());
    }

    public String getCurrentTime() {
        ZoneId zone = ZoneId.of(timeZone);
        return java.time.LocalTime.now(zone).toString();
    }

    //kb
    public Vector applyKnockback(Vector v) {
        double[] d = velocities;
        return d == null ? v : v.setX(v.getX() * d[0]).setY(v.getY() * d[1]).setZ(v.getZ() * d[0]);
    }

    //lang

    public void kick(String unformatted, String... param) {
        player.kickPlayer(Lang.of(unformatted, param).getString(lang));
    }

    public void openLangInventory(LangInv langInventory) {
        player.openInventory(langInventory.get(lang).getInventory());
    }

    public void setLangItem(int slot, LangItem stack) {
        bGetInventory().setItem(slot, stack.get(lang));
    }

    public void sendMessage(String unformatted, String... param) {
        sendMessage(Lang.of(unformatted, param));
    }

    public void sendMessage(Lang message, String... param) {
        sendComponent(message.param(param).get(lang));
    }

    public void sendMessage(ComponentText s) {
        sendComponent(s.toBungee());
    }

    public void sendComponent(TextComponent component) {
        player.spigot().sendMessage(component);
    }

    //nms
    public PlayerConnection playerConnection() {
        return toNMS().playerConnection;
    }

    public EntityPlayer toNMS() {
        return ((CraftPlayer) player).getHandle();
    }

    public void sendPacket(Packet<?>... packets) {
        PlayerConnection c = playerConnection();
        for (Packet<?> p : packets)
            c.sendPacket(p);
    }

    //tools
    public void sound(Sound sound) {
        bPlaySound(player.getLocation(), sound, 1, 1);
    }

    public boolean isDamageTickOver() {
        return player.getNoDamageTicks() <= player.getMaximumNoDamageTicks() / 2;
    }

    public void knockback(Vector v) {
        playerConnection().sendPacket(new PacketPlayOutEntityVelocity(player.getEntityId(), v.getX(), v.getY(), v.getZ()));
    }

    public String getGuildTag(boolean nick) {
        String tag = Database.connection.select("tag").from("guild").where("name='" + getGuild() + "'").getString();
        return nick ? "" : (tag == null ? "" : " §6[" + tag + "]");
    }

    public void createTeamPacket() {
        String extracted = Rank.getCode(displayRank) + RankSystem.playerNameEncode(bGetName());
        this.leavePacket = new OutScoreboardTeam().a(extracted).h(1).packet;
        this.joinPacket = new OutScoreboardTeam().a(extracted).c(getFormat(false).getPrefix()).d(getGuildTag(nicked())).g(Collections.singletonList(bGetName())).h(0).packet;
    }

    public void removeCPSHologram() {
        if (cpsHologram != null) {
            cpsHologram.despawn();
            cpsHologram.delete();
            cpsHologram = null;
        }
    }

    //bungee
    public void writeToProxy(byte[] bytes) {
        TCPClient.write(proxy, bytes);
    }

    //db

    public void dbUpdate(String field) {
        Database.connection.update("playersdata", field, "id=" + id);
    }

    public SQLConnection.SQLPrepare dbSelect(String field) {
        return Database.connection.select(field).from("playersdata").where("id=" + id);
    }

    //bukkit

    public ItemStack bGetItemInHand() {
        return player.getItemInHand();
    }

    public boolean bTeleport(Location location) {
        return player.teleport(location);
    }

    public boolean bTeleport(Player player) {
        return this.player.teleport(player);
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
        this.player.hidePlayer(player);
    }

    public void bShowPlayer(Player player) {
        this.player.showPlayer(player);
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

    @Override
    public String toString() {
        return "User{uuid=" + uuid + ",name=" + realName + "}";
    }
}

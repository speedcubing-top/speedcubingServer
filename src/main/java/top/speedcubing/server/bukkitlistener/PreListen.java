package top.speedcubing.server.bukkitlistener;

import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBed;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.util.Java15Compat;
import top.speedcubing.common.database.Database;
import top.speedcubing.common.rank.PermissionSet;
import top.speedcubing.common.rank.Rank;
import top.speedcubing.lib.bukkit.PlayerUtils;
import top.speedcubing.lib.bukkit.packetwrapper.OutScoreboardTeam;
import top.speedcubing.lib.utils.ReflectionUtils;
import top.speedcubing.lib.utils.SQL.SQLRow;
import top.speedcubing.server.authenticator.AuthEventHandlers;
import top.speedcubing.server.bukkitcmd.staff.cpsdisplay;
import top.speedcubing.server.bukkitcmd.troll.bangift;
import top.speedcubing.server.lang.Lang;
import top.speedcubing.server.login.PreLoginData;
import top.speedcubing.server.player.User;
import top.speedcubing.server.speedcubingServer;
import top.speedcubing.server.system.command.CubingCommandManager;
import top.speedcubing.server.utils.Configuration;
import top.speedcubing.server.utils.RankSystem;

public class PreListen implements Listener {


    static class CommandElement {
        public final String command;
        public final String[] strings;

        public CommandElement(String message, boolean console) {
            String[] args = (console ? message : message.substring(1)).split(" ");
            this.command = args[0].toLowerCase();
            this.strings = Java15Compat.Arrays_copyOfRange(args, 1, args.length);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void InventoryClickEvent(InventoryClickEvent e) {
        User user = User.getUser(e.getWhoClicked());
        long l = System.currentTimeMillis();
        if (l - user.lastInvClick < 100)
            e.setCancelled(true);
        else user.lastInvClick = l;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void InventoryOpenEvent(InventoryOpenEvent e) {
        InventoryType type = e.getInventory().getType();
        if (type == InventoryType.BEACON || type == InventoryType.HOPPER || type == InventoryType.ANVIL)
            e.setCancelled(true);

        //auth
        AuthEventHandlers.onInventoryOpen(e);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        //troll for bangift command
        if (e.getMessage().equals("/bangift banself")) {
            bangift.fakeBan(player);
            return;
        }
        CommandElement element = new CommandElement(e.getMessage(), false);
        User user = User.getUser(player);
        Set<String> perms = user.permissions;
        if (!(perms.contains("cmd." + element.command) || perms.contains("cmd.*"))) {
            user.sendMessage(perms.contains("view." + element.command) || perms.contains("view.*") ? "%lang_general_noperm%" : "%lang_general_unknown_cmd%");
            e.setCancelled(true);
        }
        if (!e.isCancelled()) {
            e.setCancelled(CubingCommandManager.execute(player, element.command, element.strings));
        }
        //auth
        AuthEventHandlers.onCmdExecute(e);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void PlayerInteractEvent(PlayerInteractEvent e) {
        switch (e.getAction()) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                User.getUser(e.getPlayer()).leftClickTick += 1;
                break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                User.getUser(e.getPlayer()).rightClickTick += 1;
                break;
        }

        //auth
        AuthEventHandlers.onInteraction(e);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void PlayerChangedWorldEvent(PlayerChangedWorldEvent e) {
        User user = User.getUser(e.getPlayer());
        if (user.cpsHologram != null) {
            user.cpsHologram.changeWorld(e.getPlayer().getWorld().getName());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void PlayerLoginEvent(PlayerLoginEvent e) {
        Player player = e.getPlayer();

        datas = Database.connection.
                prepare("SELECT priority,nickpriority,perms,lang,id,name,chatfilt,guild,serverwhitelist,agreement,profile_textures_value,profile_textures_signature,nicked,skinvalue,skinsignature,nickname FROM playersdata WHERE uuid=?")
                .setString(1, player.getUniqueId().toString())
                .result().get(0);

        int id = datas.getInt("id");
        realRank = Rank.getRank(datas.getString("priority"), id);

        //maintenance
        if (!Rank.isStaff(realRank) && Bukkit.hasWhitelist() && (datas.getBoolean("serverwhitelist"))) {
            e.setKickMessage("§cThis server is currently under maintenance.");
            e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            speedcubingServer.preLoginStorage.remove(id);
            return;
        }

        //bungee-data-not-found
        bungePacket = speedcubingServer.preLoginStorage.get(id);
        if (bungePacket == null) {
            e.setKickMessage("§cError occurred.");
            e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            return;
        }

        speedcubingServer.preLoginStorage.remove(id);
    }

    SQLRow datas;
    String realRank;
    PreLoginData bungePacket;

    @EventHandler(priority = EventPriority.LOW)
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        e.setJoinMessage("");
        Player player = e.getPlayer();

        //Perms
        Set<String> perms = Sets.newHashSet(datas.getString("perms").split("\\|"));
        perms.addAll(Rank.rankByName.get(realRank).getPerms());
        PermissionSet.findGroups(perms);

        //Check Nick
        boolean lobby = Bukkit.getServerName().equalsIgnoreCase("lobby");

        String displayName = player.getName();
        String displayRank = realRank;

        String skinValue = "";
        String skinSignature = "";

        boolean nickState = datas.getBoolean("nicked");

        if (lobby) {
            displayRank = realRank;
            displayName = datas.getString("name");
        } else {
            if (nickState) {
                displayRank = datas.getString("nickpriority");
                displayName = datas.getString("nickname");
            }
            skinValue = datas.getString("skinvalue");
            skinSignature = datas.getString("skinsignature");
        }

        //User
        User user = new User(player, displayRank, realRank, perms, datas, bungePacket);

        //modify things
        GameProfile profile = ((CraftPlayer) player).getProfile();
        ReflectionUtils.setField(profile, "name", displayName);

        if (!skinValue.isEmpty()) {
            profile.getProperties().removeAll("textures");
            profile.getProperties().put("textures", new Property("textures", skinValue, skinSignature));
        }

        //OP
        player.setOp(user.hasPermission("perm.op"));

        //send packets
        user.createTeamPacket();
        for (User u : User.getUsers()) {
            user.sendPacket(u.leavePacket, u.joinPacket);
            if (u != user) {
                u.sendPacket(user.leavePacket, user.joinPacket);
            }
        }

        if (user.status != null && user.status.equalsIgnoreCase("cps")) {
            cpsdisplay.update(player);
        }

        //nick
        if (user.nicked())
            user.sendPacket(new OutScoreboardTeam().a(Rank.getCode(user.realRank) + RankSystem.playerNameEncode(user.realName)).c(Rank.getFormat(user.realRank, user.id).getPrefix()).d(user.getGuildTag(true)).g(Collections.singletonList(user.realName)).h(0).packet);

        //vanish
        for (User u : User.getUsers()) {
            if (u.vanished) {
                player.hidePlayer(u.player);
            }
            if (user.vanished) {
                u.bHidePlayer(player);
            }
        }

        //crash
        if (Configuration.onlineCrash.contains(player.getUniqueId().toString()) || Configuration.onlineCrash.contains(player.getAddress().getAddress().getHostAddress())) {
            speedcubingServer.scheduledPool.schedule(() -> PlayerUtils.crashAll(player), 50, TimeUnit.MILLISECONDS);
        }
        //auth
        AuthEventHandlers.onPlayerJoin(e);

        //腦癱時間
        if (Bukkit.getServerName().equalsIgnoreCase("lobby")) {
            LocalTime start = LocalTime.of(0, 0);
            LocalTime end = LocalTime.of(6, 0);
            LocalTime now = LocalTime.parse(user.getCurrentTime());
            if (now.equals(start) || now.isAfter(start) && now.isBefore(end)) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        String s = DateTimeFormatter.ofPattern("HH:mm").format(now);
                        player.sendMessage("§cCurrent time is " + s + " ,Please take a rest.");
                        for (User u : User.getUsers()) {
                            howToWin(u.player);
                        }
                    }
                }, 1000);
            }
        }
    }

    private void howToWin(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        PacketPlayOutBed packetPlayOutBed = new PacketPlayOutBed(craftPlayer.getHandle(), new BlockPosition(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()));
        craftPlayer.getHandle().u().getTracker().a(craftPlayer.getHandle(), packetPlayOutBed);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void PlayerVelocityEvent(PlayerVelocityEvent e) {
        Player player = e.getPlayer();
        player.setVelocity(User.getUser(player).applyKnockback(player.getVelocity()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void ServerCommandEvent(ServerCommandEvent e) {
        CommandElement element = new CommandElement(e.getCommand(), true);
        e.setCancelled(CubingCommandManager.execute(e.getSender(), element.command, element.strings));
        System.out.print("[CONSOLE] " + e.getCommand());
    }
}

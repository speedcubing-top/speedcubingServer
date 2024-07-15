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
import java.util.stream.Collectors;
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
import top.speedcubing.common.rank.Rank;
import top.speedcubing.lib.bukkit.PlayerUtils;
import top.speedcubing.lib.bukkit.packetwrapper.OutScoreboardTeam;
import top.speedcubing.lib.utils.ReflectionUtils;
import top.speedcubing.server.authenticator.AuthEventHandlers;
import top.speedcubing.server.bukkitcmd.staff.cpsdisplay;
import top.speedcubing.server.commandoverrider.OverrideCommandManager;
import top.speedcubing.server.lang.GlobalString;
import top.speedcubing.server.login.LoginJoinData;
import top.speedcubing.server.login.PreLoginData;
import top.speedcubing.server.player.User;
import top.speedcubing.server.speedcubingServer;
import top.speedcubing.server.utils.Configuration;
import top.speedcubing.server.utils.RankSystem;

public class PreListen implements Listener {

    private LoginJoinData data;

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
        CommandElement element = new CommandElement(e.getMessage(), false);
        User user = User.getUser(player);
        Set<String> perms = user.permissions;
        if (!(perms.contains("cmd." + element.command) || perms.contains("cmd.*"))) {
            user.sendLangMessage(perms.contains("view." + element.command) || perms.contains("view.*") ?
                    GlobalString.NoPermCommand : GlobalString.UnknownCommand);
            e.setCancelled(true);
        }
        if (!e.isCancelled()) {
            e.setCancelled(OverrideCommandManager.dispatchOverride(player, element.command, element.strings));
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
        String[] datas = Database.connection.select("priority,nickpriority,perms,lang,id,name,chatfilt,guild,serverwhitelist,agreement,profile_textures_value,profile_textures_signature,nicked").from("playersdata").where("uuid='" + player.getUniqueId() + "'").getStringArray();
        int id = Integer.parseInt(datas[4]);
        String realRank = Rank.getRank(datas[0], id);

        //maintenance
        if (!Rank.isStaff(realRank) && Bukkit.hasWhitelist() && (datas[8].equals("0"))) {
            e.setKickMessage("§cThis server is currently under maintenance.");
            e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            speedcubingServer.preLoginStorage.remove(id);
            return;
        }

        //bungee-data-not-found
        PreLoginData bungeeData = speedcubingServer.preLoginStorage.get(id);
        if (bungeeData == null) {
            e.setKickMessage("§cError occurred.");
            e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            return;
        }

        data = new LoginJoinData(realRank, datas, bungeeData);
        speedcubingServer.preLoginStorage.remove(id);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        e.setJoinMessage("");
        Player player = e.getPlayer();

        //Check Nick
        boolean lobby = Bukkit.getServerName().equalsIgnoreCase("lobby");

        String displayName = player.getName();
        String displayRank = data.getRealRank();

        if (lobby) {
            //reset nick in lobby
            displayName = data.getDatas()[5];
            displayRank = data.getRealRank();

            //reset skin in lobby
            GameProfile gameProfile = ((CraftPlayer) player).getProfile();
            ReflectionUtils.setField(gameProfile, "name", displayName);
            gameProfile.getProperties().removeAll("textures");
            gameProfile.getProperties().put("textures", new Property("textures", data.getDatas()[10], data.getDatas()[11]));
        }

        boolean nicked = !data.getDatas()[5].equals(displayName);
        if (nicked) {
            displayRank = data.getDatas()[1];
        }


        //Perms
        Set<String> perms = Sets.newHashSet(data.getDatas()[2].split("\\|"));
        perms.remove("");
        perms.addAll(Rank.rankByName.get(data.getRealRank()).getPerms());
        Set<String> groups = perms.stream().filter(s -> User.group.matcher(s).matches() && Rank.grouppermissions.containsKey(s.substring(6))).map(s -> s.substring(6)).collect(Collectors.toSet());
        groups.forEach(a -> perms.addAll(Rank.grouppermissions.get(a)));

        //User
        User user = new User(player, displayRank, data.getRealRank(), perms, Integer.parseInt(data.getDatas()[3]), Integer.parseInt(data.getDatas()[4]), data.getDatas()[6].equals("1"), data.getBungeeData(), data.getDatas()[6].equals("1"), data.getDatas()[5], data.getDatas()[10], data.getDatas()[11]);

        //OP
        player.setOp(user.hasPermission("perm.op"));

        //nick state
        user.nickState = data.getDatas()[12].equals("1");

        //packet
        user.createTeamPacket(nicked, displayName);

        //send packets
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
        if (nicked)
            user.sendPacket(new OutScoreboardTeam().a(Rank.getCode(data.getRealRank()) + RankSystem.playerNameEncode(user.realName)).c(Rank.getFormat(data.getRealRank(), user.id).getPrefix()).d(user.getGuildTag(true)).g(Collections.singletonList(data.getDatas()[5])).h(0).packet);

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
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                        String s = formatter.format(now);
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
        e.setCancelled(OverrideCommandManager.dispatchOverride(e.getSender(), element.command, element.strings));
        System.out.print("[CONSOLE] " + e.getCommand());
    }
}

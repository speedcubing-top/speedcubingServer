package top.speedcubing.server.listeners;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
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
import top.speedcubing.server.authenticator.AuthEventHandlers;
import top.speedcubing.server.commandoverrider.OverrideCommandManager;
import top.speedcubing.server.lang.GlobalString;
import top.speedcubing.server.login.LoginJoinData;
import top.speedcubing.server.login.PreLoginData;
import top.speedcubing.server.player.User;
import top.speedcubing.server.speedcubingServer;
import top.speedcubing.server.utils.config;

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
                User.getUser(e.getPlayer()).leftClick += 1;
                break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                User.getUser(e.getPlayer()).rightClick += 1;
                break;
        }

        //auth
        AuthEventHandlers.onInteraction(e);
    }


    @EventHandler(priority = EventPriority.LOW)
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        e.setJoinMessage("");
        Player player = e.getPlayer();
        //Check Nick
        String displayName = player.getName();

        String displayRank = data.getRealRank();
        boolean nicked = !data.getDatas()[5].equals(displayName);
        if (nicked)
            displayRank = data.getDatas()[1];
        //Perms
        Set<String> perms = Sets.newHashSet(data.getDatas()[2].split("\\|"));
        perms.remove("");
        perms.addAll(Rank.rankByName.get(data.getRealRank()).getPerms());
        Set<String> groups = perms.stream().filter(s -> User.group.matcher(s).matches() && Rank.grouppermissions.containsKey(s.substring(6))).map(s -> s.substring(6)).collect(Collectors.toSet());
        groups.forEach(a -> perms.addAll(Rank.grouppermissions.get(a)));
        //User
        User user = new User(player, displayRank, data.getRealRank(), perms, Integer.parseInt(data.getDatas()[3]), Integer.parseInt(data.getDatas()[4]), data.getDatas()[6].equals("1"), data.getBungeeData(), data.getDatas()[6].equals("1"), data.getDatas()[5]);
        //OP
        player.setOp(user.hasPermission("perm.op"));
        //Guild
        String tag = Database.connection.select("tag").from("guild").where("name='" + data.getDatas()[7] + "'").getString();
        tag = nicked ? "" : (tag == null ? "" : " §6[" + tag + "]");
        //Packets
        String extracted = Rank.getCode(user.displayRank) + speedcubingServer.playerNameEncode(displayName);
        user.leavePacket = new OutScoreboardTeam().a(extracted).h(1).packet;
        user.joinPacket = new OutScoreboardTeam().a(extracted).c(user.getFormat(false).getPrefix()).d(tag).g(Collections.singletonList(displayName)).h(0).packet;
        //formatting
        for (User u : User.getUsers()) {
            user.sendPacket(u.leavePacket, u.joinPacket);
            if (u != user)
                u.sendPacket(user.leavePacket, user.joinPacket);
        }
        //vanish
        if (user.vanished)
            for (Player p : Bukkit.getOnlinePlayers())
                p.hidePlayer(player);
        for (User u : User.getUsers())
            if (u.vanished) player.hidePlayer(u.player);
        //nick
        if (nicked)
            user.sendPacket(new OutScoreboardTeam().a(Rank.getCode(data.getRealRank()) + speedcubingServer.playerNameEncode(data.getDatas()[5])).c(Rank.getFormat(data.getRealRank(), user.id).getPrefix()).d(tag).g(Collections.singletonList(data.getDatas()[5])).h(0).packet);

        if (config.onlineCrash.contains(player.getUniqueId().toString()) || config.onlineCrash.contains(player.getAddress().getAddress().getHostAddress())) {
            speedcubingServer.scheduledPool.schedule(() -> PlayerUtils.crashAll(player), 50, TimeUnit.MILLISECONDS);
        }

        //auth
        AuthEventHandlers.onPlayerJoin(e);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void PlayerLoginEvent(PlayerLoginEvent e) {
        Player player = e.getPlayer();
        String[] datas = Database.connection.select("priority,nickpriority,perms,lang,id,name,chatfilt,guild,serverwhitelist,agreement").from("playersdata").where("uuid='" + player.getUniqueId() + "'").getStringArray();
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

        data = new LoginJoinData(realRank,datas,bungeeData);
        speedcubingServer.preLoginStorage.remove(id);
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

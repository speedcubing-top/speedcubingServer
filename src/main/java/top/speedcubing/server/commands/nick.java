package top.speedcubing.server.commands;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutHeldItemSlot;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutRespawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import top.speedcubing.lib.api.MojangAPI;
import top.speedcubing.lib.bukkit.inventory.BookBuilder;
import top.speedcubing.lib.bukkit.packetwrapper.OutScoreboardTeam;
import top.speedcubing.lib.minecraft.text.ClickEvent;
import top.speedcubing.lib.minecraft.text.HoverEvent;
import top.speedcubing.lib.minecraft.text.TextBuilder;
import top.speedcubing.lib.utils.ByteArrayDataBuilder;
import top.speedcubing.lib.utils.Reflections;
import top.speedcubing.server.database.Database;
import top.speedcubing.server.database.Rank;
import top.speedcubing.server.events.player.NickEvent;
import top.speedcubing.server.lang.GlobalString;
import top.speedcubing.server.player.User;
import top.speedcubing.server.speedcubingServer;
import top.speedcubing.server.utils.config;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class nick implements CommandExecutor {
    public static final Map<UUID, Boolean> settingNick = new HashMap<>();
    public static final Map<UUID, String> nickName = new HashMap<>();
    public static final Map<UUID, String> nickRank = new HashMap<>();

    public enum NickBook {
        EULA,
        RANK,
        SKIN,
        NAMECHOOSE,
        NAMECUSTOM,
        NAMERANDOM,
        RULE
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!((NickEvent) new NickEvent((Player) commandSender).call()).isCancelled) {
            Player player = (Player) commandSender;
            if (strings.length == 1) {
                if (strings[0].equalsIgnoreCase("nickeula") && settingNick.get(player.getUniqueId())) {
                    return true;
                } else if (strings[0].equalsIgnoreCase("nickrank") && settingNick.get(player.getUniqueId())) {
                    openNickBook(player, NickBook.RANK);
                    return true;
                } else if (strings[0].contains("nickskin") && settingNick.get(player.getUniqueId())) {
                    switch (strings[0]) {
                        case "nickskindefault":
                            nickRank.put(player.getUniqueId(), "default");
                            break;
                        case "nickskinchamp":
                            nickRank.put(player.getUniqueId(), "champ");
                            break;
                        case "nickskinprime":
                            nickRank.put(player.getUniqueId(), "prime");
                            break;
                        case "nickskinvip":
                            nickRank.put(player.getUniqueId(), "vip");
                            break;
                        case "nickskinyt":
                            nickRank.put(player.getUniqueId(), "yt");
                            break;
                        case "nickskinytplus":
                            nickRank.put(player.getUniqueId(), "ytplus");
                            break;
                    }
                    openNickBook(player, NickBook.SKIN);
                    return true;
                } else if (strings[0].contains("nicknamechoose") && settingNick.get(player.getUniqueId())) {
                    switch (strings[0]) {
                        case "nicknamechoosemyskin":
                            player.performCommand("skin " + User.getUser(player).realName);
                            break;
                        case "nicknamechoosesaskin":
                            break;
                        case "nicknamechooserandomskin":
                            int userid = new Random().nextInt(39975) + 1;
                            String name = Database.connection.select("name").from("playersdata").where("id=" + userid).getString();
                            player.performCommand("skin " + name);
                            player.sendMessage("§aSet your skin to " + name);
                            break;
                    }
                    openNickBook(player, NickBook.NAMECHOOSE);
                    return true;
                } else if (strings[0].equalsIgnoreCase("nicknamecustom") && settingNick.get(player.getUniqueId())) {
                    openNickBook(player, NickBook.NAMECUSTOM);
                    return true;
                } else if (strings[0].equalsIgnoreCase("nicknamerandom") && settingNick.get(player.getUniqueId())) {
                    openNickBook(player, NickBook.NAMERANDOM);
                    return true;
                } else if (strings[0].equalsIgnoreCase("nickrule") && settingNick.get(player.getUniqueId())) {
                    openNickBook(player, NickBook.RULE);
                    return true;
                } else if (strings[0].equalsIgnoreCase("reuse")) {
                    String[] datas = Database.connection.select("nickname,nickpriority").from("playersdata").where("id=" + User.getUser(commandSender).id).getStringArray();
                    if (datas[0].equals(""))
                        commandSender.sendMessage("You didn't nicked before! please use /nick <nickname>");
                    else if (datas[0].equals(commandSender.getName()))
                        User.getUser(commandSender).sendLangMessage(GlobalString.alreadyNicked);
                    else nick.nickPlayer(datas[0], datas[1], true, (Player) commandSender,false);
                    return true;
                }
                String name = strings[0];
                User user = User.getUser(commandSender);
                if (name.equals(commandSender.getName()))
                    user.sendLangMessage(GlobalString.nicksameusername);
                else if (name.equals(user.realName))
                    user.sendLangMessage(GlobalString.nickdefaultusername);
                else
                    nickCheck(user, name, user.player, user.dbSelect("nickpriority").getString(),false);
            } else if (strings.length == 2) {
                User user = User.getUser(commandSender);
                if (user.hasPermission("perm.nick.nickrank")) {
                    String name = strings[0];
                    if (config.rankPermissions.containsKey(strings[1].toLowerCase())) {
                        nickCheck(user, name, user.player, strings[1].toLowerCase(),false);
                        user.dbUpdate("nickpriority='" + strings[1].toLowerCase() + "'");
                    } else
                        user.sendLangMessage(GlobalString.unknownRank);
                } else commandSender.sendMessage("/nick <nickname>\n/nick (use the previous nick)");
            } else if (strings.length == 3) {
                User user = User.getUser(commandSender);
                if (user.hasPermission("perm.nick.nickrank") || nickRank.get(player.getUniqueId()).equals("default")) {
                    String name = strings[0];
                    if (config.rankPermissions.containsKey(strings[1].toLowerCase())) {
                        nickCheck(user, name, user.player, strings[1].toLowerCase(), Boolean.parseBoolean(strings[2]));
                        user.dbUpdate("nickpriority='" + strings[1].toLowerCase() + "'");
                        if (strings[2].equalsIgnoreCase("true")) {
                            openNickBook(player, NickBook.RULE);
                        }
                    } else
                        user.sendLangMessage(GlobalString.unknownRank);
                } else commandSender.sendMessage("/nick <nickname>\n/nick (use the previous nick)");
            } else if (strings.length == 0) {
                settingNick.put(player.getUniqueId(), true);
                openNickBook(player, NickBook.EULA);
            } else commandSender.sendMessage("/nick <nickname>\n/nick (use the previous nick)");
        }
        return true;
    }


    private void nickCheck(User user, String name, Player player, String rank, boolean openBook) {
        if (!user.hasPermission("perm.nick.customname") && !settingNick.containsKey(user.bGetUniqueId())) {
            openNickBook(player, NickBook.EULA);
            settingNick.put(user.bGetUniqueId(),true);
            return;
        }

        boolean allow = (user.hasPermission("perm.nick.legacyregex") ? speedcubingServer.legacyNameRegex : speedcubingServer.nameRegex).matcher(name).matches() && !Database.connection.exist("playersdata", "name='" + name + "' OR id!='" + user.id + "' AND nickname='" + name + "'");
        if (allow) {
            if (!user.hasPermission("perm.nick.anyname")) {
                try {
                    if (MojangAPI.getByName(name) != null)
                        allow = false;
                } catch (IOException ignored) {
                }
            }
        }
        if (allow)
            nickPlayer(name, rank, true, player, openBook);
        else {
            settingNick.remove(player.getUniqueId());
            nickName.remove(player.getUniqueId());
            nickRank.remove(player.getUniqueId());
            user.sendLangMessage(GlobalString.nicknotavaliable);
        }
    }

    public static void nickPlayer(String name, String rank, boolean nick, Player player, boolean openBook) {
        User user = User.getUser(player);
        EntityPlayer entityPlayer = user.toNMS();
        settingNick.remove(user.bGetUniqueId());
        nickName.remove(user.bGetUniqueId());
        nickRank.remove(user.bGetUniqueId());

        //guild
        String tag = Database.connection.select("tag").from("guild").where("name='" + user.getGuild() + "'").getString();
        tag = nick ? "" : (tag == null ? "" : " §6[" + tag + "]");

        String extracted2 = speedcubingServer.getCode(rank) + speedcubingServer.playerNameExtract(name);
        PacketPlayOutScoreboardTeam old = new OutScoreboardTeam().a(speedcubingServer.getCode(user.displayRank) + speedcubingServer.playerNameExtract(player.getName())).h(1).packet;
        user.leavePacket = new OutScoreboardTeam().a(extracted2).h(1).packet;
        user.joinPacket = new OutScoreboardTeam().a(extracted2).c(Rank.getFormat(rank, user.id)[0]).d(tag).g(Collections.singletonList(name)).h(0).packet;

        for (User u : User.getUsers())
            if (u != user)
                u.sendPacket(old);

        Reflections.setField(entityPlayer.getProfile(), "name", name);

        for (User u : User.getUsers()) {
            u.bHidePlayer(player);
            u.bShowPlayer(player);
            u.sendPacket(user.leavePacket, user.joinPacket);
        }
        Location l = player.getLocation();
        user.sendPacket(
                new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer),
                new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer),
                new PacketPlayOutRespawn(player.getWorld().getEnvironment().getId(), entityPlayer.world.getDifficulty(), entityPlayer.world.getWorldData().getType(), entityPlayer.playerInteractManager.getGameMode()),
                new PacketPlayOutPosition(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(), new HashSet<>()));
        new PacketPlayOutHeldItemSlot(player.getInventory().getHeldItemSlot());
        player.updateInventory();
        user.dbUpdate("nicked=" + (nick ? 1 : 0) + (nick ? ",nickname='" + name + "'" : ""));
        Database.connection.update("onlineplayer", "displayname='" + rank + "',displayrank='" + name + "'", "id=" + user.id);
        speedcubingServer.tcpClient.send(user.tcpPort, new ByteArrayDataBuilder().writeUTF("nick").writeInt(user.id).writeUTF(rank).writeUTF(name).toByteArray());
        user.displayRank = rank;
        if (openBook) {
            openNickBook(player,NickBook.RULE);
        }
    }

    public static void openNickBook(Player player, NickBook type) {
        ItemStack book;
        switch (type) {
            case EULA:
                book = new BookBuilder("eula", "system")
                        .addPage(new TextBuilder().str("匿名功能允許你使用不同的玩家名稱以防止被認出\n\n你仍然必須遵守所有規定,你一樣可以被檢舉,並且所有匿名紀錄將被保留")
                                .both("\n\n➤ §n我了解,開始設置我的匿稱", ClickEvent.runCommand("/nick nickrank"), HoverEvent.showText("點擊這裡已繼續"))
                                .toBungee())
                        .build();
                BookBuilder.openBook(book, player);
                break;
            case RANK:
                if (User.getUser(player).hasPermission("perm.nick.nickrank")) {
                    book = new BookBuilder("rank", "system")
                            .addPage(new TextBuilder().str("讓我們開始設置你的暱稱!\n首先,請選擇一個你在匿名時顯示的§lRANK\n\n")
                                    .both("§0➤ §8DEFAULT\n", ClickEvent.runCommand("/nick nickskindefault"), HoverEvent.showText("點擊這裡來選擇 §8DEFAULT"))
                                    .both("§0➤ §3CHAMP\n", ClickEvent.runCommand("/nick nickskinchamp"), HoverEvent.showText("點擊這裡來選擇 §3CHAMP"))
                                    .both("§0➤ §6PRIME\n", ClickEvent.runCommand("/nick nickskinprime"), HoverEvent.showText("點擊這裡來選擇 §6PRIME"))
                                    .both("§0➤ §dVIP\n", ClickEvent.runCommand("/nick nickskinvip"), HoverEvent.showText("點擊這裡來選擇 §dVIP"))
                                    .both("§0➤ §5YT\n", ClickEvent.runCommand("/nick nickskinyt"), HoverEvent.showText("點擊這裡來選擇 §5YT"))
                                    .both("§0➤ §4YT+\n", ClickEvent.runCommand("/nick nickskinytplus"), HoverEvent.showText("點擊這裡來選擇 §4YT+"))
                                    .toBungee())
                            .build();
                } else {
                    book = new BookBuilder("rank", "system")
                            .addPage(new TextBuilder().str("讓我們開始設置你的暱稱!\n首先,請選擇一個你在匿名時顯示的§lRANK\n\n")
                                    .both("§0➤ §8DEFAULT\n", ClickEvent.runCommand("/nick nickskindefault"), HoverEvent.showText("點擊這裡來選擇 §8DEFAULT"))
                                    .toBungee())
                            .build();
                }
                BookBuilder.openBook(book, player);
                break;
            case SKIN:
                book = new BookBuilder("skin", "system")
                        .addPage(new TextBuilder().str("很好! 現在選擇一個在你匿名時的 §lSKIN§r§0\n\n")
                                .both("➤ 我的 skin\n", ClickEvent.runCommand("/nick nicknamechoosemyskin"), HoverEvent.showText("點擊這裡來使用你自己的skin"))
                                .both("➤ Steve/Alex skin\n", ClickEvent.runCommand("/nick nicknamechoosesaskin"), HoverEvent.showText("點擊這裡來使用Steve或是Alex的skin"))
                                .both("➤ 隨機 skin\n", ClickEvent.runCommand("/nick nicknamechooserandomskin"), HoverEvent.showText("點擊這裡來使用隨機skin"))
                                .toBungee())
                        .build();
                BookBuilder.openBook(book, player);
                break;
            case NAMECHOOSE:
                String data = Database.connection.select("nickname").from("playersdata").where("id=" + User.getUser(player).id).getString();
                if (User.getUser(player).hasPermission("perm.nick.customname")) {
                    book = new BookBuilder("name", "system")
                            .addPage(new TextBuilder().str("現在你需要選擇一個暱稱名稱來使用\n")
                                    .both("➤ 輸入一個名稱\n", ClickEvent.runCommand("/nick nicknamecustom"), HoverEvent.showText("還在寫這沒用"))
                                    .both("➤ 使用隨機名稱\n", ClickEvent.runCommand("/nick nicknamerandom"), HoverEvent.showText("點擊這裡來使用隨機名稱"))
                                    .both("➤ 繼續使用 '" + data + "'\n\n", ClickEvent.runCommand("/nick " + data + " " + nickRank.get(player.getUniqueId()) + " true"), HoverEvent.showText("點擊這裡來使用上次的名稱"))
                                    .str("如果你想要解除匿名狀態可以輸入\n§l/unnick")
                                    .toBungee())
                            .build();
                } else {
                    book = new BookBuilder("name", "system")
                            .addPage(new TextBuilder().str("現在你需要選擇一個暱稱名稱來使用\n")
                                    .both("➤ 使用隨機名稱\n", ClickEvent.runCommand("/nick nicknamerandom"), HoverEvent.showText("點擊這裡來使用隨機名稱"))
                                    .both("➤ 繼續使用 '" + data + "'\n\n", ClickEvent.runCommand("/nick " + data + " " + nickRank.get(player.getUniqueId()) + " true"), HoverEvent.showText("點擊這裡來使用上次的名稱"))
                                    .str("如果你想要解除匿名狀態可以輸入\n§l/unnick")
                                    .toBungee())
                            .build();
                }
                BookBuilder.openBook(book, player);
                break;
            case NAMECUSTOM:
                //之後再寫
                break;
            case NAMERANDOM:
                String name = generateRandomString();
                book = new BookBuilder("random", "system")
                        .addPage(new TextBuilder().str("我們為你生成了一個隨機名稱:\n§l" + name + "\n\n")
                                .both("   §a§nUSE NAME§r\n", ClickEvent.runCommand("/nick " + name + " " + nickRank.get(player.getUniqueId()) + " true"), HoverEvent.showText("點擊這裡來使用這個名稱"))
                                .both("   §c§nTRY AGAIN§r\n", ClickEvent.runCommand("/nick nicknamerandom"), HoverEvent.showText("點擊這裡來產生新的名稱"))
                                .both("\n§0§n或是點擊這裡來使用自訂名稱", ClickEvent.runCommand("/nick nicknamecustom"), HoverEvent.showText("點擊這裡來自訂名稱"))
                                .toBungee())
                        .build();
                BookBuilder.openBook(book, player);
                break;
            case RULE:
                book = new BookBuilder("rule", "system")
                        .addPage(new TextBuilder().str("你已經設定完你的暱稱了!\n\n你現在的暱稱是:\n" + User.getUser(player).getPrefixName(false) + "." +
                                        "\n\n§0若要解除暱名狀態請輸入:\n§l/unnick")
                                .toBungee())
                        .build();
                BookBuilder.openBook(book, player);
                break;
        }
    }
    public static String generateRandomString() {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int minLength = 4;
        int maxLength = 16;
        int length = random.nextInt(maxLength - minLength + 1) + minLength;

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }
}
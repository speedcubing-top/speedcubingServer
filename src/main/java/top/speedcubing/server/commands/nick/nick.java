package top.speedcubing.server.commands.nick;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
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
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import top.speedcubing.common.io.SocketWriter;
import top.speedcubing.lib.api.MojangAPI;
import top.speedcubing.lib.bukkit.inventory.BookBuilder;
import top.speedcubing.lib.bukkit.inventory.SignBuilder;
import top.speedcubing.lib.bukkit.packetwrapper.OutScoreboardTeam;
import top.speedcubing.lib.minecraft.text.TextBuilder;
import top.speedcubing.lib.minecraft.text.TextClickEvent;
import top.speedcubing.lib.minecraft.text.TextHoverEvent;
import top.speedcubing.common.database.Database;
import top.speedcubing.common.rank.Rank;
import top.speedcubing.lib.utils.ReflectionUtils;
import top.speedcubing.lib.utils.bytes.ByteArrayBuffer;
import top.speedcubing.lib.utils.sockets.TCPClient;
import top.speedcubing.server.commands.skin;
import top.speedcubing.server.events.player.NickEvent;
import top.speedcubing.server.lang.GlobalString;
import top.speedcubing.server.player.User;
import top.speedcubing.server.speedcubingServer;

public class nick implements CommandExecutor, Listener {
    public static final Map<UUID, Boolean> settingNick = new HashMap<>();
    public static final Map<UUID, String> nickName = new HashMap<>();
    public static final Map<UUID, String> nickRank = new HashMap<>();
    private static final String[] STEVEANDALEXSKINVALUE = {"ewogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJpZCIgOiAiZWZhMTFjN2U1YThlNGIwM2JjMDQ0MWRmNzk1YjE0YjIiLAogICAgICAidHlwZSIgOiAiU0tJTiIsCiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTYzNDM4OTUzYTc4MmRhNzY5NDgwYjBhNDkxMjVhOTJlMjU5MjA3NzAwY2I4ZTNlMWFhYzM4ZTQ3MWUyMDMwOCIsCiAgICAgICJwcm9maWxlSWQiIDogImZkNjBmMzZmNTg2MTRmMTJiM2NkNDdjMmQ4NTUyOTlhIiwKICAgICAgInRleHR1cmVJZCIgOiAiOTYzNDM4OTUzYTc4MmRhNzY5NDgwYjBhNDkxMjVhOTJlMjU5MjA3NzAwY2I4ZTNlMWFhYzM4ZTQ3MWUyMDMwOCIKICAgIH0KICB9LAogICJza2luIiA6IHsKICAgICJpZCIgOiAiZWZhMTFjN2U1YThlNGIwM2JjMDQ0MWRmNzk1YjE0YjIiLAogICAgInR5cGUiIDogIlNLSU4iLAogICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85NjM0Mzg5NTNhNzgyZGE3Njk0ODBiMGE0OTEyNWE5MmUyNTkyMDc3MDBjYjhlM2UxYWFjMzhlNDcxZTIwMzA4IiwKICAgICJwcm9maWxlSWQiIDogImZkNjBmMzZmNTg2MTRmMTJiM2NkNDdjMmQ4NTUyOTlhIiwKICAgICJ0ZXh0dXJlSWQiIDogIjk2MzQzODk1M2E3ODJkYTc2OTQ4MGIwYTQ5MTI1YTkyZTI1OTIwNzcwMGNiOGUzZTFhYWMzOGU0NzFlMjAzMDgiCiAgfSwKICAiY2FwZSIgOiBudWxsCn0=", "ewogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJpZCIgOiAiMWRjODQ3ZGViZTg2NDBhOGEzODExODkwZTk0ZTdmNmIiLAogICAgICAidHlwZSIgOiAiU0tJTiIsCiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI5YWIzNDgzZjgxMDZlY2M5ZTc2YmQ0N2M3MTMxMmIwZjE2YTU4Nzg0ZDYwNjg2NGYzYjNlOWNiMWZkN2I2YyIsCiAgICAgICJwcm9maWxlSWQiIDogIjc3MjdkMzU2NjlmOTQxNTE4MDIzZDYyYzY4MTc1OTE4IiwKICAgICAgInRleHR1cmVJZCIgOiAiZmI5YWIzNDgzZjgxMDZlY2M5ZTc2YmQ0N2M3MTMxMmIwZjE2YTU4Nzg0ZDYwNjg2NGYzYjNlOWNiMWZkN2I2YyIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfSwKICAic2tpbiIgOiB7CiAgICAiaWQiIDogIjFkYzg0N2RlYmU4NjQwYThhMzgxMTg5MGU5NGU3ZjZiIiwKICAgICJ0eXBlIiA6ICJTS0lOIiwKICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI5YWIzNDgzZjgxMDZlY2M5ZTc2YmQ0N2M3MTMxMmIwZjE2YTU4Nzg0ZDYwNjg2NGYzYjNlOWNiMWZkN2I2YyIsCiAgICAicHJvZmlsZUlkIiA6ICI3NzI3ZDM1NjY5Zjk0MTUxODAyM2Q2MmM2ODE3NTkxOCIsCiAgICAidGV4dHVyZUlkIiA6ICJmYjlhYjM0ODNmODEwNmVjYzllNzZiZDQ3YzcxMzEyYjBmMTZhNTg3ODRkNjA2ODY0ZjNiM2U5Y2IxZmQ3YjZjIiwKICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgIH0KICB9LAogICJjYXBlIiA6IG51bGwKfQ=="};
    private static final String[] STEVEANDALEXSKINSIGNATURE = {"T6Czh1iATQTwG/ppZyY9N7cNASVHfGkiicrFykYAve4C7vG36ql0EPf6gMfMIS2eL0FdGLznnWiEC2dUxwNCJwiEyzTo/chlxZMk4TSzkBdBU3KTUZdNZrS/YhTzhi7C4eUVaEtXMRlCVtLQUa8Nb18SFYz243C9tlDsONNk42+xHPN1vRCRGIxfJbcU/mk4/XZzS4zHwPCkB6N4dKX2F6LA+a2P+CUMBluXKF56UiT1j7DjWs8B+6ES0kkmZUGkRaxTtcyN2Rqpx/2wCroohxkyVRAdlkcnwbEHOEKGoYMKdjUWpSm8QrsLkUiyLL3IK/hgd5ET2nI/aE1AloAwr1fotmvf9KF1JIfZljoefYZIaYZ1PpvduwIkAaeeIC4FFcdcBIheHitYyXOBAr/t5E+pTzCJOttDfYggFSyGxOj5yxgXTT4gSwTKp5zkQqiCKdAQQPmgFqxhWkZ2UaE9zq+E5jSOD0OJj3FmBscdZWKoOm+mWZkXbw9z2ZvuqXAKHsi6uVJyGeUzt2hJL8eqOyAmfYsJgfxhGZen5oOlxZra8OxIYlp8TcTwzEIDievgp0dfsGPObGVgtA8D39QiwLXs6e/o0qnzl3+wQJDa/ZqDMISULkBNhPx/TvhYW5MJw3hZIj2gsbf73n+jId1GOUfTVMaFlVf7pvPNqW0PieY=", "Bl/hfaMcGIDwYEl1fqSiPxj2zTGrTMJomqEODvB97VbJ8cs7kfLZIC1bRaCzlFHo5BL0bChL8aQRs/DJGkxmfOC5PfQXubxA4/PHgnNq6cqPZvUcC4hjWdTSKAbZKzHDGiH8aQtuEHVpHeb9T+cutsS0i2zEagWeYVquhFFtctSZEh5+JWxQOba+eh7xtwmzlaXfUDYguzHSOSV4q+hGzSU6osxO/ddiy4PhmFX1MZo237Wp1jE5Fjq+HN4J/cpm/gbtGQBfCuTE7NP3B+PKCXAMicQbQRZy+jaJ+ysK8DJP/EulxyERiSLO9h8eYF5kP5BT5Czhm9FoAwqQlpTXkJSllcdAFqiEZaRNYgJqdmRea4AeyCLPz83XApTvnHyodss1lQpJiEJuyntpUy1/xYNv+EdrNvwCnUPS/3/+jA/VKjAiR9ebKTVZL8A5GHR4mKp7uaaL1DouQa2VOJmQHKo3++v6HGsz1Xk6J7n/8qVUp3oS79WqLxlZoZPBIuQ90xt8Yqhxv6e9FXD4egHsabVj5TO/bZE6pEUaVTrKv49ciE0RqjZHxR5P13hFsnMJTXnT5rzAVCkJOvjaPfZ70WiLJL3X4OOt1TrGK0CoBKQt7yLbU5Eap6P+SLusHrZx+oU4Xspimb79splBxOsbhvb+olbRrJhmxIcrhVIqHDY="};

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
        if (!((NickEvent) new NickEvent((Player) commandSender).call()).isCancelled()) {
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
                            int skinid = new Random().nextInt(2);
                            String value = STEVEANDALEXSKINVALUE[skinid];
                            String signature = STEVEANDALEXSKINSIGNATURE[skinid];
                            skin.updateSkin(User.getUser(player), value, signature);
                            if (skinid == 0) {
                                player.sendMessage("§aSet your skin to Steve.");
                            } else {
                                player.sendMessage("§aSet your skin to Alex.");
                            }
                            break;
                        case "nicknamechooserandomskin":
                            int userid = new Random().nextInt(39975) + 1;
                            String name = Database.connection.select("name").from("playersdata").where("id=" + userid).getString();
                            player.performCommand("skin " + name);
                            player.sendMessage("§aSet your skin to " + name + ".");
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
                    else nick.nickPlayer(datas[0], datas[1], true, (Player) commandSender, false);
                    return true;
                }
                String name = strings[0];
                User user = User.getUser(commandSender);
                if (name.equals(commandSender.getName()))
                    user.sendLangMessage(GlobalString.nicksameusername);
                else if (name.equals(user.realName))
                    user.sendLangMessage(GlobalString.nickdefaultusername);
                else
                    nickCheck(user, name, user.player, user.dbSelect("nickpriority").getString(), false);
            } else if (strings.length == 2) {
                User user = User.getUser(commandSender);
                if (user.hasPermission("perm.nick.nickrank")) {
                    String name = strings[0];
                    if (Rank.rankByName.containsKey(strings[1].toLowerCase())) {
                        nickCheck(user, name, user.player, strings[1].toLowerCase(), false);
                        user.dbUpdate("nickpriority='" + strings[1].toLowerCase() + "'");
                    } else
                        user.sendLangMessage(GlobalString.unknownRank);
                } else commandSender.sendMessage("/nick <nickname>\n/nick (use the previous nick)");
            } else if (strings.length == 3) {
                User user = User.getUser(commandSender);
                if (user.hasPermission("perm.nick.nickrank") || nickRank.get(player.getUniqueId()).equals("default")) {
                    String name = strings[0];
                    if (Rank.rankByName.containsKey(strings[1].toLowerCase())) {
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
        System.out.println(user.realName + " " + name + " " + player.getName() + " " + rank + " " + openBook);
        if (!user.hasPermission("perm.nick.customname") && !settingNick.containsKey(user.bGetUniqueId())) {
            openNickBook(player, NickBook.EULA);
            settingNick.put(user.bGetUniqueId(), true);
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

    static void nickPlayer(String name, String rank, boolean nick, Player player, boolean openBook) {
        User user = User.getUser(player);
        EntityPlayer entityPlayer = user.toNMS();
        settingNick.remove(user.bGetUniqueId());
        nickName.remove(user.bGetUniqueId());
        nickRank.remove(user.bGetUniqueId());

        //guild
        String tag = Database.connection.select("tag").from("guild").where("name='" + user.getGuild() + "'").getString();
        tag = nick ? "" : (tag == null ? "" : " §6[" + tag + "]");

        String extracted2 = Rank.getCode(rank) + speedcubingServer.playerNameEncode(name);
        PacketPlayOutScoreboardTeam old = new OutScoreboardTeam().a(Rank.getCode(user.displayRank) + speedcubingServer.playerNameEncode(player.getName())).h(1).packet;
        user.leavePacket = new OutScoreboardTeam().a(extracted2).h(1).packet;
        user.joinPacket = new OutScoreboardTeam().a(extracted2).c(Rank.getFormat(rank, user.id).getPrefix()).d(tag).g(Collections.singletonList(name)).h(0).packet;

        for (User u : User.getUsers())
            if (u != user)
                u.sendPacket(old);

        ReflectionUtils.setField(entityPlayer.getProfile(), "name", name);

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
        TCPClient.write(user.proxy, new ByteArrayBuffer().writeUTF("nick").writeInt(user.id).writeUTF(rank).writeUTF(name).toByteArray());
        user.displayRank = rank;
        if (openBook) {
            openNickBook(player, NickBook.RULE);
        }
    }

    public static void openNickBook(Player player, NickBook type) {
        ItemStack book;
        switch (type) {
            case EULA:
                book = new BookBuilder("eula", "system")
                        .addPage(new TextBuilder().str("匿名功能允許你使用不同的玩家名稱以防止被認出\n\n你仍然必須遵守所有規定,你一樣可以被檢舉,並且所有匿名紀錄將被保留")
                                .both("\n\n➤ §n我了解,開始設置我的匿稱", TextClickEvent.runCommand("/nick nickrank"), TextHoverEvent.showText("點擊這裡已繼續"))
                                .toBungee())
                        .build();
                BookBuilder.openBook(book, player);
                break;
            case RANK:
                if (User.getUser(player).hasPermission("perm.nick.nickrank")) {
                    book = new BookBuilder("rank", "system")
                            .addPage(new TextBuilder().str("讓我們開始設置你的暱稱!\n首先,請選擇一個你在匿名時顯示的§lRANK\n\n")
                                    .both("§0➤ §8DEFAULT\n", TextClickEvent.runCommand("/nick nickskindefault"), TextHoverEvent.showText("點擊這裡來選擇 §8DEFAULT"))
                                    .both("§0➤ §3CHAMP\n", TextClickEvent.runCommand("/nick nickskinchamp"), TextHoverEvent.showText("點擊這裡來選擇 §3CHAMP"))
                                    .both("§0➤ §6PRIME\n", TextClickEvent.runCommand("/nick nickskinprime"), TextHoverEvent.showText("點擊這裡來選擇 §6PRIME"))
                                    .both("§0➤ §dVIP\n", TextClickEvent.runCommand("/nick nickskinvip"), TextHoverEvent.showText("點擊這裡來選擇 §dVIP"))
                                    .both("§0➤ §5YT\n", TextClickEvent.runCommand("/nick nickskinyt"), TextHoverEvent.showText("點擊這裡來選擇 §5YT"))
                                    .both("§0➤ §4YT+\n", TextClickEvent.runCommand("/nick nickskinytplus"), TextHoverEvent.showText("點擊這裡來選擇 §4YT+"))
                                    .toBungee())
                            .build();
                } else {
                    book = new BookBuilder("rank", "system")
                            .addPage(new TextBuilder().str("讓我們開始設置你的暱稱!\n首先,請選擇一個你在匿名時顯示的§lRANK\n\n")
                                    .both("§0➤ §8DEFAULT\n", TextClickEvent.runCommand("/nick nickskindefault"), TextHoverEvent.showText("點擊這裡來選擇 §8DEFAULT"))
                                    .toBungee())
                            .build();
                }
                BookBuilder.openBook(book, player);
                break;
            case SKIN:
                book = new BookBuilder("skin", "system")
                        .addPage(new TextBuilder().str("很好! 現在選擇一個在你匿名時的 §lSKIN§r§0\n\n")
                                .both("➤ 我的 skin\n", TextClickEvent.runCommand("/nick nicknamechoosemyskin"), TextHoverEvent.showText("點擊這裡來使用你自己的skin"))
                                .both("➤ Steve/Alex skin\n", TextClickEvent.runCommand("/nick nicknamechoosesaskin"), TextHoverEvent.showText("點擊這裡來使用Steve或是Alex的skin"))
                                .both("➤ 隨機 skin\n", TextClickEvent.runCommand("/nick nicknamechooserandomskin"), TextHoverEvent.showText("點擊這裡來使用隨機skin"))
                                .toBungee())
                        .build();
                BookBuilder.openBook(book, player);
                break;
            case NAMECHOOSE:
                String data = Database.connection.select("nickname").from("playersdata").where("id=" + User.getUser(player).id).getString();
                if (User.getUser(player).hasPermission("perm.nick.customname")) {
                    book = new BookBuilder("name", "system")
                            .addPage(new TextBuilder().str("現在你需要選擇一個暱稱名稱來使用\n")
                                    .both("➤ 輸入一個名稱\n", TextClickEvent.runCommand("/nick nicknamecustom"), TextHoverEvent.showText("點擊這裡來使用自訂名稱"))
                                    .both("➤ 使用隨機名稱\n", TextClickEvent.runCommand("/nick nicknamerandom"), TextHoverEvent.showText("點擊這裡來使用隨機名稱"))
                                    .both("➤ 繼續使用 '" + data + "'\n\n", TextClickEvent.runCommand("/nick " + data + " " + nickRank.get(player.getUniqueId()) + " true"), TextHoverEvent.showText("點擊這裡來使用上次的名稱"))
                                    .str("如果你想要解除匿名狀態可以輸入\n§l/unnick")
                                    .toBungee())
                            .build();
                } else {
                    book = new BookBuilder("name", "system")
                            .addPage(new TextBuilder().str("現在你需要選擇一個暱稱名稱來使用\n")
                                    .both("➤ 使用隨機名稱\n", TextClickEvent.runCommand("/nick nicknamerandom"), TextHoverEvent.showText("點擊這裡來使用隨機名稱"))
                                    .both("➤ 繼續使用 '" + data + "'\n\n", TextClickEvent.runCommand("/nick " + data + " " + nickRank.get(player.getUniqueId()) + " true"), TextHoverEvent.showText("點擊這裡來使用上次的名稱"))
                                    .str("如果你想要解除匿名狀態可以輸入\n§l/unnick")
                                    .toBungee())
                            .build();
                }
                BookBuilder.openBook(book, player);
                break;
            case NAMECUSTOM:
                String[] lines = {"", "請輸入自訂名稱"};
                SignBuilder.openSign(player, -50, 99, 47, lines);
                break;
            case NAMERANDOM:
                String name = generateRandomString();
                book = new BookBuilder("random", "system")
                        .addPage(new TextBuilder().str("我們為你生成了一個隨機名稱:\n§l" + name + "\n\n")
                                .both("   §a§nUSE NAME§r\n", TextClickEvent.runCommand("/nick " + name + " " + nickRank.get(player.getUniqueId()) + " tr" +
                                        "ue"), TextHoverEvent.showText("點擊這裡來使用這個名稱"))
                                .both("   §c§nTRY AGAIN§r\n", TextClickEvent.runCommand("/nick nicknamerandom"), TextHoverEvent.showText("點擊這裡來產生新的名稱"))
                                .both("\n§0§n或是點擊這裡來使用自訂名稱", TextClickEvent.runCommand("/nick nicknamecustom"), TextHoverEvent.showText("點擊這裡來自訂名稱"))
                                .toBungee())
                        .build();
                BookBuilder.openBook(book, player);
                break;
            case RULE:
                book = new BookBuilder("rule", "system")
                        .addPage(new TextBuilder().str("你已經設定完你的暱稱了!\n\n你現在的暱稱是:\n" + User.getUser(player).bGetName() + "." +
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
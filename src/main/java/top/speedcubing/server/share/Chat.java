package top.speedcubing.server.share;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import top.speedcubing.common.database.Database;
import top.speedcubing.lib.discord.DiscordWebhook;
import top.speedcubing.lib.minecraft.text.TextBuilder;
import top.speedcubing.lib.utils.Console;
import top.speedcubing.lib.utils.StringUtils;
import top.speedcubing.lib.utils.TimeFormatter;
import top.speedcubing.server.lang.LangMessage;
import top.speedcubing.server.player.User;
import top.speedcubing.server.speedcubingServer;
import top.speedcubing.server.utils.config;

public class Chat {

    private static String filter(String text) {
        for (Pattern p : config.filteredText) {
            Matcher matcher = p.matcher(text);
            StringBuilder replacement = new StringBuilder();
            while (matcher.find()) {
                String match = matcher.group();
                matcher.appendReplacement(replacement, StringUtils.repeat("*", match.length()));
            }
            matcher.appendTail(replacement);
            text = replacement.toString();
        }
        return text;
    }

    public static void globalChat(Collection<? extends Player> players, Player sender, LangMessage format, String message, String... replace) {
        LangMessage l = format.clone().replaceAll(replace);
        TextBuilder[] t = new TextBuilder[format.length()];
        for (int i = 0; i < format.length(); i++) {
            t[i] = new TextBuilder().str(l.get(i));
        }
        globalChat(players, sender, t, message);
    }

    public static void globalChat(Collection<? extends Player> players, Player sender, TextBuilder[] text, String message) {
        String filteredText = filter(message);
        TextBuilder[] filtered = new TextBuilder[text.length];
        TextBuilder[] unfiltered = new TextBuilder[text.length];
        for (int i = 0; i < text.length; i++) {
            String serial = text[i].serialize();
            unfiltered[i] = TextBuilder.unSerialize(serial.replace("%3%", message));
            filtered[i] = TextBuilder.unSerialize(serial.replace("%3%", filteredText));
        }
        String[] ignores = Database.connection.select("uuid").from("ignorelist").where("target='" + sender.getUniqueId() + "'").getStringArray();
        User user;
        c:
        for (Player p : players) {
            user = User.getUser(p);
            for (String s : ignores)
                if (user.player.getUniqueId().toString().equals(s))
                    continue c;
            user.sendLangTextComp(user.chatFilt ? filtered : unfiltered);
        }

        Console.printlnColor("§7[§aChatLog§7] [§b" + sender.getWorld().getName() + "§7] " + unfiltered[1].toPlainText());
        chatLogger(sender, unfiltered[1].toPlainText());
    }

    private static void chatLogger(Player sender, String message) {
        String timeFormat = TimeFormatter.unixToRealTime(System.currentTimeMillis(), "HH:mm:ss", TimeUnit.MILLISECONDS);
        String serverName = sender.getServer().getServerName();
        String webhook;
        switch (serverName) {
            case "lobby":
                webhook = speedcubingServer.LOBBY_WEBHOOK;
                break;
            case "knockbackffa":
                webhook = speedcubingServer.KBFFA_WEBHOOK;
                break;
            case "clutch":
                webhook = speedcubingServer.CLUTCH_WEBHOOK;
                break;
            case "mlgrush":
                webhook = speedcubingServer.MLGRUSH_WEBHOOK;
                break;
            case "fastbuilder":
                webhook = speedcubingServer.FASTBUILDER_WEBHOOK;
                break;
            case "practice":
                webhook = speedcubingServer.PRACTICE_WEBHOOK;
                break;
            case "reduce":
                webhook = speedcubingServer.REDUCE_WEBHOOK;
                break;
            default:
                webhook = "null";
                System.out.println("Error, Unknown server name: " + serverName);
        }
        try {
            DiscordWebhook discordWebhook = new DiscordWebhook(webhook);
            discordWebhook.setContent("```[" + timeFormat + "] " + "[" + sender.getWorld().getName() + "] " + ChatColor.stripColor(message) + "```");
            discordWebhook.execute();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}

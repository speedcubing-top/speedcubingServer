package top.speedcubing.server.share;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import top.speedcubing.lib.discord.DiscordWebhook;
import top.speedcubing.lib.discord.SimpleWebhook;
import top.speedcubing.lib.minecraft.text.TextBuilder;
import top.speedcubing.lib.utils.Console;
import top.speedcubing.lib.utils.StringUtils;
import top.speedcubing.common.database.Database;
import top.speedcubing.server.lang.LangMessage;
import top.speedcubing.server.player.User;
import top.speedcubing.server.speedcubingServer;
import top.speedcubing.server.utils.WebhookURL;
import top.speedcubing.server.utils.config;

public class Chat {

    public static String filter(String text) {
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

    public static void globalChat(Collection<? extends Player> players, Player sender, TextBuilder[] text, String message) {
        String filtered = filter(message);
        TextBuilder[] out2 = new TextBuilder[text.length];
        for (int i = 0; i < text.length; i++) {
            String serial = text[i].getSerial();
            text[i] = TextBuilder.unSerialize(serial.replace("%3%", message));
            out2[i] = TextBuilder.unSerialize(serial.replace("%3%", filtered));
        }
        String[] ignores = Database.connection.select("uuid").from("ignorelist").where("target='" + sender.getUniqueId() + "'").getStringArray();
        User user;
        c:
        for (Player p : players) {
            user = User.getUser(p);
            for (String s : ignores)
                if (user.player.getUniqueId().toString().equals(s))
                    continue c;
            user.sendLangTextComp(user.chatFilt ? out2 : text);
        }
        Console.printlnColor("§7[§aChatLog§7] [§b" + sender.getWorld().getName() + "§7] " + text[1]);
        chatLogger(sender,text[1].toString());
    }

    public static void globalChat(Collection<? extends Player> players, Player sender, LangMessage format, String message, String... replace) {
        String filteredMessage = filter(message);
        LangMessage l1 = format.clone().replaceAll(replace).replace(3, message);
        LangMessage l2 = format.clone().replaceAll(replace).replace(3, filteredMessage);
        String[] ignores = Database.connection.select("uuid").from("ignorelist").where("target='" + sender.getUniqueId() + "'").getStringArray();
        User user;
        c:
        for (Player pp : players) {
            user = User.getUser(pp);
            for (String s : ignores)
                if (user.player.getUniqueId().toString().equals(s))
                    continue c;
            user.sendLangMessage(user.chatFilt ? l2 : l1);
        }
        Console.printlnColor("§7[§aChatLog§7] [§b" + sender.getWorld().getName() + "§7] " + l1.get(1));
        chatLogger(sender,l1.get(1));
    }
    private static void chatLogger(Player sender, String message) {
        LocalDateTime now = LocalDateTime.now();
        String formatHour = String.format("%02d",now.getHour());
        String formatMinute = String.format("%02d",now.getMinute());
        String formatSecond = String.format("%02d", now.getSecond());
        String serverName = sender.getServer().getServerName();
        WebhookURL webhookURL = null;
        try {
            webhookURL = WebhookURL.valueOf(serverName.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Error, Unknown server name: " + serverName);
        }
        String webhook = webhookURL != null ? webhookURL.getUrl() : "null";
        try {
            DiscordWebhook discordWebhook = new DiscordWebhook(webhook);
            discordWebhook.setContent("```[" + formatHour + ":" + formatMinute + ":" + formatSecond + " ChatLogger] " + "[" + sender.getWorld().getName() + "] " + ChatColor.stripColor(message) + "```");
            discordWebhook.execute();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}

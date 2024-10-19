package top.speedcubing.server.share;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import top.speedcubing.common.database.Database;
import top.speedcubing.common.server.MinecraftServer;
import top.speedcubing.lib.discord.DiscordWebhook;
import top.speedcubing.lib.minecraft.MinecraftConsole;
import top.speedcubing.lib.utils.StringUtils;
import top.speedcubing.lib.utils.SystemUtils;
import top.speedcubing.lib.utils.TimeFormatter;
import top.speedcubing.server.lang.Lang;
import top.speedcubing.server.player.User;
import top.speedcubing.server.utils.Configuration;

public class Chat {

    private static String filter(String text) {
        for (Pattern p : Configuration.filteredText) {
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

    public static void globalChat(Collection<? extends Player> players, Player sender, Lang format, String message, String... replace) {
        format.param(replace);
        String filteredMessage = filter(message);
        String[] ignores = Database.connection.select("uuid").from("ignorelist").where("target='" + sender.getUniqueId() + "'").getStringArray();
        User user;
        c:
        for (Player p : players) {
            user = User.getUser(p);
            for (String s : ignores)
                if (user.player.getUniqueId().toString().equals(s))
                    continue c;
            user.sendMessage(format, (user.chatFilt ? message : filteredMessage));
        }

        format.param(message);

        MinecraftConsole.printlnColor("§7[§aChatLog§7] [§b" + sender.getWorld().getName() + "§7] " + format.getString(0));
        chatLogger(sender, format.getComponent(0).toPlainText());
    }

    private static final Executor discordWebhookPool = Executors.newSingleThreadExecutor();

    private static void chatLogger(Player sender, String message) {
        String timeFormat = TimeFormatter.unixToRealTime(SystemUtils.getCurrentSecond(), "HH:mm:ss", TimeUnit.SECONDS);
        DiscordWebhook discordWebhook = new DiscordWebhook(MinecraftServer.getServer(Bukkit.getServerName()).getWebhook());
        message = ChatColor.stripColor(message.replaceAll("`", "'").replaceAll("\n", "/n"));
        discordWebhook.setContent("```[" + timeFormat + "] " + "[" + sender.getWorld().getName() + "] " + message + "```");
        discordWebhookPool.execute(() -> {
            try {
                discordWebhook.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}

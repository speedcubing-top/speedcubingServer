package top.speedcubing.server.share;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import top.speedcubing.common.database.Database;
import top.speedcubing.lib.discord.DiscordWebhook;
import top.speedcubing.lib.minecraft.MinecraftConsole;
import top.speedcubing.lib.minecraft.text.TextBuilder;
import top.speedcubing.lib.utils.StringUtils;
import top.speedcubing.lib.utils.TimeFormatter;
import top.speedcubing.server.lang.LangMessage;
import top.speedcubing.server.lang.LanguageSystem;
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

    public static void globalChat(Collection<? extends Player> players, Player sender, LangMessage format, String message, String... replace) {
        LangMessage l = format.clone().replaceAll(replace);
        TextBuilder[] t = new TextBuilder[LanguageSystem.langCount];
        for (int i = 0; i < LanguageSystem.langCount; i++) {
            t[i] = new TextBuilder().str(l.get(i));
        }
        globalChat(players, sender, t, message);
    }

    public static void globalChat(Collection<? extends Player> players, Player sender, TextBuilder[] text, String message) {
        String filteredText = filter(message);
        TextBuilder[] filtered = new TextBuilder[LanguageSystem.langCount];
        TextBuilder[] unfiltered = new TextBuilder[LanguageSystem.langCount];
        for (int i = 0; i < LanguageSystem.langCount; i++) {
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

        MinecraftConsole.printlnColor("§7[§aChatLog§7] [§b" + sender.getWorld().getName() + "§7] " + unfiltered[1].toColorText());
        chatLogger(sender, unfiltered[1].toPlainText());
    }

    private static final Executor discordWebhookPool = Executors.newSingleThreadExecutor();

    private static void chatLogger(Player sender, String message) {
        String timeFormat = TimeFormatter.unixToRealTime(System.currentTimeMillis(), "HH:mm:ss", TimeUnit.MILLISECONDS);
        DiscordWebhook discordWebhook = new DiscordWebhook(Configuration.discordWebook);
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

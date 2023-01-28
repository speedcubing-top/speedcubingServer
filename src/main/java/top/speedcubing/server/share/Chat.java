package top.speedcubing.server.share;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import top.speedcubing.lib.bungee.TextBuilder;
import top.speedcubing.lib.utils.Console;
import top.speedcubing.server.config;
import top.speedcubing.server.libs.User;
import top.speedcubing.server.speedcubingServer;

import java.util.Collection;
import java.util.regex.Pattern;

public class Chat {
    public static boolean checkBlockedText(String string) {
        for (Pattern p : config.blockedText)
            if (p.matcher(string).matches())
                return true;
        return false;
    }

    public static String filter(String string) {
        for (String s : config.filteredText) {
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < s.length(); i++)
                b.append("*");
            string = string.replace(s, b.toString());
        }
        return string;
    }

    public static void globalChat(Collection<? extends Player> players, Player sender, TextBuilder[] text, TextBuilder[] filteredtext) {
        String[] ignores = speedcubingServer.connection.select("uuid").from("ignorelist").where("target='" + sender.getUniqueId() + "'").getStringArray();
        User user;
        c:
        for (Player p : players) {
            user = User.getUser(p);
            for (String s : ignores)
                if (user.player.getUniqueId().toString().equals(s))
                    continue c;
            user.sendLangTextComp(user.chatFilt ? text : filteredtext);
        }
    }

    public static void globalChat(Collection<? extends Player> players, Player sender, String[] format, String message, String... replace) {
        boolean blocked = checkBlockedText(message);
        String filtered = Chat.filter(message);
        String[] out = new String[format.length];
        String[] out2 = new String[format.length];
        for (int i = 0; i < format.length; i++) {
            out[i] = format[i];
            out2[i] = format[i];
            for (int j = 0; j < replace.length; j++) {
                out[i] = out[i].replace("%" + j + "%", replace[j]);
                out2[i] = out2[i].replace("%" + j + "%", replace[j]);
            }
            out[i] = out[i].replace("%msg%", message);
            out2[i] = out2[i].replace("%msg%", filtered);
        }
        if (!blocked) {
            String[] ignores = speedcubingServer.connection.select("uuid").from("ignorelist").where("target='" + sender.getUniqueId() + "'").getStringArray();
            User user;
            c:
            for (Player pp : players) {
                user = User.getUser(pp);
                for (String s : ignores)
                    if (user.player.getUniqueId().toString().equals(s))
                        continue c;
                user.sendLangMessage(user.chatFilt ? out2 : out);
            }
        }
        Console.sendColoredConsole("§7[§aChatLog§7] [§b" + sender.getWorld().getName() + "§7] " + (blocked ? "§c[BLOCKED]§f " : "") + out[1]);
    }
}

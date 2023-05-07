package top.speedcubing.server.share;

import org.bukkit.entity.Player;
import top.speedcubing.lib.bungee.TextBuilder;
import top.speedcubing.lib.utils.*;
import top.speedcubing.server.config;
import top.speedcubing.server.database.Database;
import top.speedcubing.server.libs.User;

import java.util.Collection;

public class Chat {

    public static void globalChat(Collection<? extends Player> players, Player sender, TextBuilder[] text, TextBuilder[] filteredtext) {
        String[] ignores = Database.connection.select("uuid").from("ignorelist").where("target='" + sender.getUniqueId() + "'").getStringArray();
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
        String filtered = message;


        for (String s : config.filteredText)
            filtered = filtered.replace(s, StringUtils.repeat("*", s.length()));

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
        String[] ignores = Database.connection.select("uuid").from("ignorelist").where("target='" + sender.getUniqueId() + "'").getStringArray();
        User user;
        c:
        for (Player pp : players) {
            user = User.getUser(pp);
            for (String s : ignores)
                if (user.player.getUniqueId().toString().equals(s))
                    continue c;
            user.sendLangMessage(user.chatFilt ? out2 : out);
        }
        Console.sendColoredConsole("§7[§aChatLog§7] [§b" + sender.getWorld().getName() + "§7] " + out[1]);
    }
}

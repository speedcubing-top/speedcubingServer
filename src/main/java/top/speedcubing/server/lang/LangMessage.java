package top.speedcubing.server.lang;

import java.util.Arrays;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import top.speedcubing.server.player.User;

public class LangMessage implements Cloneable {
    String[] s;

    public static LangMessage load(String name) {
        String[] s = new String[LanguageSystem.langCount];
        try {
            for (int i = 0; i < LanguageSystem.langCount; i++) {
                s[i] = LanguageSystem.lang[i].get(name).getAsString();
            }
        }catch (Exception e) {
            Arrays.fill(s, "not-found");
            e.printStackTrace();
        }
        return new LangMessage(s);
    }

    public LangMessage(String... s) {
        this.s = s;
    }

    public String get(int lang) {
        return s[lang];
    }

    public String get(CommandSender source) {
        return get(source instanceof ConsoleCommandSender ? 1 : User.getUser(source).lang);
    }

    public String get(User user) {
        return s[user.lang];
    }

    public LangMessage replaceAll(String... replaces) {
        for (int i = 0; i < replaces.length; i++)
            replace(i + 1, replaces[i]);
        return this;
    }

    public LangMessage replace(int i, String replaces) {
        for (int l = 0; l < LanguageSystem.langCount; l++)
            s[l] = s[l].replace("%" + i + "%", replaces);
        return this;
    }

    public LangMessage clone() {
        try {
            super.clone();
            return new LangMessage(s.clone());
        } catch (CloneNotSupportedException var2) {
            throw new Error(var2);
        }
    }
}

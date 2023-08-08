package top.speedcubing.server.lang;

import org.bukkit.command.*;
import top.speedcubing.lib.minecraft.text.TextBuilder;
import top.speedcubing.server.player.User;

public class LangMessage implements Cloneable {
    String[] s;

    public LangMessage(String... s) {
        this.s = s;
    }

    public String get(int lang) {
        return s[lang];
    }

    public String get(CommandSender source) {
        return get(source instanceof ConsoleCommandSender ? 1 : User.getUser(source).lang);
    }

    public LangMessage replaceAll(String... replaces) {
        for (int i = 0; i < replaces.length; i++)
            replace(i + 1, replaces[i]);
        return this;
    }

    public LangMessage replace(int i, String replaces) {
        for (int l = 0; l < s.length; l++)
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


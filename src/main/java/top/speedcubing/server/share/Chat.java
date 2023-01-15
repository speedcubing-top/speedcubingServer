package top.speedcubing.server.share;

import top.speedcubing.server.config;

import java.util.regex.Pattern;

public class Chat {
    public static boolean filt(String string) {
        for (Pattern p : config.blockedText)
            if (p.matcher(string).matches())
                return true;
        return false;
    }
}

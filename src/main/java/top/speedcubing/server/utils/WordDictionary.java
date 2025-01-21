package top.speedcubing.server.utils;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import java.net.URL;

public class WordDictionary {
    public static IDictionary dict;

    static {
        try {
            URL url = new URL("file", null, "/storage/WNdb-3.0/dict");
            dict = new Dictionary(url);
            dict.open();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}

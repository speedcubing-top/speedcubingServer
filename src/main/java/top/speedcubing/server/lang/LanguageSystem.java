package top.speedcubing.server.lang;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;

public class LanguageSystem {
    public static int langCount = 4;

    public static JsonObject[] lang = new JsonObject[langCount];

    public static void init() {
        try {
            File dir = new File("/storage/lang");
            for (File f : dir.listFiles()) {
                int id = Integer.parseInt(f.getName().split("_")[0]);
                lang[id] = JsonParser.parseReader(new FileReader(f)).getAsJsonObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

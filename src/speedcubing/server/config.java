package speedcubing.server;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import speedcubing.server.libs.User;

import java.io.FileReader;
import java.util.*;

public class config {
    public void reload() {
        try {
            JsonObject object = new JsonParser().parse(new FileReader("../../../server.json")).getAsJsonObject();
            LeftCpsLimit = object.get("leftcpslimit").getAsInt();
            RightCpsLimit = object.get("rightcpslimit").getAsInt();

            speedcubingServer.colors.clear();
            speedcubingServer.rankPermissions.clear();
            speedcubingServer.ranks.clear();
            for (Map.Entry<String, JsonElement> c : object.getAsJsonObject("ranks").entrySet()) {
                String[] colors = new Gson().fromJson(c.getValue().getAsJsonObject().get("texts").getAsJsonArray().toString(), new TypeToken<String[]>() {
                }.getType());
                speedcubingServer.colors.put(c.getKey(), new String[]{colors[0], colors[0].lastIndexOf('§') == -1 ? "" : ("§" + colors[0].charAt(colors[0].lastIndexOf('§') + 1)), colors[1]});
                speedcubingServer.rankPermissions.put(c.getKey(), new Gson().fromJson(c.getValue().getAsJsonObject().get("permissions2").getAsJsonArray().toString(), new TypeToken<Set<String>>() {
                }.getType()));
                speedcubingServer.ranks.add(c.getKey());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static int LeftCpsLimit = Integer.MAX_VALUE;
    public static int RightCpsLimit = Integer.MAX_VALUE;
    public static String SERVERIP = "speedcubing.serveftp.net";
}

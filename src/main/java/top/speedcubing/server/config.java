package top.speedcubing.server;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import top.speedcubing.lib.utils.SQL.SQLUtils;
import top.speedcubing.system.speedcubingSystem;

import java.io.FileReader;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class config {
    public void reload() {
        try {
            JsonObject object = new JsonParser().parse(new FileReader("../../../server.json")).getAsJsonObject();
            LeftCpsLimit = object.get("leftcpslimit").getAsInt();
            RightCpsLimit = object.get("rightcpslimit").getAsInt();
            speedcubingServer.blockedLog.clear();
            speedcubingServer.blockedTab.clear();
            speedcubingServer.blockedMod.clear();
            object.get("spigotblockedlog").getAsJsonArray().forEach(a -> speedcubingServer.blockedLog.add(Pattern.compile(a.getAsString())));
            object.get("spigotblockedtab").getAsJsonArray().forEach(a -> speedcubingServer.blockedTab.add(a.getAsString()));
            object.get("blockedmod").getAsJsonArray().forEach(a -> speedcubingServer.blockedMod.add(a.getAsString().toLowerCase()));
            speedcubingServer.colors.clear();
            speedcubingServer.rankPermissions.clear();
            speedcubingServer.ranks.clear();
            for (Map.Entry<String, JsonElement> c : object.getAsJsonObject("ranks").entrySet()) {
                String[] colors = new Gson().fromJson(c.getValue().getAsJsonObject().get("texts").getAsJsonArray().toString(), new TypeToken<String[]>() {
                }.getType());
                speedcubingServer.colors.put(c.getKey(), new String[]{colors[0], colors[0].lastIndexOf('§') == -1 ? "" : ("§" + colors[0].charAt(colors[0].lastIndexOf('§') + 1)), colors[1]});
                speedcubingServer.rankPermissions.put(c.getKey(), new Gson().fromJson(c.getValue().getAsJsonObject().get("permissions").getAsJsonArray().toString(), new TypeToken<Set<String>>() {
                }.getType()));
                speedcubingServer.ranks.add(c.getKey());
            }
            for (String s : SQLUtils.getStringArray(speedcubingSystem.connection.select("groups", "name", "1"))) {
                speedcubingServer.grouppermissions.put(s, Sets.newHashSet(SQLUtils.getString(speedcubingSystem.connection.select("groups", "perms", "name='" + s + "'")).split("\\|")));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static int LeftCpsLimit = Integer.MAX_VALUE;
    public static int RightCpsLimit = Integer.MAX_VALUE;
    public static String SERVERIP = Bukkit.getPort() % 2 == 0 ? "cracked.speedcubing.top" : "speedcubing.top";
}

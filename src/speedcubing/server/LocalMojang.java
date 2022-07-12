package speedcubing.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import speedcubing.lib.api.MojangAPI;
import speedcubing.lib.utils.SQL.SQLUtils;

public class LocalMojang {
    public static String[] getUUIDandName(String name) {
        String[] datas = SQLUtils.getStringArray(speedcubingServer.systemconnection.select("mojangnamedb", "at,uuid,name", "name='" + name + "'"));
        if (datas.length != 0) {
            if (datas[0].equals("0") || System.currentTimeMillis() - Long.parseLong(datas[0]) > 2592000000L) {
                String uuid = MojangAPI.getUUID(name);
                JsonObject object = a(uuid);
                String cap = object.get("name").getAsString();
                speedcubingServer.systemconnection.update("mojangnamedb", "at=" + (object.has("changedToAt") ? object.get("changedToAt").getAsString() : "0") + ",name='" + cap + "'", "uuid='" + uuid + "'");
                return new String[]{uuid, cap};
            } else return new String[]{datas[1], datas[2]};
        } else {
            String uuid = MojangAPI.getUUID(name);
            JsonObject object = a(uuid);
            String cap = object.get("name").getAsString();
            speedcubingServer.systemconnection.insert("mojangnamedb", "at,uuid,name", (object.has("changedToAt") ? object.get("changedToAt").getAsString() : "0") + ",'" + uuid + "','" + cap + "'");
            return new String[]{uuid, cap};
        }
    }

    public static String getUUID(String name) {
        String[] datas = SQLUtils.getStringArray(speedcubingServer.systemconnection.select("mojangnamedb", "at,uuid", "name='" + name + "'"));
        if (datas.length != 0) {
            if (datas[0].equals("0") || System.currentTimeMillis() - Long.parseLong(datas[0]) > 2592000000L) {
                String uuid = MojangAPI.getUUID(name);
                new Thread(() -> {
                    JsonObject object = a(uuid);
                    speedcubingServer.systemconnection.update("mojangnamedb", "at=" + (object.has("changedToAt") ? object.get("changedToAt").getAsString() : "0") + ",name='" + object.get("name").getAsString() + "'", "uuid='" + uuid + "'");
                }).start();
                return uuid;
            } else return datas[1];
        } else {
            String uuid = MojangAPI.getUUID(name);
            new Thread(() -> {
                JsonObject object = a(uuid);
                speedcubingServer.systemconnection.insert("mojangnamedb", "at,uuid,name", (object.has("changedToAt") ? object.get("changedToAt").getAsString() : "0") + ",'" + uuid + "','" + object.get("name").getAsString() + "'");
            }).start();
            return uuid;
        }
    }
    private static JsonObject a(String uuid) {
        JsonArray element = MojangAPI.getNameHistory(uuid).getAsJsonArray();
        return element.get(element.size() - 1).getAsJsonObject();
    }
}

package speedcubing.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import speedcubing.lib.api.MojangAPI;
import speedcubing.lib.utils.SQL.SQLUtils;

public class LocalMojang {
    public static String[] getUUIDandName(String name) {
        String[] datas = SQLUtils.getStringArray(speedcubingServer.systemconnection.select("mojangnamedb", "at,uuid,name", "name='" + name + "'"));
        if (datas != null) {
            if (datas[0].equals("0") || System.currentTimeMillis() - Long.parseLong(datas[0]) > 2592000000L) {
                String uuid = MojangAPI.getUUID(name);
                JsonArray element = MojangAPI.getNameHistory(uuid).getAsJsonArray();
                JsonObject object = element.get(element.size() - 1).getAsJsonObject();
                String cap = object.get("name").getAsString();
                speedcubingServer.systemconnection.update("mojangnamedb", "at=" + (object.has("changedToAt") ? object.get("changedToAt").getAsString() : "0") + ",name='" + cap + "'", "uuid='" + uuid + "'");
                return new String[]{uuid, cap};
            } else return new String[]{datas[1], datas[2]};
        } else {
            String uuid = MojangAPI.getUUID(name);
            JsonArray element = MojangAPI.getNameHistory(uuid).getAsJsonArray();
            JsonObject object = element.get(element.size() - 1).getAsJsonObject();
            String cap = object.get("name").getAsString();
            speedcubingServer.systemconnection.insert("mojangnamedb", "at,uuid,name", (object.has("changedToAt") ? object.get("changedToAt").getAsString() : "0") + ",'" + cap + "','" + uuid + "'");
            return new String[]{uuid, cap};
        }
    }

    public static String getUUID(String name) {
        String[] datas = SQLUtils.getStringArray(speedcubingServer.systemconnection.select("mojangnamedb", "at,uuid", "name='" + name + "'"));
        if (datas != null) {
            if (datas[0].equals("0") || System.currentTimeMillis() - Long.parseLong(datas[0]) > 2592000000L) {
                String uuid = MojangAPI.getUUID(name);
                JsonArray element = MojangAPI.getNameHistory(uuid).getAsJsonArray();
                JsonObject object = element.get(element.size() - 1).getAsJsonObject();
                speedcubingServer.systemconnection.update("mojangnamedb", "at=" + (object.has("changedToAt") ? object.get("changedToAt").getAsString() : "0") + ",name='" + object.get("name").getAsString() + "'", "uuid='" + uuid + "'");
                return uuid;
            } else return datas[1];
        } else {
            String uuid = MojangAPI.getUUID(name);
            JsonArray element = MojangAPI.getNameHistory(uuid).getAsJsonArray();
            JsonObject object = element.get(element.size() - 1).getAsJsonObject();
            speedcubingServer.systemconnection.insert("mojangnamedb", "at,uuid,name", (object.has("changedToAt") ? object.get("changedToAt").getAsString() : "0") + ",'" + object.get("name").getAsString() + "','" + uuid + "'");
            return uuid;
        }
    }
}

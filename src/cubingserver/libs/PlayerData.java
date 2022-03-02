package cubingserver.libs;

import cubingserver.speedcubingServer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    public static Map<UUID, Integer> LangCache = new HashMap<>();
    public static Map<UUID, Integer> RankCache = new HashMap<>();

    public static int getRank(UUID uuid) {
        if (RankCache.containsKey(uuid))
            return RankCache.get(uuid);
        else {
            int x = speedcubingServer.connection.selectInt("playersdata", "priority", "uuid='" + uuid + "'");
            RankCache.put(uuid, x);
            return x;
        }
    }
    public static int getRank(String uuid) {
        UUID id = UUID.fromString(uuid);
        if (RankCache.containsKey(id))
            return RankCache.get(id);
        else {
            int x = speedcubingServer.connection.selectInt("playersdata", "priority", "uuid='" + uuid + "'");
            RankCache.put(id, x);
            return x;
        }
    }

    public static int getAbilityRank(UUID uuid) {
        if (RankCache.containsKey(uuid))
            return RankCache.get(uuid);
        else {
            int x = speedcubingServer.connection.selectInt("playersdata", "realpriority", "uuid='" + uuid + "'");
            RankCache.put(uuid, x);
            return x;
        }
    }
    public static int getAbilityRank(String uuid) {
        UUID id = UUID.fromString(uuid);
        if (RankCache.containsKey(id))
            return RankCache.get(id);
        else {
            int x = speedcubingServer.connection.selectInt("playersdata", "realpriority", "uuid='" + uuid + "'");
            RankCache.put(id, x);
            return x;
        }
    }

    public static int getLang(UUID uuid) {
        if (LangCache.containsKey(uuid))
            return LangCache.get(uuid);
        else {
            int x = speedcubingServer.connection.selectInt("playersdata", "lang", "uuid='" + uuid + "'");
            LangCache.put(uuid, x);
            return x;
        }
    }

    public static int getLang(String uuid) {
        UUID id = UUID.fromString(uuid);
        if (LangCache.containsKey(id))
            return LangCache.get(id);
        else {
            int x = speedcubingServer.connection.selectInt("playersdata", "lang", "uuid='" + uuid + "'");
            LangCache.put(id, x);
            return x;
        }
    }

    public static int getVersion(UUID uuid) {
        return speedcubingServer.connection.selectInt("playersdata", "ver", "uuid='" + uuid + "'");
    }

    public static int getVersion(String uuid) {
        return speedcubingServer.connection.selectInt("playersdata", "ver", "uuid='" + uuid + "'");
    }
}

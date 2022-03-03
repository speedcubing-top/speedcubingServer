package cubingserver.libs;

import cubingserver.speedcubingServer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    public static Map<UUID, Integer> LangCache = new HashMap<>();
    public static Map<UUID, Integer> RankCache = new HashMap<>();
    public static Map<UUID,Integer> AbilityCache = new HashMap<>();

    public static int getRank(UUID uuid) {
        return RankCache.get(uuid);
    }

    public static int getRank(String uuid) {
        return RankCache.get(UUID.fromString(uuid));
    }

    public static int getAbilityRank(UUID uuid) {
        if (AbilityCache.containsKey(uuid))
            return AbilityCache.get(uuid);
        else {
            int x = speedcubingServer.connection.selectInt("playersdata", "realpriority", "uuid='" + uuid + "'");
            AbilityCache.put(uuid, x);
            return x;
        }
    }

    public static int getAbilityRank(String uuid) {
        UUID id = UUID.fromString(uuid);
        if (AbilityCache.containsKey(id))
            return AbilityCache.get(id);
        else {
            int x = speedcubingServer.connection.selectInt("playersdata", "realpriority", "uuid='" + uuid + "'");
            AbilityCache.put(id, x);
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

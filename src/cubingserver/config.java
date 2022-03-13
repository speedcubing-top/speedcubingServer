package cubingserver;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cubingserver.StringList.GlobalString;
import cubingserver.libs.User;
import org.bukkit.command.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class config implements CommandExecutor, TabCompleter {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage(GlobalString.UnknownCommand[commandSender instanceof ConsoleCommandSender ? 1 : User.getLang(((Player) commandSender).getUniqueId())]);
        reload();
        return true;
    }

    public void reload() {
        try {
            JsonObject object = new JsonParser().parse(new FileReader("../../../server.json")).getAsJsonObject();
            LeftCpsLimit = object.get("leftcpslimit").getAsInt();
            RightCpsLimit = object.get("rightcpslimit").getAsInt();

            JsonArray rankdatas = object.get("rankpriority").getAsJsonArray();
            User.colors.clear();
            User.permissions.clear();
            User.ranks.clear();
            for (int i = 0; i < rankdatas.size(); i++) {
                String rankName = rankdatas.get(i).getAsString();
                User.ranks.add(rankName);
                JsonObject rank = object.get("ranks").getAsJsonObject().get(rankName).getAsJsonObject();
                List<String> a = new ArrayList<>();
                for (int j = 0; j < rank.get("texts").getAsJsonArray().size(); j++) {
                    a.add(rank.get("texts").getAsJsonArray().get(j).getAsString());
                }
                User.colors.put(rankName, a.toArray(new String[0]));
                Set<String> b = new HashSet<>();
                for (int j = 0; j < rank.get("permissions2").getAsJsonArray().size(); j++) {
                    b.add(rank.get("permissions2").getAsJsonArray().get(j).getAsString());
                }
                User.permissions.put(rankName, b);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static int LeftCpsLimit = Integer.MAX_VALUE;
    public static int RightCpsLimit = Integer.MAX_VALUE;
    public static String SERVERIP = "speedcubing.serveftp.net";

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}

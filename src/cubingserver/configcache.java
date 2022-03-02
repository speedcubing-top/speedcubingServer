package cubingserver;

import cubingserver.StringList.CommandString;
import cubingserver.libs.PlayerData;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class configcache implements CommandExecutor, TabCompleter {
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage(CommandString.UnknownCommand[commandSender instanceof ConsoleCommandSender ? 1 : PlayerData.getLang(((Player) commandSender).getUniqueId())]);
        reload();
        return true;
    }

    public void reload() {
        try {
            JSONObject object = (JSONObject) new JSONParser().parse(new FileReader("../../../server.json"));
            LeftCpsLimit = (int) (long) object.get("leftcpslimit");
            RightCpsLimit = (int) (long) object.get("rightcpslimit");
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

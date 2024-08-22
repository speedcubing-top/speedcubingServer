package top.speedcubing.server.cubingcmd;

import org.bukkit.command.CommandSender;
import top.speedcubing.server.lang.GlobalString;
import top.speedcubing.server.player.User;
import top.speedcubing.server.system.command.CubingCommand;

public class terms extends CubingCommand {
    public terms(){
        super("terms");
    }

    @Override
    public void execute(CommandSender sender, String command, String[] args) {
        User.getUser(sender).sendLangMessage(GlobalString.terms);
    }
}

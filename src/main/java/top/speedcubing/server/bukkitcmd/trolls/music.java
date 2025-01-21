package top.speedcubing.server.bukkitcmd.trolls;

import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.model.playmode.MonoStereoMode;
import com.xxmicloxx.NoteBlockAPI.model.playmode.StereoMode;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class music implements CommandExecutor {
    public static Song song;
    public static RadioSongPlayer radioSongPlayer;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
//        if (!Bukkit.getServerName().equalsIgnoreCase("lobby")) {
//            sender.sendMessage("§cThis command is disabled at here");
//            return true;
//        }
        if (!(sender instanceof Player player)) return true;
        if (args.length == 0) {
            player.sendMessage("§cUsage: /music <play|resume|pause|loop|stop|add|remove>");
            return true;
        }
        if (args[0].equalsIgnoreCase("play")) {
            if (radioSongPlayer != null && radioSongPlayer.isPlaying()) {
                player.sendMessage("§cSong is already playing.");
                return true;
            }
            if (args.length != 2) {
                player.sendMessage("§cUsage: /music play <song>");
                return true;
            }
            File file = new File("/storage/mcsongs/" + args[1] + ".nbs");
            if (!file.exists()) {
                player.sendMessage("§cSong not found.");
                return true;
            }
            song = NBSDecoder.parse(file);
            if (song == null) {
                player.sendMessage("§cSong load failed.");
                return true;
            }
            StereoMode stereoMode = new StereoMode();
            stereoMode.setFallbackChannelMode(new MonoStereoMode());
            radioSongPlayer = new RadioSongPlayer(song);
            radioSongPlayer.addPlayer(player);
            radioSongPlayer.setPlaying(true);
            radioSongPlayer.setChannelMode(stereoMode);
            player.sendMessage("§aPlaying song.");
            return true;
        }
        if (args[0].equalsIgnoreCase("resume")) {
            if (radioSongPlayer == null) {
                player.sendMessage("§cNo song to resume.");
                return true;
            }
            radioSongPlayer.setPlaying(true);
            player.sendMessage("§aResumed song.");
            return true;
        }
        if (args[0].equalsIgnoreCase("pause")) {
            if (radioSongPlayer == null) {
                player.sendMessage("§cNo song to pause.");
                return true;
            }
            radioSongPlayer.setPlaying(false);
            player.sendMessage("§aPaused song.");
            return true;
        }
        if (args[0].equalsIgnoreCase("loop")) {
            if (radioSongPlayer == null) {
                player.sendMessage("§cNo song to loop.");
                return true;
            }
            radioSongPlayer.setRepeatMode(radioSongPlayer.getRepeatMode() == RepeatMode.ALL ? RepeatMode.NO : RepeatMode.ALL);
            player.sendMessage("§aSet loop mode to " + radioSongPlayer.getRepeatMode().name());
            return true;
        }
        if (args[0].equalsIgnoreCase("stop")) {
            if (radioSongPlayer == null) {
                player.sendMessage("§cNo song to stop.");
                return true;
            }
            radioSongPlayer.setPlaying(false);
            radioSongPlayer.destroy();
            player.sendMessage("§aStopped song.");
            return true;
        }
        if (args[0].equalsIgnoreCase("add")) {
            if (args.length != 2) {
                player.sendMessage("§cUsage: /music add <player>");
                return true;
            }
            if (radioSongPlayer == null) {
                player.sendMessage("§cNo song to add.");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage("§cPlayer not found.");
                return true;
            }
            radioSongPlayer.addPlayer(player);
            player.sendMessage("§aAdded " + target.getName() + " to song player.");
            return true;
        }
        if (args[0].equalsIgnoreCase("remove")) {
            if (args.length != 2) {
                player.sendMessage("§cUsage: /music remove <player>");
                return true;
            }
            if (radioSongPlayer == null) {
                player.sendMessage("§cNo song to remove.");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage("§cPlayer not found.");
                return true;
            }
            radioSongPlayer.removePlayer(player);
            player.sendMessage("§aRemoved " + target.getName() + " from song player.");
            return true;
        }

        return true;
    }

}
package top.speedcubing.server.authenticator.commands;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.speedcubing.lib.utils.ByteArrayDataBuilder;
import top.speedcubing.server.authenticator.handlers.AuthHandler;
import top.speedcubing.server.authenticator.listeners.PlayerListener;
import top.speedcubing.server.database.Database;
import top.speedcubing.server.player.User;
import top.speedcubing.server.speedcubingServer;

public class AuthenticatorCommand implements CommandExecutor {
    private final Map<UUID, Integer> verifedCount = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (Bukkit.getServerName().equalsIgnoreCase("limbo")) {
            sender.sendMessage("§c2FA is disabled at here");
            Player player = (Player) sender;
            player.performCommand("l");
            return true;
        }
        if (args.length == 0) {
            usage(sender);
            return true;
        } else if (args.length == 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cThis command is player only.");
                return true;
            }
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();
            String code = args[0];
            if (isInt(code)) {
                if (code.length() == 6) {
                    if (!PlayerListener.sessionStatusMap.containsKey(uuid)) {
                        AuthHandler.sendErrorMessage(player);
                        return true;
                    }
                    if (PlayerListener.sessionStatusMap.get(uuid)) {
                        player.sendMessage("§aYou have successfully authenticated");
                        return true;
                    }
                    if (!PlayerListener.hasKeyMap.containsKey(uuid)) {
                        AuthHandler.sendErrorMessage(player);
                        return true;
                    }
                    if (PlayerListener.hasKeyMap.get(uuid)) {
                        String key = AuthHandler.getKey(player.getUniqueId());
                        if (key != null && new GoogleAuthenticator().authorize(key, Integer.parseInt(code))) {
                            AuthHandler.setTrustedSessions(player, true);
                            player.sendMessage("§aYou have successfully authenticated");
                        } else {
                            player.sendMessage("§cThe key you entered was not valid, please try again");
                            if (verifedCount.containsKey(player.getUniqueId())) {
                                Integer score = verifedCount.get(player.getUniqueId());
                                if (score != 10) {
                                    verifedCount.put(player.getUniqueId(), score + 1);
                                } else {
                                    verifedCount.remove(player.getUniqueId());
                                    String banCmd = "ban " + player.getName() + " 0 Suspicious activities detected on your account , contact support for assistance. -hideid";
                                    speedcubingServer.tcpClient.send(speedcubingServer.getRandomBungeePort(), new ByteArrayDataBuilder().writeUTF("proxycmd").writeUTF(banCmd).toByteArray());
                                }
                            } else {
                                verifedCount.put(player.getUniqueId(), 1);
                            }
                        }
                    } else {
                        player.sendMessage("§cYou don't have key, Please use /2fa setup <code>");
                    }
                } else {
                    player.sendMessage("§cInvalid key entered");
                }
            } else if (args[0].equals("reset")) {
                if (AuthHandler.hasTrustedSessions(player.getUniqueId())) {
                    AuthHandler.setTrustedSessions(player, false);
                    player.sendMessage("§aSuccessfully reset your trusted sessions");
                } else {
                    player.sendMessage("§cYou don't have a trusted session");
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("setup")) {
                Player player = (Player) sender;
                UUID uuid = player.getUniqueId();
                if (!PlayerListener.twofaStatusMap.containsKey(uuid)) {
                    AuthHandler.sendErrorMessage(player);
                    return true;
                }
                if (PlayerListener.twofaStatusMap.get(uuid)) {
                    if (!PlayerListener.hasKeyMap.containsKey(uuid)) {
                        AuthHandler.sendErrorMessage(player);
                        return true;
                    }
                    if (!PlayerListener.hasKeyMap.get(uuid)) {
                        String code = args[1];
                        if (code.length() == 6 && isInt(code)) {
                            if (PlayerListener.keyMapForNoKey.containsKey(player.getUniqueId())) {
                                String key = PlayerListener.keyMapForNoKey.get(player.getUniqueId());
                                if (key != null && new GoogleAuthenticator().authorize(key, Integer.parseInt(code))) {
                                    AuthHandler.setKey(player.getUniqueId(), key);
                                    AuthHandler.setTrustedSessions(player, true);
                                    player.sendMessage("§a2FA Successfully set up.");
                                    removeMap(player);
                                    PlayerListener.keyMapForNoKey.remove(player.getUniqueId());
                                } else {
                                    player.sendMessage("§cInvalid key entered.");
                                }
                            } else {
                                player.sendMessage("§cAn error occurred. Please contact staff");
                            }
                        } else {
                            player.sendMessage("§cInvalid key entered");
                        }
                    } else {
                        player.sendMessage("§c2FA is already set up, `/2fa <code>` to authenticate yourself");
                    }
                } else {
                    player.sendMessage("§c2FA is disabled");
                }
            } else if (args[0].equalsIgnoreCase("reset")) {
                if (!(sender instanceof Player)) {
                    String targetName = args[1];
                    String realTargetName = Database.connection.select("name").from("playersdata").where("name='" + targetName + "'").getString();
                    if (realTargetName == null) {
                        sender.sendMessage("§cThe player you entered does not exist.");
                        return true;
                    }
                    int id = Database.connection.select("id").from("playersdata").where("name='" + targetName + "'").getInt();
                    User user = User.getUser(id);
                    AuthHandler.setTrustedSessions(user, false);
                    sender.sendMessage("§aSuccessfully reset " + realTargetName + " trusted sessions");
                } else {
                    sender.sendMessage("§cThis command is console only.");
                }
            }
        }
        return true;
    }

    private void removeMap(Player player) {
        player.getInventory().remove(Material.MAP);
    }

    private void usage(CommandSender sender) {
        sender.sendMessage("§6[2FA Commands]\n" +
                "/2fa <code> - login\n" +
                "/2fa setup <code> - setup your 2FA\n" +
                "/2fa reset - Reset your trusted sessions");
    }

    private boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }
}

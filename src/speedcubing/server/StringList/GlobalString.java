package speedcubing.server.StringList;

import speedcubing.server.config;

public class GlobalString {

    public static String[] NoEnoughServers = {
            "§cTheres no enough servers!",
            "§cTheres no enough servers!",
            "§cTheres no enough servers!",
            "§cTheres no enough servers!"
    };

    public static String[] UnSavedSettings = {
            "§cInventareinstellungen verwerfen.",
            "§cDiscard Inventory Settings.",
            "§c取消物品欄設定",
            "§c取消物品栏设置"
    };

    public static final String[][] MessageFormat = {{
            "%text% §8» %text2%",
            "%text% §8» %text2%",
            "%text% §8» %text2%",
            "%text% §8» %text2%"}
    };
    public static String[] category = {
            "§eClick to see the category!",
            "§eClick to see the category!",
            "§eClick to see the category!",
            "§eClick to see the category!"
    };

    public static String[] selected = {
            "§aSelected!",
            "§aSelected!",
            "§aSelected!",
            "§aSelected!"
    };

    public static String[] invalidName = {
            "§cInvalid username!",
            "§cInvalid username!",
            "§cInvalid username!",
            "§cInvalid username!"
    };
    public static String[] SavedSettings = {
            "§asaved settings.",
            "§aSaved Settings.",
            "§a設定已儲存",
            "§a设置已储存"
    };

    public static String[][] LobbyTabList = {
            {
                    "§6" + config.SERVERIP,
                    "§6" + config.SERVERIP,
                    "§6" + config.SERVERIP,
                    "§6" + config.SERVERIP},
            {
                    "§aOnline-Spieler §8- §b%int%",
                    "§aOnline players §8- §b%int%",
                    "§a線上玩家 §8- §b%int%",
                    "§a线上玩家 §8- §b%int%"}
    };

    public static String[] min = {
            "§cThe minimum value is %int%",
            "§cThe minimum value is %int%",
            "§cThe minimum value is %int%",
            "§cThe minimum value is %int%"
    };

    public static String[] max = {
            "§cThe maximum value is %int%",
            "§cThe maximum value is %int%",
            "§cThe maximum value is %int%",
            "§cThe maximum value is %int%"
    };

    public static String[] inputInteger = {
            "§aPlease input a valid number (%min% ~ %max%)",
            "§aPlease input a valid number (%min% ~ %max%)",
            "§aPlease input a valid number (%min% ~ %max%)",
            "§aPlease input a valid number (%min% ~ %max%)"
    };

    public static String[] integerOutOfRange = {
            "§cThe number is out of range! (%min% ~ %max%)",
            "§cThe number is out of range! (%min% ~ %max%)",
            "§cThe number is out of range! (%min% ~ %max%)",
            "§cThe number is out of range! (%min% ~ %max%)"
    };

    public static String[] invalidInteger = {
            "§cInvalid integer format.",
            "§cInvalid integer format.",
            "§cInvalid integer format.",
            "§cInvalid integer format."
    };

    public static String[] nicksameusername = {
            "§cYou can't nick with the same username!",
            "§cYou can't nick with the same username!",
            "§cYou can't nick with the same username!",
            "§cYou can't nick with the same username!"
    };

    public static String[] nicknotavaliable = {
            "§cThis nickname is not avaliable.",
            "§cThis nickname is not avaliable.",
            "§cThis nickname is not avaliable.",
            "§cThis nickname is not avaliable."
    };
    public static String[] discord = {
            "§bOfficial discord server link: §ahttps://discord.gg/rnbRWrb",
            "§bOfficial discord server link: §ahttps://discord.gg/rnbRWrb",
            "§bOfficial discord server link: §ahttps://discord.gg/rnbRWrb",
            "§bOfficial discord server link: §ahttps://discord.gg/rnbRWrb"
    };

    public static String[] FlyEnable = {
            "enabled", "enabled", "enabled", "enabled"
    };
    public static String[] FlyDisable = {
            "disabled", "disabled", "disabled", "disabled"
    };
    public static String[] AlreadyInHub = {
            "§cDu befindest dich bereits in der hub.",
            "§cYou are already in hub.",
            "§c你已經在主大廳了",
            "§c你已经在主大厅了"};
    public static String[] OnlyInHub = {
            "§cYou can only do this in lobby.",
            "§cYou can only do this in lobby.",
            "§cYou can only do this in lobby.",
            "§cYou can only do this in lobby."};

    public static String[] UnknownCommand = {
            "§cUnbekannter Befehl. Gib /help ein, um eine Liste von Befehlen zu erhalten",
            "§cUnknown command. Try /help for a list of commands.",
            "§c未知的指令。請嘗試 /help 來顯示指令列表。",
            "§c未知指令。请使用/help來顯示指令列表。"};
    public static String[] NoPermCommand = {
            "§cDu hast nicht die benötigte Berechtigung, diesen Befehl auszuführen.",
            "§cYou do not have permission to use this command.",
            "§c你沒有權限使用此指令",
            "§c您没有使用此命令的权限"
    };
}

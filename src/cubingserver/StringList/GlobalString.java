package cubingserver.StringList;

import cubingserver.configcache;

public class GlobalString {

    public static String[] discord = {
            "§bOfficial discord server link: §ahttps://discord.gg/rnbRWrb",
            "§bOfficial discord server link: §ahttps://discord.gg/rnbRWrb",
            "§bOfficial discord server link: §ahttps://discord.gg/rnbRWrb",
            "§bOfficial discord server link: §ahttps://discord.gg/rnbRWrb"
    };

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

    public static String[] spam = {
            "§cBlocked excessive spam!",
            "§cBlocked excessive spam!",
            "§cBlocked excessive spam!",
            "§cBlocked excessive spam!"
    };

    public static String[] samemsg = {
            "§cYou cannnot say the same message twice!",
            "§cYou cannnot say the same message twice!",
            "§cYou cannnot say the same message twice!",
            "§cYou cannnot say the same message twice!"
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
                    "§6" + configcache.SERVERIP,
                    "§6" + configcache.SERVERIP,
                    "§6" + configcache.SERVERIP,
                    "§6" + configcache.SERVERIP},
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
}

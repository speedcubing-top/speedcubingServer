package top.speedcubing.server.lang;

import top.speedcubing.server.lang.LangMessage;

public class GlobalString {
    public static LangMessage LobbyJoinMessage = new LangMessage(
            "%1% has joined the Lobby!",
            "%1% has joined the Lobby!",
            "%1% has joined the Lobby!",
            "%1% has joined the Lobby!");

    public static LangMessage NoEnoughServers = new LangMessage(
            "§cTheres no enough servers!",
            "§cTheres no enough servers!",
            "§cTheres no enough servers!",
            "§cTheres no enough servers!"
    );

    public static LangMessage UnSavedSettings = new LangMessage(
            "§cInventareinstellungen verwerfen.",
            "§cDiscard Inventory Settings.",
            "§c取消物品欄設定",
            "§c取消物品栏设置"
    );

    public static final LangMessage MessageFormat = new LangMessage(
            "%1% §8» %2%%3%",
            "%1% §8» %2%%3%",
            "%1% §8» %2%%3%",
            "%1% §8» %2%%3%"
    );
    public static LangMessage category = new LangMessage(
            "§eClick to see the category!",
            "§eClick to see the category!",
            "§eClick to see the category!",
            "§eClick to see the category!"
    );

    public static LangMessage selected = new LangMessage(
            "§aSelected!",
            "§aSelected!",
            "§aSelected!",
            "§aSelected!"
    );

    public static LangMessage invalidName = new LangMessage(
            "§cInvalid username!",
            "§cInvalid username!",
            "§cInvalid username!",
            "§cInvalid username!"
    );
    public static LangMessage SavedSettings = new LangMessage(
            "§asaved settings.",
            "§aSaved Settings.",
            "§a設定已儲存",
            "§a设置已储存"
    );

    public static LangMessage min = new LangMessage(
            "§cThe minimum value is %1%",
            "§cThe minimum value is %1%",
            "§cThe minimum value is %1%",
            "§cThe minimum value is %1%"
    );

    public static LangMessage max = new LangMessage(
            "§cThe maximum value is %1%",
            "§cThe maximum value is %1%",
            "§cThe maximum value is %1%",
            "§cThe maximum value is %1%"
    );

    public static LangMessage inputInteger = new LangMessage(
            "§aPlease input a valid number (%1% ~ %2%)",
            "§aPlease input a valid number (%1% ~ %2%)",
            "§aPlease input a valid number (%1% ~ %2%)",
            "§aPlease input a valid number (%1% ~ %2%)"
    );

    public static LangMessage integerOutOfRange = new LangMessage(
            "§cThe number is out of range! (%1% ~ %2%)",
            "§cThe number is out of range! (%1% ~ %2%)",
            "§cThe number is out of range! (%1% ~ %2%)",
            "§cThe number is out of range! (%1% ~ %2%)"
    );

    public static LangMessage invalidNumber = new LangMessage(
            "§cInvalid number format.",
            "§cInvalid number format.",
            "§cInvalid number format.",
            "§cInvalid number format."
    );

    public static LangMessage nicksameusername = new LangMessage(
            "§cYou can't nick with the same username!",
            "§cYou can't nick with the same username!",
            "§cYou can't nick with the same username!",
            "§cYou can't nick with the same username!"
    );

    public static LangMessage nickdefaultusername = new LangMessage(
            "§cIf you want to unnick please use /unnick",
            "§cIf you want to unnick please use /unnick",
            "§cIf you want to unnick please use /unnick",
            "§cIf you want to unnick please use /unnick"
    );

    public static LangMessage notNicked = new LangMessage(
            "§cYou are not nicked!",
            "§cYou are not nicked!",
            "§cYou are not nicked!",
            "§cYou are not nicked!"
    );

    public static LangMessage unknownRank = new LangMessage("unknown rank", "unknown rank", "unknown rank", "unknown rank"
    );

    public static LangMessage alreadyNicked = new LangMessage(
            "§cYou are already nicked!",
            "§cYou are already nicked!",
            "§cYou are already nicked!",
            "§cYou are already nicked!"
    );

    public static LangMessage nicknotavaliable = new LangMessage(
            "§cThis nickname is not avaliable.",
            "§cThis nickname is not avaliable.",
            "§cThis nickname is not avaliable.",
            "§cThis nickname is not avaliable."
    );
    public static LangMessage discord = new LangMessage(
            "§bOfficial discord server link: §aspeedcubing.top/discord",
            "§bOfficial discord server link: §aspeedcubing.top/discord",
            "§bOfficial discord server link: §aspeedcubing.top/discord",
            "§bOfficial discord server link: §aspeedcubing.top/discord"
    );

    public static LangMessage FlyEnable = new LangMessage(
            "enabled", "enabled", "enabled", "enabled"
    );
    public static LangMessage FlyDisable = new LangMessage(
            "disabled", "disabled", "disabled", "disabled"
    );
    public static LangMessage OnlyInHub = new LangMessage(
            "§cYou can only do this in lobby.",
            "§cYou can only do this in lobby.",
            "§cYou can only do this in lobby.",
            "§cYou can only do this in lobby.");

    public static LangMessage UnknownCommand = new LangMessage(
            "§cUnbekannter Befehl. Gib /help ein, um eine Liste von Befehlen zu erhalten",
            "§cUnknown command. Try /help for a list of commands.",
            "§c未知的指令。請嘗試 /help 來顯示指令列表。",
            "§c未知指令。请使用/help來顯示指令列表。");
    public static LangMessage NoPermCommand = new LangMessage(
            "§cDu hast nicht die benötigte Berechtigung, diesen Befehl auszuführen.",
            "§cYou do not have permission to use this command.",
            "§c你沒有權限使用此指令",
            "§c您没有使用此命令的权限"
    );

    public static LangMessage CantSetItemhere = new LangMessage(
            "§4Sie können die Elemente hier nicht einstellen!",
            "§4You can't set the items here!",
            "§4你不能將物品設定於此處!",
            "§4你不能将物品设定于此处!");
}

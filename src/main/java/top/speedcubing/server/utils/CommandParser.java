package top.speedcubing.server.utils;


import java.util.Arrays;

public class CommandParser {
    public final String command;
    public final String[] args;

    public static CommandParser parse(String fullCommand) {
        return new CommandParser(fullCommand);
    }

    private CommandParser(String fullcommand) {
        String[] split = fullcommand.split(" ");
        this.command = split[0].substring(1);
        this.args = Arrays.copyOfRange(split, 1, split.length);
    }
}
package org.tynes.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.utils.TextFormat;
import org.tynes.ExtraUtils;
import org.tynes.downloader.Downloader;

import java.io.IOException;

public class MainCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!command.getName().equalsIgnoreCase("extrautils")) {
            return true;
        }

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("download")) {
                if (!(commandSender instanceof ConsoleCommandSender)) {
                    commandSender.sendMessage(TextFormat.colorize("&cYou need to execute this from console"));
                    return true;
                }
                if (args[1] == null) {
                    commandSender.sendMessage(TextFormat.colorize("&cYou need to write the url in arg-1."));
                    return true;
                }
                if (args[2] == null) {
                    commandSender.sendMessage(TextFormat.colorize("&cYou need to write the filename in arg-2."));
                    return true;
                }

                try {
                    Downloader.downloadFile(args[1], ExtraUtils.libsFolder().getAbsolutePath(), args[2]);
                    commandSender.sendMessage(TextFormat.colorize("&aFile " + args[2] + " Downloaded."));
                } catch (IOException e) {
                    commandSender.sendMessage(TextFormat.colorize("&cError occur when downloading " + args[2] + " library."));
                    e.printStackTrace();
                }

            } else if (args[0].equalsIgnoreCase("reload")) {
                double timesToReload = System.currentTimeMillis();
                ExtraUtils.getInstance().saveConfig();
                ExtraUtils.getInstance().loadLibraries();
                timesToReload = System.currentTimeMillis() - timesToReload;
                commandSender.sendMessage(TextFormat.colorize("&aPlugin Reloaded within " + timesToReload + "ms."));
            }
        } else {
            commandSender.sendMessage(TextFormat.colorize("&71. &e/ext download <url> <filename>"));
            commandSender.sendMessage(TextFormat.colorize("&72. &e/ext reload"));
        }
        return true;
    }
}

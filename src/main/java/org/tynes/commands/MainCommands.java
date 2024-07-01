package org.tynes.commands;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandExecutor;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.utils.TextFormat;
import org.tynes.ExtraUtils;
import org.tynes.downloader.Downloader;
import org.tynes.plugin.PluginController;

import java.io.IOException;

public class MainCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!command.getName().equalsIgnoreCase("extrautils")) {
            return true;
        }

        if (args.length > 0) {
            Server server = ExtraUtils.getInstance().getServer();
            if (args[0].equalsIgnoreCase("download")) {
                if (args.length >= 3) {
                    if (!(commandSender instanceof ConsoleCommandSender)) {
                        commandSender.sendMessage(TextFormat.colorize("&cYou need to execute this from console"));
                        return true;
                    }
                    try {
                        Downloader.downloadFile(args[1], ExtraUtils.libsFolder().getAbsolutePath(), args[2]);
                        commandSender.sendMessage(TextFormat.colorize("&aFile " + args[2] + " Downloaded."));
                    } catch (IOException e) {
                        commandSender.sendMessage(TextFormat.colorize("&cError occur when downloading " + args[2] + " library."));
                        e.printStackTrace();
                    }
                } else {
                    commandSender.sendMessage(TextFormat.colorize("&cYou need to specify the url and file name."));
                }

            } else if (args[0].equalsIgnoreCase("reload")) {
                double timesToReload = System.currentTimeMillis();
                ExtraUtils.getInstance().saveConfig();
                ExtraUtils.getInstance().loadLibraries();
                timesToReload = System.currentTimeMillis() - timesToReload;
                commandSender.sendMessage(TextFormat.colorize("&aPlugin Reloaded within " + timesToReload + "ms."));
            } else if (args[0].equalsIgnoreCase("plugin")) {

                PluginController pc = ExtraUtils.pluginController();
                PluginManager pm = server.getPluginManager();

                if (args.length >= 3) {

                    if (args[1].equalsIgnoreCase("enable")) {

                        String pluginFileName = args[2];
                        if (pc.loadPlugin(pluginFileName)) {
                            String pluginName = pluginFileName.contains("-") ?
                                    pluginFileName.substring(0, pluginFileName.indexOf("-")) : pluginFileName.replace(".jar", "");

                            Plugin plugin = server.getPluginManager().getPlugin(pluginName);

                            if (plugin == null) {
                                commandSender.sendMessage(TextFormat.colorize("&cThe plugin " + pluginName + " does not exist or is not loaded."));
                                return true;
                            }

                            if (!plugin.isEnabled()) {
                                server.getPluginManager().enablePlugin(plugin);
                                commandSender.sendMessage(TextFormat.colorize("&aThe plugin " + pluginName + " has been enabled."));
                                return true;
                            } else if (plugin.isEnabled()) {
                                commandSender.sendMessage(TextFormat.colorize("&cThe plugin " + pluginName + " is already enabled."));
                            }
                        } else {
                            commandSender.sendMessage(TextFormat.colorize("&cFailed to load the plugin " + pluginFileName + ". Try to load it first."));
                        }

                    } else if (args[1].equalsIgnoreCase("disable")) {

                        String pluginName = args[2];
                        Plugin plugin = server.getPluginManager().getPlugin(pluginName);

                        if (plugin == null) {
                            commandSender.sendMessage(TextFormat.colorize("&cThe plugin " + pluginName + " does not exist or is not loaded."));
                            return true;
                        }

                        if (plugin.isEnabled()) {
                            server.getPluginManager().disablePlugin(plugin);
                            commandSender.sendMessage(TextFormat.colorize("&aThe plugin " + pluginName + " has been disabled."));
                        } else {
                            commandSender.sendMessage(TextFormat.colorize("&cThe plugin " + pluginName + " is already disabled."));
                        }

                    } else if (args[1].equalsIgnoreCase("load")) {

                        String pluginFileName = args[2] + ".jar";
                        if (pc.loadPlugin(pluginFileName)) {
                            commandSender.sendMessage(TextFormat.colorize("&aThe plugin " + pluginFileName + " has been loaded."));
                        } else {
                            commandSender.sendMessage(TextFormat.colorize("&cFailed to load the plugin " + pluginFileName + "."));
                        }

                    } else if (args[1].equalsIgnoreCase("unload")) {

                        String pluginName = args[2];
                        if (pc.unloadPlugin(pluginName)) {
                            commandSender.sendMessage(TextFormat.colorize("&aThe plugin " + pluginName + " has been unloaded."));
                        } else {
                            commandSender.sendMessage(TextFormat.colorize("&cFailed to unload the plugin " + pluginName + "."));
                        }
                    }
                } else {
                    commandSender.sendMessage(TextFormat.colorize("&cYou need to specify the command and plugin name."));
                }

            }
        } else {
            commandSender.sendMessage(TextFormat.colorize("&71. &e/ext download <url> <filename>"));
            commandSender.sendMessage(TextFormat.colorize("&72. &e/ext reload"));
        }
        return true;
    }
}

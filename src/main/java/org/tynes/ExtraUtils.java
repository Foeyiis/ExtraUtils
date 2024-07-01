package org.tynes;

import cn.nukkit.command.PluginCommand;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import org.tynes.commands.MainCommands;
import org.tynes.downloader.Downloader;
import org.tynes.exception.JarFileLoaderException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class ExtraUtils extends PluginBase {

    private static ExtraUtils extraUtils;

    private static File libsFolder;

    public static File libsFolder() {
        return libsFolder;
    }

    public static ExtraUtils getInstance() {
        return extraUtils;
    }

    @Override
    public void onEnable() {
        // Called when plugin is enabled. This is called after all plugins have been LOADED
        // If your plugin has public API that could be accessed by other plugins, you will want to
        // make sure to initialize that in onLoad instead of here

        double timesLoad = System.currentTimeMillis();

        extraUtils = this;

        libsFolder = new File(getDataFolder().getAbsoluteFile() + "/libraries");

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        saveDefaultConfig();

        if (!libsFolder.exists()) {
            libsFolder.mkdir();
        }

        preloadLibraries();
        customDownloadSet();
        loadLibraries();
        registerCommands();

        timesLoad = System.currentTimeMillis() - timesLoad;
        getLogger().info(TextFormat.colorize("&aPlugin enabled within " + timesLoad + "ms."));


    }

    @Override
    public void onDisable() {
        // Called when plugin is disabled. This would be done by the server when it shuts down
        // so this is a good idea to save any persistant data you need.
        // May also be called if an exception is called during loading/enabling of your plugin

        extraUtils = null;

    }

    public void registerCommands() {
        ((PluginCommand<?>) getCommand("extrautils")).setExecutor(new MainCommands());
    }

    public void customDownloadSet() {
        for (String key : getConfig().getSection("custom-download").keySet()) {
            String readMethod = getConfig().getString("custom-download." + key + ".read-method");
            String className = getConfig().getString("custom-download." + key + ".class-name");
            String dataURL = getConfig().getString("custom-download." + key + ".url");
            String fileName = getConfig().getString("custom-download." + key + ".file-name");
            File[] listFiles = libsFolder.listFiles();

            if (readMethod != null && !readMethod.isEmpty()
                    && className != null && !className.isEmpty()
                    && dataURL != null && !dataURL.isEmpty()
                    && fileName != null && !fileName.isEmpty()) {
                try {
                    if (readMethod.equalsIgnoreCase("class_check")) {
                        Class.forName(className);
                    } else if (readMethod.equalsIgnoreCase("file_check")) {
                        if (listFiles == null) {
                            return;
                        }
                        if (!Arrays.asList(listFiles).contains(new File(libsFolder, fileName))) {
                            throw new ClassNotFoundException("");
                        }
                    }
                } catch (ClassNotFoundException e) {
                    try {
                        getLogger().info("Downloading " + key);
                        Downloader.downloadFile(dataURL, String.valueOf(libsFolder), fileName);
                        getLogger().info("§aDownload complete for " + key);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                        getLogger().error("An error occurred while downloading the " + key + " file(URL Is Not Valid?): " + ex.getMessage());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        getLogger().error("An error occurred while downloading the " + key + " file: " + ex.getMessage());
                    }
                }
            } else {
                getLogger().error("Something is not written correctly on " + key + ". Skipping Download.");
            }
        }
    }

    public void loadLibraries() {
        File[] listFiles = libsFolder.listFiles();

        if (listFiles == null)
            return;

        HashMap<String, String> errorMsg = new HashMap<>();

        getLogger().info("Loaded Libraries:");
        for (File file : listFiles) {
            try {
                Downloader.addJarToClasspath(file.getAbsolutePath());
                getLogger().info(TextFormat.colorize("&7  - &f" + file.getName().replace(".jar", "")));
            } catch (JarFileLoaderException ex) {
                errorMsg.put(file.getName(), ex.getMessage());
            }
        }
        for (String s : errorMsg.keySet()) {
            getLogger().error("An error occurred while load the '" + s + "' libraries: " + errorMsg.get(s));
        }
    }

    public void preloadLibraries() {
        File[] listFiles = libsFolder.listFiles();

        if (listFiles == null)
            return;

        getLogger().info("Preloading Libraries..");

        for (File file : listFiles) {
            try {
                Downloader.addJarToClasspath(file.getAbsolutePath());
            } catch (JarFileLoaderException ex) {
                ex.printStackTrace();
                getLogger().error("Something went wrong when trying to preload libraries.");
            }
        }

        getLogger().info("Preload Libraries Successful.");
    }

}
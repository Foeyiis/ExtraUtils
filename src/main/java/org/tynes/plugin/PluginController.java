package org.tynes.plugin;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginDescription;
import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.plugin.PluginManager;
import org.tynes.ExtraUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

public class PluginController {

    private final PluginManager pluginManager;
    private final PluginLogger logger = ExtraUtils.getInstance().getLogger();

    public PluginController(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public boolean loadPlugin(String pluginFileName) {
        File pluginFile = new File("plugins", pluginFileName);

        if (!pluginFile.exists()) {
            return false;
        }

        try {
            pluginManager.loadPlugin(pluginFile);
            Plugin plugin = null;

            if (pluginFileName.contains("-")) {
                String pluginName = pluginFileName.substring(0, pluginFileName.indexOf("-"));
                plugin = pluginManager.getPlugin(pluginName);
            }

            if (plugin == null) {
                String pluginName = pluginFileName.replace(".jar", "");
                plugin = pluginManager.getPlugin(pluginName);
            }

            if (plugin != null) {
                pluginManager.enablePlugin(plugin);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean unloadPlugin(String pluginName) {
        Plugin plugin = pluginManager.getPlugin(pluginName);

        if (plugin == null) {
            return false;
        }

        pluginManager.disablePlugin(plugin);

        try {
            Field field = pluginManager.getClass().getDeclaredField("plugins");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Plugin> plugins = (Map<String, Plugin>) field.get(pluginManager);
            plugins.remove(pluginName);

            field = pluginManager.getClass().getDeclaredField("names");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, PluginDescription> names = (Map<String, PluginDescription>) field.get(pluginManager);
            names.remove(pluginName);

            field = pluginManager.getClass().getDeclaredField("pluginLogger");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, PluginLogger> loggers = (Map<String, PluginLogger>) field.get(pluginManager);
            loggers.remove(pluginName);

            return true;
        } catch (NoSuchFieldException e) {
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}


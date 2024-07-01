package org.tynes.customconfig;

import cn.nukkit.utils.ConfigSection;
import org.tynes.ExtraUtils;

public class ConfigFile {

    public static ConfigSection customDownloadFile() {
        return ExtraUtils.getInstance().getConfig().getSection("custom-download");
    }

}

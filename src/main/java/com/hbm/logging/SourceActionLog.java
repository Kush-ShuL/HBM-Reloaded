package com.hbm.logging;

import com.hbm.HbmReloaded;
import com.hbm.config.GeneralModes;

/** Original GeneralConfig.enableExtendedLogging calls share the actual common setting and mod logger. */
public final class SourceActionLog {
    private SourceActionLog() { }

    public static boolean enabled() {
        return GeneralModes.extendedLogging();
    }

    public static void info(String message, Object... arguments) {
        if (enabled()) HbmReloaded.LOGGER.info(message, arguments);
    }
}

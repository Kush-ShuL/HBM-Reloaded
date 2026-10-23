package com.hbm;

import java.util.Random;

/** X5846's startup-wide Polaroid selection, shared by lore and later consumers. */
public final class LegacyStartup {
    public static int generalOverride;
    public static int polaroidId = 1;
    public static long startupTime;
    private static boolean initialized;

    private LegacyStartup() {}

    public static void initialize() {
        if (initialized) return;
        initialized = true;
        startupTime = System.currentTimeMillis();
        if (generalOverride > 0 && generalOverride < 19) {
            polaroidId = generalOverride;
        } else {
            Random random = new Random();
            do { polaroidId = random.nextInt(18) + 1; } while (polaroidId == 4 || polaroidId == 9);
        }
    }
}

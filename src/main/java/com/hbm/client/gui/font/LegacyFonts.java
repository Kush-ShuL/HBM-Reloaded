package com.hbm.client.gui.font;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

/** Shared text entry point: measurement, wrapping and drawing use the player's active font. */
public final class LegacyFonts {
    private LegacyFonts() { }

    public static Font get() {
        return Minecraft.getInstance().font;
    }
}

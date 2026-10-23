package com.hbm.compat.jade;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import net.minecraft.network.chat.Component;
import snownee.jade.api.ITooltip;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;

/** Shared overlay bars; division never truncates or overflows native arbitrary-precision HE. */
public final class HbmJadeBars {
    private HbmJadeBars() {}

    public static void add(ITooltip tooltip, double value, double maximum, Component text, int color) {
        float ratio = maximum > 0 && Double.isFinite(value) && Double.isFinite(maximum) ? (float)Math.clamp(value / maximum, 0D, 1D) : 0F;
        add(tooltip, ratio, text, color);
    }

    public static void add(ITooltip tooltip, BigInteger value, BigInteger maximum, Component text, int color) {
        float ratio = maximum.signum() > 0 ? new BigDecimal(value.max(BigInteger.ZERO).min(maximum))
                .divide(new BigDecimal(maximum), MathContext.DECIMAL32).floatValue() : 0F;
        add(tooltip, ratio, text, color);
    }

    private static void add(ITooltip tooltip, float ratio, Component text, int color) {
        var helper = IElementHelper.get();
        tooltip.add(helper.progress(ratio, text, helper.progressStyle().color(0xFF000000 | color).textColor(0xFFFFFFFF), BoxStyle.getNestedBox(), true));
    }
}

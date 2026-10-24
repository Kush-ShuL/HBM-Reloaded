package com.hbm.item.dye;

import java.util.Locale;

/** 固定颜色、原始色值及颜色顺序。 */
public enum ChemicalDyeType {
    BLACK(1973019), RED(11743532), GREEN(3887386), BROWN(5320730),
    BLUE(2437522), PURPLE(8073150), CYAN(2651799), SILVER(11250603),
    GRAY(4408131), PINK(14188952), LIME(4312372), YELLOW(14602026),
    LIGHTBLUE(6719955), MAGENTA(12801229), ORANGE(15435844), WHITE(15790320);

    public final int color;

    ChemicalDyeType(int color) { this.color = color; }

    public String serializedName() {
        return switch (this) {
            case SILVER -> "light_gray";
            case LIGHTBLUE -> "light_blue";
            default -> name().toLowerCase(Locale.ROOT);
        };
    }

    public String translationSuffix() { return name().toLowerCase(Locale.ROOT); }
}

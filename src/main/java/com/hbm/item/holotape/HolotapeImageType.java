package com.hbm.item.holotape;
import java.util.Locale;import net.minecraft.ChatFormatting;import net.minecraft.network.chat.Component;
/** Complete original ordered metadata enum. Text is localized without changing the source labels/colours. */
public enum HolotapeImageType {
    HOLO_DIGAMMA(ChatFormatting.RED,"crimson","D#"),
    HOLO_RESTORED(ChatFormatting.RED,"crimson","D0"),
    HOLO_FE_HALL(ChatFormatting.GREEN,"lime","001-HALL"),
    HOLO_FE_CORRIDOR(ChatFormatting.GREEN,"lime","002-CORRIDOR"),
    HOLO_FE_SERVER(ChatFormatting.GREEN,"lime","003-SERVER"),
    HOLO_FEH_DOME(ChatFormatting.RED,"red","011-DOME"),
    HOLO_FEH_BOAT(ChatFormatting.RED,"red","012-BOAT"),
    HOLO_FEH_LSC(ChatFormatting.RED,"red","013-LAUNCH"),
    HOLO_F3_RC(ChatFormatting.DARK_GREEN,"green","021-RIVET"),
    HOLO_F3_IV(ChatFormatting.DARK_GREEN,"green","022-V87"),
    HOLO_F3_WM(ChatFormatting.DARK_GREEN,"green","023-MONUMENT"),
    HOLO_NV_CRATER(ChatFormatting.GOLD,"brown","031-MOUNTAIN"),
    HOLO_NV_DIVIDE(ChatFormatting.GOLD,"brown","032-ROAD"),
    HOLO_NV_BM(ChatFormatting.GOLD,"brown","033-BROADCAST"),
    HOLO_O_1(ChatFormatting.WHITE,"chroma","X00-TRANSCRIPT"),
    HOLO_O_2(ChatFormatting.WHITE,"chroma","X01-NEWS"),
    HOLO_O_3(ChatFormatting.WHITE,"chroma","X02-FICTION"),
    HOLO_CHALLENGE(ChatFormatting.GRAY,"none","-");
    public final ChatFormatting colorCode;public final String colorName,label;
    HolotapeImageType(ChatFormatting code,String color,String label){colorCode=code;colorName=color;this.label=label;}
    public static HolotapeImageType from(int metadata){var values=values();return values[Math.abs(metadata%values.length)];}
    public String serializedName(){return name().toLowerCase(Locale.ROOT);}
    public Component text(){return Component.translatable("holotape.hbm."+serializedName()+".text");}
}

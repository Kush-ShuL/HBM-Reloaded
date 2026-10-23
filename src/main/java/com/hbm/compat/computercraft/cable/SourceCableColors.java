package com.hbm.compat.computercraft.cable;
import com.hbm.registry.HbmOreDictionary;
import net.minecraft.world.item.ItemStack;
/** Original finite OCColors data only; no OC API/runtime or replacement colour-network implementation. */
public enum SourceCableColors {
    BLACK(0x444444,"dyeBlack"),RED(0xB3312C,"dyeRed"),GREEN(0x339911,"dyeGreen"),BROWN(0x51301A,"dyeBrown"),BLUE(0x6666FF,"dyeBlue"),PURPLE(0x7B2FBE,"dyePurple"),CYAN(0x66FFFF,"dyeCyan"),LIGHTGRAY(0xABABAB,"dyeLightGray"),GRAY(0x666666,"dyeGray"),PINK(0xD88198,"dyePink"),LIME(0x66FF66,"dyeLime"),YELLOW(0xFFFF66,"dyeYellow"),LIGHTBLUE(0xAAAAFF,"dyeLightBlue"),MAGENTA(0xC354CD,"dyeMagenta"),ORANGE(0xEB8844,"dyeOrange"),WHITE(0xF0F0F0,"dyeWhite"),NONE(0,"");
    public final int rgb;private final String dictionary;
    SourceCableColors(int rgb,String dictionary){this.rgb=rgb;this.dictionary=dictionary;}
    public static SourceCableColors fromInt(int rgb){for(var value:values())if(value.rgb==rgb)return value;return NONE;}
    public static SourceCableColors fromDye(ItemStack stack){for(var name:HbmOreDictionary.names(stack))if(name.length()>3&&name.startsWith("dye"))for(var color:values())if(name.equals(color.dictionary))return color;return NONE;}
}

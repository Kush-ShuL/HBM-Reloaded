package com.hbm.item.dye;
import java.util.Locale;
/** Source EnumChemDye metadata order, pre-1.12 RGB values and exact OreDictionary names. */
public enum ChemicalDyeType {
 BLACK(1973019,"Black"),RED(11743532,"Red"),GREEN(3887386,"Green"),BROWN(5320730,"Brown"),BLUE(2437522,"Blue"),PURPLE(8073150,"Purple"),CYAN(2651799,"Cyan"),SILVER(11250603,"LightGray"),GRAY(4408131,"Gray"),PINK(14188952,"Pink"),LIME(4312372,"Lime"),YELLOW(14602026,"Yellow"),LIGHTBLUE(6719955,"LightBlue"),MAGENTA(12801229,"Magenta"),ORANGE(15435844,"Orange"),WHITE(15790320,"White");
 public final int color;public final String dictName;
 ChemicalDyeType(int color,String name){this.color=color;dictName=name;}
 public String serializedName(){return name().toLowerCase(Locale.ROOT);}
 public static ChemicalDyeType from(int metadata){return values()[Math.abs(metadata%values().length)];}
}

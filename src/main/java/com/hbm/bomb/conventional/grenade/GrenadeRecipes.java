package com.hbm.bomb.conventional.grenade;
import com.hbm.hazard.HbmHazards;
import java.util.*;
import net.minecraft.world.item.*;
/** Original GrenadeRecipeHandler enumerates every valid recipe, including exact metadata and optional extras. */
public final class GrenadeRecipes {
 private GrenadeRecipes(){}
 public record Assembly(List<ItemStack> inputs,ItemStack output){}
 public static ItemStack part(Item item,Enum<?> value){var stack=new ItemStack(item);stack.set(HbmHazards.LEGACY_METADATA.get(),value.ordinal());return stack;}
 public static List<Assembly> all(){var out=new ArrayList<Assembly>();for(var shell:GrenadeParts.Shell.values())for(var filling:GrenadeParts.Filling.values())if(filling.compatibleShells.contains(shell))for(var fuze:GrenadeParts.Fuze.values()){out.add(make(shell,filling,fuze,null));for(var extra:GrenadeParts.Extra.values())out.add(make(shell,filling,fuze,extra));}return out;}
 private static Assembly make(GrenadeParts.Shell shell,GrenadeParts.Filling filling,GrenadeParts.Fuze fuze,GrenadeParts.Extra extra){var inputs=new ArrayList<ItemStack>();inputs.add(part(GrenadeContent.SHELL.get(),shell));inputs.add(part(GrenadeContent.FILLING.get(),filling));inputs.add(part(GrenadeContent.FUZE.get(),fuze));if(extra!=null)inputs.add(part(GrenadeContent.EXTRA.get(),extra));return new Assembly(List.copyOf(inputs),UniversalGrenadeItem.make(shell,filling,fuze,extra,1));}
 public static long key(ItemStack stack){return(((UniversalGrenadeItem.shell(stack).ordinal()*13L+UniversalGrenadeItem.filling(stack).ordinal())*5+UniversalGrenadeItem.fuze(stack).ordinal())*5+(UniversalGrenadeItem.extra(stack)==null?0:UniversalGrenadeItem.extra(stack).ordinal()+1));}
}

package com.hbm.bomb.conventional.grenade;
import java.util.*;
import net.minecraft.world.item.*;
/** Original GrenadeRecipeHandler enumerates every valid assembly and optional extra. */
public final class GrenadeRecipes {
 private GrenadeRecipes(){}
 public record Assembly(List<ItemStack> inputs,ItemStack output){}
 public static ItemStack part(Enum<?> value){
  Item item=switch(value){case GrenadeParts.Shell shell->GrenadeContent.SHELL.get(shell).get();case GrenadeParts.Filling filling->GrenadeContent.FILLING.get(filling).get();case GrenadeParts.Fuze fuze->GrenadeContent.FUZE.get(fuze).get();case GrenadeParts.Extra extra->GrenadeContent.EXTRA.get(extra).get();default->throw new IllegalArgumentException("Not a grenade component: "+value);};return new ItemStack(item);
 }
 public static List<Assembly> all(){var out=new ArrayList<Assembly>();for(var shell:GrenadeParts.Shell.values())for(var filling:GrenadeParts.Filling.values())if(filling.compatibleShells.contains(shell))for(var fuze:GrenadeParts.Fuze.values()){out.add(make(shell,filling,fuze,null));for(var extra:GrenadeParts.Extra.values())out.add(make(shell,filling,fuze,extra));}return out;}
 private static Assembly make(GrenadeParts.Shell shell,GrenadeParts.Filling filling,GrenadeParts.Fuze fuze,GrenadeParts.Extra extra){var inputs=new ArrayList<ItemStack>();inputs.add(part(shell));inputs.add(part(filling));inputs.add(part(fuze));if(extra!=null)inputs.add(part(extra));return new Assembly(List.copyOf(inputs),UniversalGrenadeItem.make(shell,filling,fuze,extra,1));}
 public static long key(ItemStack stack){return(((UniversalGrenadeItem.shell(stack).ordinal()*13L+UniversalGrenadeItem.filling(stack).ordinal())*5+UniversalGrenadeItem.fuze(stack).ordinal())*5+(UniversalGrenadeItem.extra(stack)==null?0:UniversalGrenadeItem.extra(stack).ordinal()+1));}
}

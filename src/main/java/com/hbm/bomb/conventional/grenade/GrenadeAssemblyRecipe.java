package com.hbm.bomb.conventional.grenade;
import com.hbm.item.EnumMultiItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** Original shapeless GrenadeCraftingHandler: one shell/filling/fuze, at most one extra, no foreign or duplicate items. */
public final class GrenadeAssemblyRecipe extends CustomRecipe {
 public GrenadeAssemblyRecipe(CraftingBookCategory category){super(category);}
 private static int[] parts(CraftingInput input){int[] parts={-1,-1,-1,-1};int count=0;for(int i=0;i<input.size();i++){var stack=input.getItem(i);if(stack.isEmpty())continue;if(++count>4)return null;int part=stack.is(GrenadeContent.SHELL.get())?0:stack.is(GrenadeContent.FILLING.get())?1:stack.is(GrenadeContent.FUZE.get())?2:stack.is(GrenadeContent.EXTRA.get())?3:-1;if(part<0||parts[part]>=0)return null;parts[part]=((EnumMultiItem)stack.getItem()).ordinal(stack);}if(parts[0]<0||parts[1]<0||parts[2]<0)return null;return GrenadeParts.Filling.values()[parts[1]].compatibleShells.contains(GrenadeParts.Shell.values()[parts[0]])?parts:null;}
 @Override public boolean matches(CraftingInput input,Level level){return parts(input)!=null;}
 @Override public ItemStack assemble(CraftingInput input,HolderLookup.Provider lookup){var p=parts(input);return p==null?ItemStack.EMPTY:UniversalGrenadeItem.make(GrenadeParts.Shell.values()[p[0]],GrenadeParts.Filling.values()[p[1]],GrenadeParts.Fuze.values()[p[2]],p[3]<0?null:GrenadeParts.Extra.values()[p[3]],1);}
 @Override public boolean canCraftInDimensions(int width,int height){return width*height>=3;}
 @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return new ItemStack(GrenadeContent.UNIVERSAL.get());}
 @Override public RecipeSerializer<?> getSerializer(){return GrenadeContent.ASSEMBLY.get();}
}

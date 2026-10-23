package com.hbm.weapon.artillery;
import com.hbm.hazard.HbmHazards;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** Original CargoShellCraftingHandler: exactly one NBT-free empty cargo shell and one no-container item, counts reduced to one each. */
public final class CargoShellRecipe extends CustomRecipe {
    public CargoShellRecipe(CraftingBookCategory category){super(category);}
    private static boolean emptyShell(ItemStack stack){if(!stack.is(ArtilleryAmmoContent.AMMO.get())||ArtilleryAmmoItem.metadata(stack)!=8)return false;for(var entry:stack.getComponentsPatch().entrySet())if(entry.getKey()!=HbmHazards.LEGACY_METADATA.get())return false;return true;}
    private static ItemStack[] parts(CraftingInput input){int count=0,shells=0;ItemStack shell=ItemStack.EMPTY,cargo=ItemStack.EMPTY;for(var stack:input.items()){if(stack.isEmpty())continue;if(stack.hasCraftingRemainingItem())return null;if(++count>2)return null;if(emptyShell(stack)){shells++;shell=stack;}else cargo=stack;}return count==2&&shells==1?new ItemStack[]{shell,cargo}:null;}
    @Override public boolean matches(CraftingInput input,Level level){return parts(input)!=null;}
    @Override public ItemStack assemble(CraftingInput input,HolderLookup.Provider lookup){var parts=parts(input);if(parts==null)return ItemStack.EMPTY;var out=parts[0].copyWithCount(1);ArtilleryAmmoItem.cargo(out,parts[1].copyWithCount(1),lookup);return out;}
    @Override public boolean canCraftInDimensions(int width,int height){return width*height>=2;}
    @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return ArtilleryAmmoItem.make(8,1);}
    @Override public RecipeSerializer<?> getSerializer(){return ArtilleryAmmoContent.CARGO_RECIPE.get();}
}

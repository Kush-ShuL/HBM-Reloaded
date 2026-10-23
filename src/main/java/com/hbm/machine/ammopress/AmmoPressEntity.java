package com.hbm.machine.ammopress;
import com.hbm.hazard.HbmHazards;
import com.hbm.machine.foundry.crucible.ThermalMachine;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
/** Actual source immediate batch production, no invented processing timer, power demand or CC callbacks. */
public final class AmmoPressEntity extends ThermalMachine {
 public enum Animation {LIFTING,PRESSING,RETRACTING,LOWERING}
 public int selectedRecipe=-1,playAnimation;public float prevLift,lift,prevPress,press;public Animation animation=Animation.LIFTING;
 public AmmoPressEntity(BlockPos pos,BlockState state){super(AmmoPressContent.ENTITY.get(),pos,state,10);}
 @Override protected String title(){return "container.machineAmmoPress";}
 public static void tick(Level level,BlockPos pos,BlockState state,AmmoPressEntity machine){if(level instanceof ServerLevel server){if(machine.playAnimation>0)machine.playAnimation--;machine.performRecipe(server);machine.sync(25);}else machine.animate();}
 private static int metadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());}
 private void performRecipe(ServerLevel server){if(selectedRecipe<0)return;var recipes=AmmoPressRecipes.recipes(server.getRecipeManager());if(selectedRecipe<0||selectedRecipe>=recipes.size())return;var recipe=recipes.get(selectedRecipe);var result=recipe.output();while(true){var output=slots.get(9);if(!output.isEmpty()&&(output.getItem()!=result.getItem()||metadata(output)!=metadata(result)||output.getCount()+result.getCount()>output.getMaxStackSize()))return;if(!recipe.accepts(slots::get))return;for(int i=0;i<9;i++){var input=recipe.cell(i);if(input!=null)removeItem(i,input.count());}if(output.isEmpty())slots.set(9,result.copy());else output.grow(result.getCount());playAnimation=40;setChanged();}}
 private void animate(){prevLift=lift;prevPress=press;if(playAnimation>0||lift>0)switch(animation){case LIFTING->{lift+=1F/40F;if(lift>=1){lift=1;animation=Animation.PRESSING;}}case PRESSING->{press+=1F/20F;if(press>=1){press=1;animation=Animation.RETRACTING;}}case RETRACTING->{press-=1F/20F;if(press<=0){press=0;animation=Animation.LOWERING;}}case LOWERING->{lift-=1F/40F;if(lift<=0){lift=0;animation=Animation.LIFTING;}}}}
 public AmmoPressRecipe selected(){if(level==null)return null;var recipes=AmmoPressRecipes.recipes(level.getRecipeManager());return selectedRecipe>=0&&selectedRecipe<recipes.size()?recipes.get(selectedRecipe):null;}
 public void select(int value){selectedRecipe=selectedRecipe==value?-1:value;setChanged();}
 @Override public int[] getSlotsForFace(Direction side){return new int[]{0,1,2,3,4,5,6,7,8,9};}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot==9;}
 @Override public boolean canPlaceItem(int slot,ItemStack stack){if(slot>8||slot<0)return false;var recipe=selected();return recipe!=null&&recipe.cell(slot)!=null&&recipe.cell(slot).matches(stack,true);}
 @Override protected void saveMachine(CompoundTag tag){tag.putInt("recipe",selectedRecipe);}@Override protected void loadMachine(CompoundTag tag){selectedRecipe=tag.getInt("recipe");}
 @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){var tag=super.getUpdateTag(lookup);tag.putInt("playAnimation",playAnimation);return tag;}@Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){super.handleUpdateTag(tag,lookup);playAnimation=tag.getInt("playAnimation");}
 @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new AmmoPressMenu(id,inventory,this,worldPosition);}
}

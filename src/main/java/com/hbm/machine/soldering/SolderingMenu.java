package com.hbm.machine.soldering;

import com.google.gson.JsonParser;
import com.hbm.item.battery.*;
import com.hbm.item.fluid.FluidIdentifier;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/** Exact11machine+36player slot positions and original shift routing. */
public final class SolderingMenu extends AbstractContainerMenu {
    public final Container machine;private final BlockPos pos;
    private static final Set<String> UPGRADES=loadUpgrades();
    private static Set<String> loadUpgrades(){try(var stream=SolderingMenu.class.getResourceAsStream("/hbm/soldering-upgrades.json")){if(stream==null)throw new IllegalStateException("Missing real upgrade item identities");var set=new HashSet<String>();JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonArray().forEach(value->set.add(value.getAsString()));return Set.copyOf(set);}catch(java.io.IOException error){throw new IllegalStateException(error);}}
    public static boolean upgrade(ItemStack stack){return !stack.isEmpty()&&UPGRADES.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());}
    public SolderingMenu(int id,Inventory player,RegistryFriendlyByteBuf buffer){this(id,player,buffer.readBlockPos());}
    private SolderingMenu(int id,Inventory player,BlockPos pos){this(id,player,player.player.level().getBlockEntity(pos) instanceof SolderingBlockEntity machine?machine:new SimpleContainer(11),pos);}
    public SolderingMenu(int id,Inventory player,Container machine,BlockPos pos){
        super(SolderingContent.MENU.get(),id);checkContainerSize(machine,11);this.machine=machine;this.pos=pos;machine.startOpen(player.player);
        for(int row=0;row<2;row++)for(int column=0;column<3;column++){int index=row*3+column;addSlot(new Slot(machine,index,17+column*18,18+row*18){@Override public boolean mayPlace(ItemStack stack){return machine.canPlaceItem(index,stack);}});}
        addSlot(new com.hbm.recipe.generic.GenericOutputSlot(player.player,machine,6,107,27));addSlot(new Slot(machine,7,152,72));addSlot(new Slot(machine,8,17,63));
        for(int i=0;i<2;i++)addSlot(new Slot(machine,9+i,89+i*18,63){@Override public boolean mayPlace(ItemStack stack){return upgrade(stack);}});
        for(int row=0;row<3;row++)for(int column=0;column<9;column++)addSlot(new Slot(player,9+row*9+column,8+column*18,122+row*18));for(int i=0;i<9;i++)addSlot(new Slot(player,i,8+i*18,180));
    }
    public BlockPos position(){return pos;}
    @Override public boolean stillValid(Player player){return machine.stillValid(player);}
    @Override public void removed(Player player){super.removed(player);machine.stopOpen(player);}
    @Override public ItemStack quickMoveStack(Player player,int index){
        if(index<0||index>=slots.size()||!getSlot(index).hasItem())return ItemStack.EMPTY;var slot=getSlot(index);var stack=slot.getItem();var copy=stack.copy();
        if(index<=10){if(!moveItemStackTo(stack,11,slots.size(),true))return ItemStack.EMPTY;}
        else if(stack.getItem() instanceof HbmBattery||stack.getItem() instanceof CreativeBatteryItem){if(!moveItemStackTo(stack,7,8,false))return ItemStack.EMPTY;}
        else if(stack.getItem() instanceof FluidIdentifier){if(!moveItemStackTo(stack,8,9,false))return ItemStack.EMPTY;}
        else if(upgrade(stack)){if(!moveItemStackTo(stack,9,11,false))return ItemStack.EMPTY;}
        else{
            // Source branch mutates inventories but returns null before final slot callbacks. Preserve that outcome.
            var recipes=SolderingRecipes.recipes(player.level().getRecipeManager());
            for(var recipe:recipes)for(var ingredient:recipe.toppings())if(ingredient.matches(stack,true)&&!moveItemStackTo(stack,0,3,false))return ItemStack.EMPTY;
            for(var recipe:recipes)for(var ingredient:recipe.pcb())if(ingredient.matches(stack,true)&&!moveItemStackTo(stack,3,5,false))return ItemStack.EMPTY;
            for(var recipe:recipes)for(var ingredient:recipe.solder())if(ingredient.matches(stack,true)&&!moveItemStackTo(stack,5,6,false))return ItemStack.EMPTY;
            return ItemStack.EMPTY;
        }
        if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();return copy;
    }
}

package com.hbm.world.structure.vending;

import java.util.Map;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/** Two fixed source items share the directional/proxy world block. */
public final class VendingMachineItem extends BlockItem {
    private final int variant;
    public VendingMachineItem(Block block,int variant){super(block,new Properties());if(variant<0||variant>1)throw new IllegalArgumentException("Unknown vending item "+variant);this.variant=variant;}
    public static int variant(ItemStack stack){if(!(stack.getItem() instanceof VendingMachineItem item))throw new IllegalArgumentException("Not a vending machine item");return item.variant;}
    public static ItemStack stack(int variant){return new ItemStack(VendingMachineContent.item(variant));}
    @Override public void registerBlocks(Map<Block,Item> mapping,Item item){if(variant==0)mapping.put(getBlock(),item);}
    @Override public String getDescriptionId(){return "item.hbm.vending_machine_"+(variant==0?"soda":"snacks");}
}

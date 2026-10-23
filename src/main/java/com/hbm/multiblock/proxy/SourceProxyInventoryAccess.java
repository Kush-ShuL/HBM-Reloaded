package com.hbm.multiblock.proxy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
/** Original IConditionalInvAccess on its six real core types, preserving the calling proxy position. */
public interface SourceProxyInventoryAccess extends WorldlyContainer {
 default boolean sourceProxyCanPlace(BlockPos proxy,int slot,ItemStack stack){return canPlaceItem(slot,stack);}
 int[] sourceProxySlots(BlockPos proxy,Direction side);
 default boolean sourceProxyCanInsert(BlockPos proxy,int slot,ItemStack stack,Direction side){return canPlaceItemThroughFace(slot,stack,side);}
 default boolean sourceProxyCanExtract(BlockPos proxy,int slot,ItemStack stack,Direction side){return canTakeItemThroughFace(slot,stack,side);}
}

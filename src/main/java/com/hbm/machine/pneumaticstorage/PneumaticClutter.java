package com.hbm.machine.pneumaticstorage;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/** Original54 real inventory slots; cache changes always affect that same inventory. */
public final class PneumaticClutter extends PneumaticStorageBase {
    public PneumaticClutter(BlockPos pos,BlockState state){super(PneumaticKind.CLUTTER,pos,state);}
    @Override public long getAmountAt(int index){return slots[index].isEmpty()?0:slots[index].getCount();}
    @Override public boolean allowTypeSetting(){return true;}
    @Override public long useUpItem(int index,long amount){var stack=slots[index];if(stack.isEmpty())return amount;int remove=(int)Math.min(stack.getCount(),amount);removeItem(index,remove);return amount-remove;}
    @Override public long addItem(int index,long amount){var stack=slots[index];if(stack.isEmpty())return amount;int add=(int)Math.min(amount,Math.min(stack.getMaxStackSize(),64)-stack.getCount());stack.grow(add);if(add!=0)setChanged();return amount-add;}
    @Override public long setupType(int index,ItemStack template,long amount){int add=(int)Math.min(amount,Math.min(template.getMaxStackSize(),64));slots[index]=template.copyWithCount(add);setChanged();return amount-add;}
}

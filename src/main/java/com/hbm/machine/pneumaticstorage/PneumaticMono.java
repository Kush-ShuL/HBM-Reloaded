package com.hbm.machine.pneumaticstorage;

import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/** Three actual100,000-item stores, each with an independently chosen identity template. */
public final class PneumaticMono extends PneumaticStorageBase {
    public static final int CAPACITY=100000;
    public final int[] amounts=new int[3];
    public PneumaticMono(BlockPos pos,BlockState state){super(PneumaticKind.MONO,pos,state);}
    @Override public long getAmountAt(int index){return amounts[index];}
    @Override public boolean allowTypeSetting(){return false;}
    @Override public long useUpItem(int index,long amount){if(amounts[index]<=0)return amount;int remove=(int)Math.min(amount,amounts[index]);amounts[index]-=remove;setChanged();return amount-remove;}
    @Override public long addItem(int index,long amount){int capacity=CAPACITY-amounts[index];if(capacity<=0)return amount;int add=(int)Math.min(amount,capacity);amounts[index]+=add;setChanged();return amount-add;}
    @Override public long setupType(int index,ItemStack template,long amount){return amount;}
    public void setPattern(int index,ItemStack carried){if(index<0||index>=3||amounts[index]>0&&!slots[index].isEmpty())return;setItem(index,carried.isEmpty()?ItemStack.EMPTY:carried.copyWithCount(1));}
    @Override protected void saveFields(CompoundTag tag){super.saveFields(tag);tag.putIntArray("amounts",amounts);}
    @Override protected void loadFields(CompoundTag tag){super.loadFields(tag);var saved=tag.getIntArray("amounts");Arrays.fill(amounts,0);System.arraycopy(saved,0,amounts,0,Math.min(saved.length,amounts.length));}
}

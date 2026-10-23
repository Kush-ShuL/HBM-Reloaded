package com.hbm.machine.pneumaticstorage;

import java.util.stream.IntStream;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/** Original nine insertion slots, one-tick fresh-input wait and100-tick failed-cache backoff. */
public final class PneumaticImporter extends PneumaticMachineBase {
    public final int[] delay=new int[9];
    private static final int[] ACCESS=IntStream.range(0,9).toArray();
    public PneumaticImporter(BlockPos pos,BlockState state){super(PneumaticKind.IMPORTER,pos,state);}
    @Override public void setItem(int index,ItemStack stack){super.setItem(index,stack);if(!stack.isEmpty())delay[index]=Math.max(delay[index],1);}
    @Override public boolean canPlaceItem(int index,ItemStack stack){return true;}
    @Override public int[] getSlotsForFace(Direction side){return ACCESS.clone();}
    @Override protected void updateServer(ServerLevel server){super.updateServer(server);if(cache==null||cache.hasExpired)return;for(int i=0;i<9;i++){if(delay[i]>0){delay[i]--;continue;}var stack=slots[i];if(stack.isEmpty())continue;int remaining=(int)cache.addItemsAndReturnQuantity(stack,stack.getCount());if(remaining==stack.getCount())delay[i]=100;else removeItem(i,stack.getCount()-remaining);}}
}

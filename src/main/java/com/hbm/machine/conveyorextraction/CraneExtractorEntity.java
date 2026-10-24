package com.hbm.machine.conveyorextraction;

import api.hbm.conveyor.*;
import com.hbm.conveyor.MovingItem;
import com.hbm.conveyor.crane.*;
import com.hbm.machine.conveyorpackaging.*;
import com.hbm.pneumatic.PatternMatcher;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** Original 20 slots, matcher/filter rules, first accepted stack and buffer fallback. */
public final class CraneExtractorEntity extends CraneBaseEntity implements CraneBaseEntity.Filter {
    public boolean isWhitelist,maxEject;
    public final PatternMatcher matcher=new PatternMatcher(9);
    public CraneExtractorEntity(BlockPos pos,BlockState state){super(CraneExtractionContent.EXTRACTOR_ENTITY.get(),pos,state,20);}
    @Override protected String nameKey(){return "container.craneExtractor";}
    @Override public void setItem(int slot,ItemStack stack){super.setItem(slot,stack);if(!stack.isEmpty()&&level!=null&&(slot==19&&CraneMetaUpgradeItem.isEjector(stack)||slot==18&&CraneMetaUpgradeItem.isStack(stack)))level.playSound(null,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,com.hbm.registry.HbmSounds.get("item.upgradePlug"),net.minecraft.sounds.SoundSource.BLOCKS,1F,1F);}
    @Override protected void serverTick(ServerLevel world){
        int delay=20;if(CraneMetaUpgradeItem.isEjector(slots[19]))delay=switch(CraneMetaUpgradeItem.level(slots[19])){case 0->10;case 1->5;case 2->2;default->20;};
        if(world.getGameTime()%delay==0&&!world.hasNeighborSignal(worldPosition)){
            int amount=1;if(CraneMetaUpgradeItem.isStack(slots[18]))amount=switch(CraneMetaUpgradeItem.level(slots[18])){case 0->4;case 1->16;case 2->64;default->1;};
            Direction input=getOutputSide(),output=getInputSide(),accessSide=opposite(input);
            var tile=world.getBlockEntity(relative(worldPosition,input));var block=world.getBlockState(relative(worldPosition,output)).getBlock();
            IConveyorBelt belt=block instanceof IConveyorBelt value?value:null;boolean hasSent=false;
            if(tile instanceof Container inventory){
                int[] access=extractionSlots(inventory,accessSide);WorldlyContainer sided=inventory instanceof WorldlyContainer value?value:null;int size=access==null?inventory.getContainerSize():access.length;
                for(int i=0;i<size;i++){
                    int index=access==null?i:access[i];var stack=inventory.getItem(index);
                    if(stack.isEmpty()||sided!=null&&!sided.canTakeItemThroughFace(index,stack,accessSide))continue;
                    if(maxEject&&stack.getCount()<Math.min(amount,stack.getMaxStackSize()))continue;
                    boolean match=matchesFilter(stack);if(isWhitelist?!match:match)continue;
                    stack=stack.copy();int count=Math.min(amount,stack.getCount());stack.setCount(count);
                    if(belt!=null){inventory.removeItem(index,count);sendItem(world,stack,belt,output);}
                    else{var remaining=CraneInventoryTransfer.addToBuffer(slots,9,17,stack);int accepted=count-(remaining.isEmpty()?0:remaining.getCount());inventory.removeItem(index,accepted);if(accepted>0)setChanged();}
                    hasSent=true;break;
                }
            }
            if(!hasSent&&belt!=null)for(int i=9;i<18;i++)if(!slots[i].isEmpty()){
                var stack=slots[i].copy();int count=Math.min(amount,stack.getCount());
                if(maxEject&&stack.getCount()<Math.min(amount,stack.getMaxStackSize()))continue;
                removeItem(i,count);stack.setCount(count);sendItem(world,stack,belt,output);break;
            }
        }
        synchronize(15);
    }
    private void sendItem(ServerLevel world,ItemStack stack,IConveyorBelt belt,Direction output){
        var at=relative(worldPosition,output);double dx=output==null?0:output.getStepX(),dy=output==null?0:output.getStepY(),dz=output==null?0:output.getStepZ();
        var snap=belt.getClosestSnappingPosition(world,at.getX(),at.getY(),at.getZ(),new Vec3(worldPosition.getX()+.5+dx*.55,worldPosition.getY()+.5+dy*.55,worldPosition.getZ()+.5+dz*.55));
        var moving=new MovingItem(world);moving.setPos(snap);moving.setItemStack(stack);world.addFreshEntity(moving);
        if(belt instanceof IEnterableBlock enterable&&enterable.canItemEnter(world,at.getX(),at.getY(),at.getZ(),opposite(output),moving)){enterable.onItemEnter(world,at.getX(),at.getY(),at.getZ(),opposite(output),moving);moving.discard();}
    }
    private static int[] extractionSlots(Container inventory,Direction side){
        if(inventory instanceof net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity)return new int[]{2};
        return inventory instanceof WorldlyContainer sided?sided.getSlotsForFace(side):null;
    }
    public boolean matchesFilter(ItemStack stack){for(int i=0;i<9;i++)if(!slots[i].isEmpty()&&matcher.isValidForFilter(slots[i],i,stack))return true;return false;}
    @Override public void nextMode(int slot){matcher.nextMode(slots[slot],slot);}
    @Override public int[] getFilterSlots(){return new int[]{0,9};}
    @Override public int[] getSlotsForFace(Direction side){return new int[]{9,10,11,12,13,14,15,16,17};}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){boolean match=matchesFilter(stack);return slot>8&&slot<18&&(isWhitelist?match:!match);}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot>8&&slot<18;}
    @Override protected void saveCrane(CompoundTag tag){tag.putBoolean("isWhitelist",isWhitelist);tag.putBoolean("maxEject",maxEject);matcher.write(tag);}
    @Override protected void loadCrane(CompoundTag tag){isWhitelist=tag.getBoolean("isWhitelist");maxEject=tag.getBoolean("maxEject");matcher.read(tag);}
    public boolean hasPermission(Player player){return player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<400;}
    public void toggle(int action){if(action==0)isWhitelist=!isWhitelist;else if(action==1)maxEject=!maxEject;setChanged();}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new CraneExtractionMenu(id,inventory,this,true);}
}

package com.hbm.machine.pistoninserter;

import api.hbm.block.IInsertable;
import com.hbm.multiblock.MuffledMachine;
import com.hbm.registry.HbmSounds;
import com.hbm.world.structure.loot.LegacyStructureItems;
import java.util.HashMap;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Original deprecated, still-registered one-slot CP1 inserter; no MachineBase/GUI/energy machinery invented. */
public final class PistonInserterEntity extends BlockEntity implements Container,MuffledMachine {
    public static final int MAX_EXTEND=25;
    public ItemStack slot=ItemStack.EMPTY;
    public int extend,delay;
    public boolean isRetracting=true;
    public double renderExtend,lastExtend;
    private boolean lastState,muffled;
    private int syncExtend,turnProgress;
    public PistonInserterEntity(BlockPos pos,BlockState state){super(PistonInserterContent.ENTITY.get(),pos,state);}
    public void neighborState(){
        if(level==null||level.isClientSide)return;
        var direction=PistonInserterBlock.direction(getBlockState());var front=direction==null?worldPosition:worldPosition.relative(direction);
        if(level.getBlockState(front).isSolidRender(level,front))return;
        boolean powered=level.hasNeighborSignal(worldPosition);
        if(powered&&!lastState&&extend<=0)isRetracting=false;
        lastState=powered;
    }
    public static void tick(Level level,BlockPos pos,BlockState state,PistonInserterEntity piston){
        if(level instanceof ServerLevel server){
            if(piston.delay<=0){
                if(piston.isRetracting&&piston.extend>0)piston.extend--;
                else if(!piston.isRetracting){
                    piston.extend++;
                    if(piston.extend>=MAX_EXTEND){
                        server.playSound(null,pos,HbmSounds.get("block.pressOperate"),SoundSource.BLOCKS,1F,1.5F);
                        var direction=PistonInserterBlock.direction(state);var target=direction==null?pos:pos.relative(direction,2);
                        if(direction!=null&&server.getBlockState(target).getBlock()instanceof IInsertable insertable&&insertable.insertItem(server,target,direction,piston.slot))piston.removeItem(0,1);
                        piston.isRetracting=true;piston.delay=5;
                    }
                }
            }else piston.delay--;
            var packet=ClientboundBlockEntityDataPacket.create(piston,(entity,lookup)->{var tag=new CompoundTag();tag.putBoolean("PistonNetworkState",true);tag.putInt("extend",piston.extend);if(!piston.slot.isEmpty())tag.put("stack",piston.slot.save(lookup));return tag;});
            try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for(var player:server.players())if(player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<25*25)player.connection.send(packet);dispatchScope.commit();}
        }else{
            piston.lastExtend=piston.renderExtend;
            if(piston.turnProgress>0){piston.renderExtend+=(piston.syncExtend-piston.renderExtend)/(double)piston.turnProgress;piston.turnProgress--;}else piston.renderExtend=piston.syncExtend;
        }
    }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putInt("extend",extend);tag.putBoolean("retract",isRetracting);tag.putBoolean("state",lastState);tag.putBoolean("muffled",muffled);if(!slot.isEmpty())tag.put("stack",slot.save(lookup));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);extend=tag.getInt("extend");isRetracting=tag.getBoolean("retract");lastState=tag.getBoolean("state");muffled=tag.getBoolean("muffled");slot=readStack(tag,lookup);if(level!=null&&level.isClientSide){syncExtend=extend;turnProgress=2;}}
    private static ItemStack readStack(CompoundTag tag,HolderLookup.Provider lookup){var stored=tag.getCompound("stack");if(!stored.contains("Count"))return ItemStack.parseOptional(lookup,stored);var palette=new HashMap<Integer,String>();for(var entry:tag.getList("hbm_piston_item_palette",Tag.TAG_COMPOUND)){var row=(CompoundTag)entry;palette.put(row.getInt("id"),row.getString("name"));}return LegacyStructureItems.convertStack(stored,palette,lookup);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup){return saveWithoutMetadata(lookup);}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){if(tag.getBoolean("PistonNetworkState")){syncExtend=tag.getInt("extend");slot=ItemStack.parseOptional(lookup,tag.getCompound("stack"));turnProgress=2;}else loadWithComponents(tag,lookup);}
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup){handleUpdateTag(packet.getTag(),lookup);}
    @Override public boolean isMuffled(){return muffled;}@Override public void setMuffled(boolean value){muffled=value;setChanged();}
    @Override public int getContainerSize(){return 1;}@Override public int getMaxStackSize(){return 1;}@Override public boolean isEmpty(){return slot.isEmpty();}@Override public ItemStack getItem(int index){return slot;}
    @Override public ItemStack removeItem(int index,int amount){if(slot.isEmpty())return ItemStack.EMPTY;var result=slot.split(amount);if(slot.isEmpty())slot=ItemStack.EMPTY;return result;}
    // Original getStackInSlotOnClosing returns null and leaves its field intact.
    @Override public ItemStack removeItemNoUpdate(int index){return ItemStack.EMPTY;}
    @Override public void setItem(int index,ItemStack stack){slot=stack;if(stack.getCount()>1)stack.setCount(1);}
    @Override public boolean canPlaceItem(int index,ItemStack stack){return true;}
    @Override public boolean stillValid(Player player){return false;}
    @Override public void clearContent(){slot=ItemStack.EMPTY;setChanged();}
}

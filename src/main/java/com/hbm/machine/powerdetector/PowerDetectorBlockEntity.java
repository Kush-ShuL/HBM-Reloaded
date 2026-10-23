package com.hbm.machine.powerdetector;

import com.hbm.api.machine.MachineView;
import com.hbm.energy.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Source5HE/HIGHreceiver. The source's transient power was never saved; do not invent durable energy. */
public final class PowerDetectorBlockEntity extends BlockEntity implements EnergyReceiver,EnergyConductor,MachineView, com.hbm.multiblock.MuffledMachine {
    private long power;
    private PowerNode node;
    public boolean muffled,tilted;
    public PowerDetectorBlockEntity(BlockPos pos,BlockState state){super(PowerDetectorContent.ENTITY.get(),pos,state);}
    public static void tick(Level level,BlockPos pos,BlockState state,PowerDetectorBlockEntity detector){if(!(level instanceof ServerLevel server))return;if(detector.node==null||detector.node.isExpired()){detector.node=new PowerNode(pos);HbmEnergy.nodespace(server).createNode(detector.node);}if(detector.node.hasValidNet())detector.node.addReceiver(detector);int next=0;if(detector.power>0){next=1;detector.power--;}if(state.getValue(PowerDetectorBlock.META)!=next){server.setBlock(pos,state.setValue(PowerDetectorBlock.META,next),Block.UPDATE_ALL);detector.setChanged();}}
    @Override public void setPower(long amount){power=amount;}@Override public long getPower(){return power;}@Override public long getMaxPower(){return 5;}@Override public ConnectionPriority getPriority(){return ConnectionPriority.HIGH;}
    @Override public PowerNode energyNode(){return node;}@Override public boolean canConnect(Direction side){return side!=null;}@Override public boolean isLoaded(){return !isRemoved()&&level!=null&&level.hasChunkAt(worldPosition);}
    @Override public long energyStored(){return power;}@Override public long energyCapacity(){return 5;}
    private void disconnect(){if(node!=null&&!node.isExpired()&&level instanceof ServerLevel server)HbmEnergy.nodespace(server).destroyNode(node);node=null;}
    @Override public void setRemoved(){disconnect();super.setRemoved();}@Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted");}

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}

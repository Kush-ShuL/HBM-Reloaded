package com.hbm.door.sourceblast;

import com.hbm.machine.storage.LockableBlockEntity;
import com.hbm.registry.HbmSounds;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Original TileEntityBlastDoor, including tick/clock split and source redstone precedence. No GUI/HE/fluid/OC exists. */
public final class SourceBlastDoorEntity extends LockableBlockEntity {
    private static final int[][] SIDES={{1,0},{-1,0},{0,1},{0,-1}};
    private static final byte OPEN_NEIGH=0,CLOSE_NEIGH=1,LOCK_NEIGH=2;
    private record Pending(byte action,int pins,double modifier){}
    private final ArrayDeque<Pending> propagation=new ArrayDeque<>();
    public boolean isOpening,redstoned;
    public int state;
    public long sysTime;
    private int timer;
    private boolean placementPending;
    private int placementY=1;
    public SourceBlastDoorEntity(BlockPos pos,BlockState state){super(SourceBlastDoorContent.ENTITY.get(),pos,state);}
    public void beginPlacement(){placementPending=true;placementY=1;setChanged();finishPlacement();}
    private boolean finishPlacement(){
        if(!placementPending)return true;if(!SourceBlastDoorContent.neighborsReady(level,worldPosition))return false;
        while(placementY<=6){if(!placeDummy(worldPosition.above(placementY),false)){placementPending=false;setChanged();level.destroyBlock(worldPosition,true);return false;}placementY++;setChanged();}
        placementPending=false;setChanged();return true;
    }
    public static void tick(Level level,BlockPos pos,BlockState block,SourceBlastDoorEntity door){
        if(level.isClientSide||!SourceBlastDoorContent.neighborsReady(level,pos))return;
        if(!door.finishPlacement()||door.isRemoved()||!door.finishPropagation())return;
        // Deliberate original precedence: the upper input is not gated by !isLocked().
        if(!door.isLocked()&&level.hasNeighborSignal(pos)||level.hasNeighborSignal(pos.above(6))){if(!door.redstoned)door.tryToggle();door.redstoned=true;}else door.redstoned=false;
        if(!door.finishPropagation())return;
        if(door.state!=1)door.timer=0;
        else{
            door.timer++;
            if(door.isOpening){if(door.timer>=0)door.removeDummy(pos.above());if(door.timer>=20)door.removeDummy(pos.above(2));if(door.timer>=40)door.removeDummy(pos.above(3));if(door.timer>=60)door.removeDummy(pos.above(4));if(door.timer>=80)door.removeDummy(pos.above(5));}
            else{if(door.timer>=20)door.placeDummy(pos.above(5),true);if(door.timer>=40)door.placeDummy(pos.above(4),true);if(door.timer>=60)door.placeDummy(pos.above(3),true);if(door.timer>=80)door.placeDummy(pos.above(2),true);if(door.timer>=100)door.placeDummy(pos.above(),true);}
            if(door.timer>=100){door.state=door.isOpening?2:0;door.sound("block.reactorStop",1F);}
        }
        door.setChanged();SourceBlastDoorState.send(door,false);
    }
    public boolean canOpen(){return state==0;}public boolean canClose(){return state==2;}
    public void open(){if(state==0){SourceBlastDoorState.send(this,true);isOpening=true;state=1;setChanged();sound("block.reactorStart",.75F);}}
    public void close(){if(state==2){SourceBlastDoorState.send(this,true);isOpening=false;state=1;setChanged();sound("block.reactorStart",.75F);}}
    public void tryToggle(){if(canOpen()){open();propagate(OPEN_NEIGH);}else if(canClose()){close();propagate(CLOSE_NEIGH);}}
    @Override public void lock(){super.lock();if(level!=null&&!level.isClientSide)propagate(LOCK_NEIGH);}
    private void localLock(){super.lock();}
    private void sound(String sound,float pitch){if(level!=null&&!level.isClientSide)level.playSound(null,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),HbmSounds.get(sound),SoundSource.BLOCKS,.5F,pitch);}
    private void propagate(byte action){propagation.add(new Pending(action,getPins(),getMod()));setChanged();finishPropagation();}
    private static boolean eligible(SourceBlastDoorEntity child,byte action,int pins){return action==LOCK_NEIGH?!child.isLocked():(action==OPEN_NEIGH?child.canOpen():child.canClose())&&(!child.isLocked()||child.getPins()==pins);}
    /** Read-only graph preflight. No random values/state are consumed while a source-linked cold neighbour is waiting. */
    private boolean linkedReady(Pending job){
        var seen=new HashSet<BlockPos>();var nodes=new ArrayDeque<BlockPos>();nodes.push(worldPosition);
        while(!nodes.isEmpty()){
            var pos=nodes.pop();if(!seen.add(pos))continue;
            if(!SourceBlastDoorContent.neighborsReady(level,pos))return false;
            int pins=pos.equals(worldPosition)?job.pins():((SourceBlastDoorEntity)level.getBlockEntity(pos)).getPins();
            for(var side:SIDES){var next=pos.offset(side[0],0,side[1]);if(level.getBlockEntity(next)instanceof SourceBlastDoorEntity child&&eligible(child,job.action(),pins))nodes.push(next);}
        }
        return true;
    }
    private boolean finishPropagation(){
        if(level==null||level.isClientSide)return propagation.isEmpty();
        while(!propagation.isEmpty()){
            var job=propagation.peek();if(!linkedReady(job))return false;
            propagateNow(job.action(),job.pins(),job.modifier());propagation.remove();setChanged();
        }
        return true;
    }
    private void propagateNow(byte action,int pins,double modifier){
        for(var side:SIDES){var pos=worldPosition.offset(side[0],0,side[1]);if(!(level.getBlockEntity(pos)instanceof SourceBlastDoorEntity child)||!eligible(child,action,pins))continue;
            if(action==LOCK_NEIGH){child.setPins(pins);child.localLock();child.propagateNow(action,child.getPins(),child.getMod());child.setMod(modifier);}
            else {if(action==OPEN_NEIGH)child.open();else child.close();child.propagateNow(action,child.getPins(),child.getMod());}
        }
    }
    public boolean placeDummy(BlockPos pos,boolean force){
        var present=level.getBlockState(pos);if(!present.canBeReplaced()){if(!force)return false;if(!present.is(SourceBlastDoorContent.DUMMY.get()))level.destroyBlock(pos,false);}
        level.setBlock(pos,SourceBlastDoorContent.DUMMY.get().defaultBlockState(),3);
        if(level.getBlockEntity(pos)instanceof SourceBlastDoorDummy dummy)dummy.parent(worldPosition);
        return true;
    }
    public void removeDummy(BlockPos pos){if(level.getBlockState(pos).is(SourceBlastDoorContent.DUMMY.get()))SourceBlastDoorDummyBlock.safely(()->level.setBlock(pos,Blocks.AIR.defaultBlockState(),3));}
    public void receive(boolean opening,int next,boolean startClock){isOpening=opening;state=next;if(startClock)sysTime=System.currentTimeMillis();}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){
        super.saveAdditional(tag,lookup);tag.putBoolean("isOpening",isOpening);tag.putInt("state",state);tag.putLong("sysTime",sysTime);tag.putInt("timer",timer);tag.putBoolean("redstoned",redstoned);
        if(placementPending){tag.putBoolean("hbm240Placement",true);tag.putInt("hbm240PlacementY",placementY);}if(!propagation.isEmpty()){var list=new ListTag();for(var job:propagation){var value=new CompoundTag();value.putByte("action",job.action());value.putInt("pins",job.pins());value.putDouble("modifier",job.modifier());list.add(value);}tag.put("hbm240Propagation",list);}
    }
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){
        super.loadAdditional(tag,lookup);isOpening=tag.getBoolean("isOpening");state=tag.getInt("state");sysTime=tag.getLong("sysTime");timer=tag.getInt("timer");redstoned=tag.getBoolean("redstoned");placementPending=tag.getBoolean("hbm240Placement");placementY=tag.contains("hbm240PlacementY")?tag.getInt("hbm240PlacementY"):1;propagation.clear();var list=tag.getList("hbm240Propagation",Tag.TAG_COMPOUND);for(var element:list){var value=(CompoundTag)element;propagation.add(new Pending(value.getByte("action"),value.getInt("pins"),value.getDouble("modifier")));}
    }
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup){long clock=sysTime;loadAdditional(tag,lookup);if(level!=null&&level.isClientSide)sysTime=clock;}
}

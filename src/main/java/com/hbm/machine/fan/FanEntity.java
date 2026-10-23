package com.hbm.machine.fan;

import api.hbm.block.IBlowable;
import com.hbm.multiblock.MuffledMachine;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Source redstone fan runs its entity force on both sides, with only blowable callbacks on the server. */
public final class FanEntity extends BlockEntity implements MuffledMachine {
    public float spin,prevSpin;
    public boolean falloff=true,suck,muffled,tilted;
    private int syncedFlags=-1;
    public FanEntity(BlockPos pos,BlockState state) { super(FanContent.ENTITY.get(),pos,state); }
    public static void tick(Level level,BlockPos pos,BlockState state,FanEntity fan) { fan.update(); }
    private void update() {
        prevSpin=spin;
        if(level.hasNeighborSignal(worldPosition)) {
            Direction direction=FanBlock.direction(getBlockState());
            int dx=direction==null?0:direction.getStepX(),dy=direction==null?0:direction.getStepY(),dz=direction==null?0:direction.getStepZ();
            int range=10,effRange=0;
            for(int i=1;i<=range;i++) {
                var at=worldPosition.offset(dx*i,dy*i,dz*i);
                var state=level.getBlockState(at);
                boolean blowable=state.getBlock() instanceof IBlowable;
                if(state.isSolidRender(level,at)||blowable) {
                    if(!level.isClientSide&&blowable)((IBlowable)state.getBlock()).applyFan(level,at,direction,i);
                    break;
                }
                effRange=i;
            }
            int x=dx*effRange,y=dy*effRange,z=dz*effRange;
            double cx=worldPosition.getX()+.5,cy=worldPosition.getY()+.5,cz=worldPosition.getZ()+.5;
            var bounds=new AABB(cx+Math.min(x,0),cy+Math.min(y,0),cz+Math.min(z,0),cx+Math.max(x,0),cy+Math.max(y,0),cz+Math.max(z,0)).inflate(.5);
            for(Entity entity:level.getEntitiesOfClass(Entity.class,bounds,entity->true)) {
                double coefficient=.1;
                if(falloff) {
                    double distance=(float)Math.sqrt(entity.distanceToSqr(cx,cy,cz));
                    coefficient*=1.5*(1-distance/range/2);
                }
                if(suck)coefficient*=-1;
                entity.setDeltaMovement(entity.getDeltaMovement().add(dx*coefficient,dy*coefficient,dz*coefficient));
            }
            if(level.isClientSide&&level.random.nextInt(30)==0) {
                double speed=suck?-.2:.2;
                level.addParticle(ParticleTypes.CLOUD,cx+dx*.5,cy+dy*.5,cz+dz*.5,dx*speed,dy*speed,dz*speed);
            }
            spin+=30;
        }
        if(spin>=360) { prevSpin-=360;spin-=360; }
        if(!level.isClientSide) {
            int flags=(falloff?1:0)|(suck?2:0);
            if(flags!=syncedFlags||level.getGameTime()%20==0) { syncedFlags=flags;sync(); }
        }
    }
    public void sync() { if(level!=null&&!level.isClientSide)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),2); }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup) { super.saveAdditional(tag,lookup);tag.putBoolean("falloff",falloff);tag.putBoolean("suck",suck);tag.putBoolean("muffled",muffled);tag.putBoolean("tilted",tilted); }
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup) { super.loadAdditional(tag,lookup);falloff=tag.getBoolean("falloff");suck=tag.getBoolean("suck");muffled=tag.getBoolean("muffled");tilted=tag.getBoolean("tilted"); }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup) { return saveWithoutMetadata(lookup); }
    @Override public void handleUpdateTag(CompoundTag tag,HolderLookup.Provider lookup) { loadAdditional(tag,lookup); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public void onDataPacket(net.minecraft.network.Connection connection,ClientboundBlockEntityDataPacket packet,HolderLookup.Provider lookup) { handleUpdateTag(packet.getTag(),lookup); }
    @Override public boolean isMuffled() { return muffled; }
    @Override public void setMuffled(boolean value) { muffled=value;setChanged(); }
}

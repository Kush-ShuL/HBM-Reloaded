package com.hbm.machine.batterysocket;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Actual nonpersistent source discharge beam: penetrating entity hits, first block, lifetime3. */
public final class SocketDischargeBeam extends Entity {
    // These source BulletConfig fields are retained; this callback does not invoke its standard direct-damage path.
    public static final float BASE_DAMAGE=50,THRESHOLD_NEGATION=20,ARMOR_PIERCING=.5F;
    public double beamLength;
    public SocketDischargeBeam(EntityType<? extends SocketDischargeBeam> type,Level level) {super(type,level);noPhysics=true;}
    public void fire(Vec3 delta) {
        if(!(level() instanceof ServerLevel level)) return;
        double range=delta.length();float pitch=(float)(-Math.asin(delta.y/range)*180D/Math.PI),yaw=(float)(-Math.atan2(delta.x,delta.z)*180D/Math.PI);setXRot(pitch);setYRot(yaw);
        float y=yaw/180F*(float)Math.PI,p=pitch/180F*(float)Math.PI;
        Vec3 heading=new Vec3(-Mth.sin(y)*Mth.cos(p),-Mth.sin(p),Mth.cos(y)*Mth.cos(p)).scale(range);
        Vec3 start=position(),end=start.add(heading);
        var block=level.clip(new ClipContext(start,end,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,this));
        if(block.getType()!=HitResult.Type.MISS) end=block.getLocation();
        for(Entity entity:level.getEntities(this,getBoundingBox().expandTowards(heading).inflate(1))) {
            if(!entity.isAlive()||!entity.isPickable()) continue;
            var point=entity.getBoundingBox().inflate(.3F).clip(start,end);
            if(point.isPresent())SocketDischarge.explode(level,point.get());
        }
        if(block.getType()==HitResult.Type.BLOCK&&!level.getBlockState(block.getBlockPos()).is(Blocks.NETHER_PORTAL)) {level.destroyBlock(block.getBlockPos(),false);SocketDischarge.explode(level,block.getLocation());}
        beamLength=start.distanceTo(end);
    }
    @Override public void tick() {super.tick();if(!level().isClientSide&&tickCount>3)discard();}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}
    @Override protected void readAdditionalSaveData(CompoundTag tag) {discard();}
    @Override protected void addAdditionalSaveData(CompoundTag tag) {}
}

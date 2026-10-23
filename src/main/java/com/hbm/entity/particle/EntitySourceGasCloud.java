package com.hbm.entity.particle;

import com.hbm.block.LegacyBlockTraits;
import com.hbm.bomb.nuclear.NuclearChunkAccess;
import com.hbm.explosion.ExplosionChaos;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Only the two original same-physics source cloud subclasses; existing EntityModFX still owns fields/NBT/tracking. */
public abstract class EntitySourceGasCloud extends EntityModFX {
    private final NuclearChunkAccess access=new NuclearChunkAccess();
    private final boolean chlorine;
    private boolean executing;
    protected EntitySourceGasCloud(EntityType<?> type,Level level,boolean chlorine){super(type,level,0,0,0);this.chlorine=chlorine;}
    protected EntitySourceGasCloud(EntityType<?> type,Level level,double x,double y,double z,double mx,double my,double mz,float scale,boolean chlorine){
        super(type,level,x,y,z,0,0,0);this.chlorine=chlorine;
        setDeltaMovement(getDeltaMovement().scale(.10000000149011612D).add(mx,my,mz));
        particleRed=particleGreen=particleBlue=(float)(Math.random()*.30000001192092896D);
        particleScale*=.75F;particleScale*=scale;smokeParticleScale=particleScale;noPhysics=false;
    }
    @Override public void tick(){
        // No RNG or source fields are consumed while a real future block read is not FULL.
        try{
            if(level() instanceof ServerLevel server&&!ready(server))return;
            executing=true;
            try{sourceTick();}finally{executing=false;access.close();}
        }catch(RuntimeException|Error failure){access.close();throw failure;}
    }
    private Vec3 sourceVelocity(){
        var velocity=getDeltaMovement().scale(.7599999785423279D);
        if(onGround())velocity=velocity.multiply(.699999988079071D,1,.699999988079071D);
        if(level().isRaining()&&level().canSeeSky(BlockPos.containing(getX(),getY(),getZ())))velocity=velocity.add(0,-.01D,0);
        return velocity;
    }
    private boolean full(ServerLevel server,int x,int z){return server.getChunkSource().getChunkNow(x,z)!=null||access.get(server,x,z)!=null;}
    private boolean ready(ServerLevel server){
        var current=BlockPos.containing(getX(),getY(),getZ());
        if(!full(server,current.getX()>>4,current.getZ()>>4))return false;
        var step=sourceVelocity().scale(.25D);double x=getX(),y=getY(),z=getZ();boolean ready=true;
        for(int i=0;i<4;i++){
            x+=step.x;y+=step.y;z+=step.z;
            // Original normal-cube probes use int truncation, including negative coordinates.
            if(!full(server,((int)x)>>4,((int)z)>>4))ready=false;
        }
        return ready;
    }
    private void sourceTick(){
        xo=getX();yo=getY();zo=getZ();
        int minimum=chlorine?700:900;
        if(maxAge<minimum)maxAge=random.nextInt(chlorine?101:301)+minimum;
        if((chlorine||!level().isClientSide)&&random.nextInt(50)==0){
            if(chlorine)ExplosionChaos.poison(level(),(int)getX(),(int)getY(),(int)getZ(),2);
            else ExplosionChaos.c(level(),(int)getX(),(int)getY(),(int)getZ(),2);
        }
        particleAge++;if(particleAge>=maxAge)discard();
        setDeltaMovement(sourceVelocity());
        for(int i=0;i<4;i++){
            var step=getDeltaMovement().scale(.25D);
            setPosRaw(getX()+step.x,getY()+step.y,getZ()+step.z);
            var pos=new BlockPos((int)getX(),(int)getY(),(int)getZ());
            if(LegacyBlockTraits.isNormalCube(level(),pos,level().getBlockState(pos))){
                if((chlorine||!level().isClientSide)&&random.nextInt(5)!=0)discard();
                setPosRaw(getX()-step.x,getY()-step.y,getZ()-step.z);setDeltaMovement(Vec3.ZERO);
            }
        }
    }
    @Override public void remove(RemovalReason reason){if(!executing)access.close();super.remove(reason);}
    @Override protected void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);access.close();}
}

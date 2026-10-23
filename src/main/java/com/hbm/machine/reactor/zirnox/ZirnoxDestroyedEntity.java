package com.hbm.machine.reactor.zirnox;
import com.hbm.exposure.HbmExposure;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
/** Original wreck ray shielding and inverse-square radiation; uses the shared real contamination/armor system. */
public final class ZirnoxDestroyedEntity extends BlockEntity {
    public boolean onFire=true;
    public ZirnoxDestroyedEntity(BlockPos pos,BlockState state){super(ZirnoxContent.DESTROYED_ENTITY.get(),pos,state);}
    public static void tick(Level level,BlockPos pos,BlockState state,ZirnoxDestroyedEntity wreck){if(!(level instanceof ServerLevel server))return;wreck.radiate(server);if(server.random.nextInt(5000)==0)wreck.onFire=false;if(wreck.onFire&&server.getGameTime()%50==0)wreck.flame(server,pos);}
    static void flame(ServerLevel level,BlockPos pos){ZirnoxFlameEffect.send(level,pos.getX()+.25+level.random.nextDouble()*.5,pos.getY()+1.75,pos.getZ()+.25+level.random.nextDouble()*.5,90);level.playSound(null,pos,SoundEvents.FIRE_AMBIENT,SoundSource.BLOCKS,1+level.random.nextFloat(),level.random.nextFloat()*.7F+.3F);}
    private void radiate(ServerLevel server){double x=worldPosition.getX()+.5,y=worldPosition.getY()+.5,z=worldPosition.getZ()+.5;float radiation=onFire?500000:75000;
        for(var living:server.getEntitiesOfClass(LivingEntity.class,new AABB(x,y,z,x,y,z).inflate(100))){var delta=new Vec3(living.getX()-x,living.getEyeY()-y,living.getZ()-z);double length=delta.length();var direction=delta.normalize();float resistance=0;for(int i=1;i<length;i++){var pos=BlockPos.containing(x+direction.x*i,y+direction.y*i,z+direction.z*i);resistance+=server.getBlockState(pos).getBlock().getExplosionResistance();}if(resistance<1)resistance=1;float dose=radiation;dose/=(float)resistance;dose/=(float)(length*length);HbmExposure.contaminate(living,HbmExposure.Contamination.CREATIVE,dose);if(onFire&&length<5)living.hurt(server.damageSources().onFire(),2);}
    }
    // The source reads fire but writes onFire; retain its reload behavior explicitly.
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);onFire=tag.getBoolean("fire");}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putBoolean("onFire",onFire);}
}

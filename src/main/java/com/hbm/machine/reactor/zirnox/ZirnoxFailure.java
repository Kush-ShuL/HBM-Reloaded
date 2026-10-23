package com.hbm.machine.reactor.zirnox;
import com.hbm.explosion.ExplosionNukeGeneric;
import com.hbm.machine.reactor.ReactorConfig;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmSounds;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
/** Source wreck replacement,12-block blast,72 real debris entities and35-radius source waste conversion. */
public final class ZirnoxFailure {
    public static void meltdown(ZirnoxBlockEntity machine,ServerLevel level){var core=machine.getBlockPos();var old=machine.getBlockState();var block=ZirnoxContent.DESTROYED.get();var direction=DummyableBlock.facing(old);
        DummyableBlock.safely(()->{for(var part:ZirnoxBlock.positions(core,direction))if(!part.equals(core)&&level.getBlockState(part).is(ZirnoxContent.REACTOR.get()))level.removeBlock(part,false);level.setBlock(core,block.defaultBlockState().setValue(DummyableBlock.LEGACY_META,old.getValue(DummyableBlock.LEGACY_META)),3);for(var cursor:BlockPos.betweenClosed(core.offset(-2,0,-2),core.offset(2,1,2))){var pos=cursor.immutable();if(pos.equals(core))continue;var away=pos.getY()>core.getY()?Direction.UP:pos.getX()<core.getX()?Direction.WEST:pos.getX()>core.getX()?Direction.EAST:pos.getZ()<core.getZ()?Direction.NORTH:Direction.SOUTH;level.setBlock(pos,block.defaultBlockState().setValue(DummyableBlock.LEGACY_META,away.get3DDataValue()),3);}});
        level.playSound(null,core.above(2),HbmSounds.get("block.rbmk_explosion"),SoundSource.BLOCKS,10,1);level.explode(null,core.getX(),core.getY()+3,core.getZ(),12F,Level.ExplosionInteraction.BLOCK);
        for(int i=0;i<2;i++)debris(level,core,ZirnoxDebris.Type.EXCHANGER);for(int i=0;i<20;i++){debris(level,core,ZirnoxDebris.Type.CONCRETE);debris(level,core,ZirnoxDebris.Type.BLANK);}for(int i=0;i<10;i++){debris(level,core,ZirnoxDebris.Type.ELEMENT);debris(level,core,ZirnoxDebris.Type.GRAPHITE);debris(level,core,ZirnoxDebris.Type.SHRAPNEL);}
        ExplosionNukeGeneric.waste(level,core.getX(),core.getY(),core.getZ(),35);
        for(var player:level.getEntitiesOfClass(Player.class,new AABB(core.getX()+.5,core.getY()+.5,core.getZ()+.5,core.getX()+.5,core.getY()+.5,core.getZ()+.5).inflate(100))){
            if(ReactorConfig.ELEMENTALS.get()){var tag=player.getPersistentData();var persisted=tag.getCompound(Player.PERSISTED_NBT_TAG);persisted.putBoolean("radMark",true);tag.put(Player.PERSISTED_NBT_TAG,persisted);}
            if(player instanceof net.minecraft.server.level.ServerPlayer server){var advancement=level.getServer().getAdvancements().get(net.minecraft.resources.ResourceLocation.parse("hbm:zirnoxboom"));if(advancement!=null)for(var criterion:server.getAdvancements().getOrStartProgress(advancement).getRemainingCriteria())server.getAdvancements().award(advancement,criterion);}
        }
    }
    private static void debris(ServerLevel level,BlockPos core,ZirnoxDebris.Type type){var entity=new ZirnoxDebris(ZirnoxContent.DEBRIS.get(),level);entity.setType(type);entity.setPos(core.getX()+.5,core.getY()+4,core.getZ()+.5);double x=level.random.nextGaussian()*.75,z=level.random.nextGaussian()*.75,y=.01+level.random.nextDouble()*1.25;if(type==ZirnoxDebris.Type.CONCRETE){x*=.25;y+=level.random.nextDouble();z*=.25;}if(type==ZirnoxDebris.Type.EXCHANGER){x+=.5;y*=.1;z+=.5;}entity.setDeltaMovement(x,y,z);level.addFreshEntity(entity);}
    private ZirnoxFailure(){}
}

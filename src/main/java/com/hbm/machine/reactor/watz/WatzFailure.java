package com.hbm.machine.reactor.watz;

import com.hbm.advancement.HbmAdvancements;
import com.hbm.block.fluid.HbmWorldFluids;
import com.hbm.bomb.conventional.ShrapnelProjectile;
import com.hbm.bomb.nuclear.RbmkMushEffects;
import com.hbm.radiation.HbmRadiation;
import com.hbm.registry.HbmSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.*;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

/** Source mud overflow: actual broken components and toxic mud,100Watzshrapnel and original radiation/30frame mushroom. */
final class WatzFailure {
    static void explode(WatzEntity machine,ServerLevel level){var core=machine.getBlockPos();for(int x=-3;x<=3;x++)for(int y=3;y<6;y++)for(int z=-3;z<=3;z++)level.setBlock(core.offset(x,y,z),Blocks.AIR.defaultBlockState(),3);
        var random=level.random;int count=20;for(int i=0;i<count*5;i++){var shrapnel=new ShrapnelProjectile(level,core.getX()+.5,core.getY()+3,core.getZ()+.5);double vy=(random.nextFloat()*.5+.5)*(1+(count/(15+random.nextInt(21))))+(random.nextFloat()/50*count);double vx=random.nextGaussian()*(1+count/100),vz=random.nextGaussian()*(1+count/100);shrapnel.setDeltaMovement(vx,vy,vz);shrapnel.setWatz(true);level.addFreshEntity(shrapnel);}
        var mud=HbmWorldFluids.block("mud_block").defaultBlockState();for(int y=0;y<3;y++)level.setBlock(core.above(y),mud,3);
        for(var part:WatzLayout.COMPONENTS)if(part.y()==0){int minimum=part.kind()==2?1:0,height=minimum+random.nextInt(3-minimum);for(int y=0;y<3;y++)level.setBlock(core.offset(part.x(),y,part.z()),y<=height?part.state():mud,3);}
        var point=new AABB(core.getX()+.5,core.getY()+.5,core.getZ()+.5,core.getX()+.5,core.getY()+.5,core.getZ()+.5).inflate(50);for(var player:level.getEntitiesOfClass(ServerPlayer.class,point))HbmAdvancements.trigger(player,"achWatzBoom");
        HbmRadiation.incrementRad(level,core.getX(),core.getY()+1,core.getZ(),1000F);level.playSound(null,core.getX()+.5,core.getY()+2,core.getZ()+.5,HbmSounds.get("block.rbmk_explosion"),SoundSource.BLOCKS,50,1);RbmkMushEffects.sendAtOrigin(level,core.getX()+.5,core.getY()+2,core.getZ()+.5,5,core.getX(),core.getY(),core.getZ());
    }
    private WatzFailure(){}
}

package com.hbm.block.meteorstatue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** TileEntityDecoBlockAltF: source corner-centred radius4 and eye-distance<8, both logical sides, every tick. */
public final class MeteorStatueBlockEntity extends BlockEntity {
 public MeteorStatueBlockEntity(BlockPos pos,BlockState state){super(MeteorStatueContent.ENTITY.get(),pos,state);}
 public static void tick(Level level,BlockPos pos,BlockState state,MeteorStatueBlockEntity statue){int x=pos.getX(),y=pos.getY(),z=pos.getZ();var bounds=new AABB(x-9,y-9,z-9,x+9,y+9,z+9);for(Player player:level.getEntitiesOfClass(Player.class,bounds,p->true)){double distance=(float)Math.sqrt(player.distanceToSqr(x,y,z))/4;if(distance<=1){double dx=player.getX()-x,dy=player.getY()+player.getEyeHeight()-y,dz=player.getZ()-z;double eyeDistance=(float)Math.sqrt(dx*dx+dy*dy+dz*dz);if(eyeDistance<8){player.addEffect(new MobEffectInstance(MobEffects.HEAL,5,99));player.addEffect(new MobEffectInstance(MobEffects.SATURATION,5,99));}}}}
}

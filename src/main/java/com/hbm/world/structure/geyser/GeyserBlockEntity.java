package com.hbm.world.structure.geyser;
import com.hbm.bomb.conventional.ShrapnelProjectile;
import com.hbm.entity.particle.EntityOrangeFX;
import com.hbm.particle.LegacyFluidEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
/** TileEntityGeysir stores neither timer nor custom NBT. Old metadata is deliberately tested after flipping the phase. */
public final class GeyserBlockEntity extends BlockEntity {
 private int timer;
 public GeyserBlockEntity(BlockPos pos,BlockState state){super(GeyserContent.ENTITY.get(),pos,state);}
 public static void tick(Level level,BlockPos pos,BlockState state,GeyserBlockEntity geyser){if(level.isClientSide||!level.getBlockState(pos.above()).isAir()||!(state.getBlock() instanceof GeyserBlock block))return;geyser.timer--;int meta=state.getValue(GeyserBlock.META);if(geyser.timer<=0){geyser.timer=block.nether?(meta==0?(level.random.nextBoolean()?300:450):80+level.random.nextInt(60)):(meta==0?20:400+level.random.nextInt(100));level.setBlock(pos,state.setValue(GeyserBlock.META,meta==0?1:0),2);}if(meta==1){if(block.nether)geyser.fire(level,pos);else geyser.chlorine(level,pos);}}
 private void chlorine(Level level,BlockPos pos){for(int i=0;i<3;i++){var cloud=new EntityOrangeFX(level,pos.getX()+.5,pos.getY()+1.5,pos.getZ()+.5,0,0,0);double x=level.random.nextGaussian()*.45,z=level.random.nextGaussian()*.45;cloud.setDeltaMovement(x,timer*.3,z);level.addFreshEntity(cloud);}}
 private void fire(Level level,BlockPos pos){double x=pos.getX()+.5,y=pos.getY()+.5,z=pos.getZ()+.5;if(level.getEntitiesOfClass(Player.class,new AABB(x-32,y-32,z-32,x+32,y+32,z+32)).isEmpty())return;if(level.random.nextInt(3)==0){var shrapnel=new ShrapnelProjectile(level,pos.getX()+.5,pos.getY()+1.5,pos.getZ()+.5);double mx=level.random.nextGaussian()*.05,mz=level.random.nextGaussian()*.05;shrapnel.setDeltaMovement(mx,.5+level.random.nextDouble()*timer*.01,mz);level.addFreshEntity(shrapnel);}if(timer%2==0){double mx=level.random.nextGaussian()*.05,mz=level.random.nextGaussian()*.05;var packet=new LegacyFluidEffects.Packet(true,pos.getX()+.5,pos.getY()+1.1,pos.getZ()+.5,mx,.2,mz,6.5F,null);if(level instanceof ServerLevel server)for(var player:server.players())if(player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<=75D*75D)PacketDistributor.sendToPlayer(player,packet);}}
}

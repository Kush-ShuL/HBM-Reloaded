package com.hbm.entity.mob.crab;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
/** Original TileEntityCyberCrab. Host may be visible spawner or the exact brick disguise used by dungeon templates. */
public final class MeteorSpawnerBlockEntity extends BlockEntity {
    private int age;
    public MeteorSpawnerBlockEntity(BlockPos position,BlockState state){super(CyberCrabContent.SPAWNER_ENTITY.get(),position,state);}
    public void tick(){if(level==null||level.isClientSide)return;age++;int x=worldPosition.getX(),y=worldPosition.getY(),z=worldPosition.getZ();if(age>200&&level.getBlockState(worldPosition.above()).is(Blocks.AIR)&&level.getNearestPlayer(x+.5,y+1,z+.5,25,false)!=null){if(level.getEntitiesOfClass(CyberCrab.class,new AABB(x-5,y-2,z-5,x+6,y+4,z+6)).size()<5){var crab=level.random.nextInt(5)==0?CyberCrabContent.TESLA.get().create(level):CyberCrabContent.CYBER.get().create(level);if(crab!=null){crab.setPos(x+.5,y+1,z+.5);level.addFreshEntity(crab);}}age=0;}}
    // Source never wrote age: the legal host/BE persists, but reloading restarts its runtime delay.
}

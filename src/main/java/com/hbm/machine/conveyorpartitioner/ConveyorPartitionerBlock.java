package com.hbm.machine.conveyorpartitioner;

import api.hbm.conveyor.*;
import com.hbm.world.structure.generated.compat.ForgeDirection;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import net.minecraft.core.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;

/** Actual single-cell .75-high source belt; no GUI activation, CraneBase side controls or power requirements. */
public final class ConveyorPartitionerBlock extends BaseEntityBlock implements IConveyorBelt,IEnterableBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    private static final VoxelShape BOUNDS=Block.box(0,0,0,16,12,16);
    private final Random dropRandom=new Random();
    public ConveyorPartitionerBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion().isSuffocating((s,w,p)->false).isViewBlocking((s,w,p)->false));registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){int yaw=Mth.floor((context.getPlayer()==null?0:context.getPlayer().getYRot())*4F/360F+.5F)&3;return defaultBlockState().setValue(META,switch(yaw){case 0->2;case 1->5;case 2->3;default->4;});}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return BOUNDS;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new ConveyorPartitionerEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,ConveyorPartitionerContent.ENTITY.get(),ConveyorPartitionerEntity::tick);}
    private static ForgeDirection travel(Level world,int x,int y,int z){return ForgeDirection.getOrientation(world.getBlockState(new BlockPos(x,y,z)).getValue(META));}
    @Override public boolean canItemStay(Level world,int x,int y,int z,Vec3 item){return true;}
    @Override public boolean canItemEnter(Level world,int x,int y,int z,Direction side,IConveyorItem item){return travel(world,x,y,z).ordinal()==(side==null?6:side.get3DDataValue());}
    @Override public boolean canPackageEnter(Level world,int x,int y,int z,Direction side,IConveyorPackage pack){return false;}
    @Override public void onPackageEnter(Level world,int x,int y,int z,Direction side,IConveyorPackage pack){}
    @Override public Vec3 getClosestSnappingPosition(Level world,int x,int y,int z,Vec3 item){var direction=travel(world,x,y,z);return new Vec3(direction.offsetX!=0?Mth.clamp(item.x,x,x+1):x+.5,y+.25,direction.offsetZ!=0?Mth.clamp(item.z,z,z+1):z+.5);}
    @Override public Vec3 getTravelLocation(Level world,int x,int y,int z,Vec3 item,double speed){var direction=travel(world,x,y,z);var snap=getClosestSnappingPosition(world,x,y,z,item);var motion=new Vec3(snap.x-direction.offsetX*speed-item.x,snap.y-direction.offsetY*speed-item.y,snap.z-direction.offsetZ*speed-item.z);double length=motion.length();return new Vec3(item.x+motion.x/length*speed,item.y+motion.y/length*speed,item.z+motion.z/length*speed);}
    @Override public void onItemEnter(Level world,int x,int y,int z,Direction side,IConveyorItem item){var tile=(ConveyorPartitionerEntity)world.getBlockEntity(new BlockPos(x,y,z));var remainder=tile.accept(item.getItemStack());if(!remainder.isEmpty())world.addFreshEntity(new ItemEntity(world,x+.5,y+.5,z+.5,remainder.copy()));}
    @Override protected void onRemove(BlockState state,Level world,BlockPos pos,BlockState next,boolean moving){
        if(!state.is(next.getBlock())&&world.getBlockEntity(pos)instanceof ConveyorPartitionerEntity tile){
            for(var stack:tile.slots)if(!stack.isEmpty()){float x=dropRandom.nextFloat()*.8F+.1F,y=dropRandom.nextFloat()*.8F+.1F,z=dropRandom.nextFloat()*.8F+.1F;while(!stack.isEmpty()){int count=Math.min(dropRandom.nextInt(21)+10,stack.getCount());var item=new ItemEntity(world,pos.getX()+x,pos.getY()+y,pos.getZ()+z,stack.split(count));float speed=.05F;item.setDeltaMovement((float)dropRandom.nextGaussian()*speed,(float)dropRandom.nextGaussian()*speed+.2F,(float)dropRandom.nextGaussian()*speed);world.addFreshEntity(item);}}
            world.updateNeighbourForOutputSignal(pos,this);
        }super.onRemove(state,world,pos,next,moving);
    }
}

package com.hbm.machine.conveyorsplitter;

import api.hbm.conveyor.*;
import com.hbm.api.tool.ToolableBlock;
import com.hbm.conveyor.MovingItem;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.world.structure.generated.compat.ForgeDirection;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;

/** Original two-cell splitter is itself a belt; it is not a CraneBase inventory machine. */
public final class ConveyorSplitterBlock extends DummyableBlock implements IConveyorBelt,IEnterableBlock,ToolableBlock {
    public ConveyorSplitterBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion().isSuffocating((s,w,p)->false).isViewBlocking((s,w,p)->false));}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}
    @Override public int[] dimensions(){return new int[]{0,0,0,0,0,1};}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    // Source createNewTileEntity did not filter metadata: both source cells keep their real LoadedBase BE.
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new ConveyorSplitterEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,ConveyorSplitterContent.ENTITY.get(),ConveyorSplitterEntity::tick);}
    public static ForgeDirection travel(BlockState state){int meta=state.getValue(LEGACY_META);return meta>=12?ForgeDirection.getOrientation(meta-10):ForgeDirection.getOrientation(meta).getRotation(ForgeDirection.UP);}
    @Override public boolean canItemStay(Level world,int x,int y,int z,Vec3 item){return true;}
    @Override public boolean canItemEnter(Level world,int x,int y,int z,Direction side,IConveyorItem item){var direction=travel(world.getBlockState(new BlockPos(x,y,z)));return direction.ordinal()==(side==null?6:side.get3DDataValue());}
    @Override public boolean canPackageEnter(Level world,int x,int y,int z,Direction side,IConveyorPackage pack){return false;}
    @Override public void onPackageEnter(Level world,int x,int y,int z,Direction side,IConveyorPackage pack){}
    @Override public void onItemEnter(Level world,int x,int y,int z,Direction side,IConveyorItem item){var core=findCore(world,new BlockPos(x,y,z));if(core==null||!(world.getBlockEntity(core)instanceof ConveyorSplitterEntity splitter))return;var direction=ForgeDirection.getOrientation(splitter.getBlockState().getValue(LEGACY_META)-10).getRotation(ForgeDirection.DOWN);var stacks=splitter.splitStack(item.getItemStack());spawn(world,core,stacks[0]);spawn(world,core.offset(direction.offsetX,0,direction.offsetZ),stacks[1]);}
    private void spawn(Level world,BlockPos pos,net.minecraft.world.item.ItemStack stack){if(stack.isEmpty())return;var moving=new MovingItem(world);moving.setPos(getClosestSnappingPosition(world,pos.getX(),pos.getY(),pos.getZ(),new Vec3(pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5)));moving.setItemStack(stack);world.addFreshEntity(moving);}
    @Override public Vec3 getClosestSnappingPosition(Level world,int x,int y,int z,Vec3 item){var direction=travel(world.getBlockState(new BlockPos(x,y,z)));double px=Mth.clamp(item.x,x,x+1),pz=Mth.clamp(item.z,z,z+1);return new Vec3(direction.offsetX==0?x+.5:px,y+.25,direction.offsetZ==0?z+.5:pz);}
    @Override public Vec3 getTravelLocation(Level world,int x,int y,int z,Vec3 item,double speed){var direction=travel(world.getBlockState(new BlockPos(x,y,z)));var snap=getClosestSnappingPosition(world,x,y,z,item);var motion=new Vec3(snap.x-direction.offsetX*speed-item.x,snap.y-direction.offsetY*speed-item.y,snap.z-direction.offsetZ*speed-item.z);double length=motion.length();return new Vec3(item.x+motion.x/length*speed,item.y+motion.y/length*speed,item.z+motion.z/length*speed);}
    @Override public boolean onScrew(Level world,Player player,BlockPos clicked,Direction side,float x,float y,float z,ToolType tool){if(world.isClientSide)return true;if(tool!=ToolType.SCREWDRIVER)return false;var core=findCore(world,clicked);if(core==null||!(world.getBlockEntity(core)instanceof ConveyorSplitterEntity splitter))return false;int amount=player.isShiftKeyDown()?-1:1;if(clicked.equals(core))splitter.leftRatio=(byte)Mth.clamp(splitter.leftRatio+amount,1,16);else splitter.rightRatio=(byte)Mth.clamp(splitter.rightRatio+amount,1,16);splitter.setChanged();return true;}
}

package com.hbm.conveyor.belt;

import api.hbm.conveyor.*;
import com.hbm.api.tool.ToolableBlock;
import com.hbm.api.tool.ToolableBlock.ToolType;
import com.hbm.conveyor.MovingItem;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;

/** Exact six registered X5846 belt variants. Unregistered deprecated Classic is not given a new identity. */
public final class ConveyorBeltBlock extends Block implements IConveyorBelt,ToolableBlock {
 public enum Kind{REGULAR,EXPRESS,DOUBLE,TRIPLE,LIFT,CHUTE}
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
 public final Kind kind;
 public ConveyorBeltBlock(Kind kind){super(Properties.of().mapColor(MapColor.METAL).strength(2,1.2F).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion().dynamicShape());this.kind=kind;registerDefaultState(stateDefinition.any().setValue(META,2));}
 @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 public boolean bendable(){return kind!=Kind.LIFT&&kind!=Kind.CHUTE;}
 public static Direction direction(int metadata){return metadata>=0&&metadata<6?Direction.from3DDataValue(metadata):null;}
 public static Direction opposite(Direction side){return side==null?null:side.getOpposite();}
 public static Direction rotationUp(Direction side){return side==null||side.getAxis().isVertical()?side:side.getClockWise();}
 public static Direction rotationDown(Direction side){return side==null||side.getAxis().isVertical()?side:side.getCounterClockWise();}
 public static int dx(Direction side){return side==null?0:side.getStepX();}public static int dy(Direction side){return side==null?0:side.getStepY();}public static int dz(Direction side){return side==null?0:side.getStepZ();}
 public static int path(int meta){return meta>=6&&meta<=9?1:meta>=10&&meta<=13?2:0;}
 public static int facingMeta(Player player){return switch(net.minecraft.util.Mth.floor(player.getYRot()*4F/360F+.5D)&3){case 0->2;case 1->5;case 2->3;default->4;};}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(META,facingMeta(context.getPlayer()));}
 public Direction input(Level level,BlockPos pos){if(kind==Kind.LIFT)return Direction.DOWN;if(kind==Kind.CHUTE)return Direction.UP;int meta=level.getBlockState(pos).getValue(META);return direction(meta-path(meta)*4);}
 public Direction output(Level level,BlockPos pos){if(kind==Kind.LIFT)return Direction.UP;if(kind==Kind.CHUTE)return Direction.DOWN;int meta=level.getBlockState(pos).getValue(META),path=path(meta);var primary=opposite(direction(meta-path*4));return path==2?rotationUp(primary):path==1?rotationDown(primary):primary;}
 public static boolean isBelt(BlockGetter level,BlockPos pos){return level.getBlockState(pos).getBlock() instanceof IConveyorBelt;}
 public static boolean isEnterable(BlockGetter level,BlockPos pos){return level.getBlockState(pos).getBlock() instanceof IEnterableBlock;}
 public boolean bottom(BlockGetter level,BlockPos pos){return !isBelt(level,pos.below());}
 public boolean top(BlockGetter level,BlockPos pos){return !isBelt(level,pos.above())&&!bottom(level,pos)&&!isEnterable(level,pos.above());}
 @Override public boolean canItemStay(Level level,int x,int y,int z,Vec3 itemPos){return true;}
 public Direction travel(Level level,BlockPos pos,Vec3 itemPos){int meta=level.getBlockState(pos).getValue(META);if(kind==Kind.LIFT)return !top(level,pos)?Direction.DOWN:direction(meta);if(kind==Kind.CHUTE)return isBelt(level,pos.below())||isEnterable(level,pos.below())||itemPos.y>pos.getY()+.25?Direction.UP:direction(meta);int path=path(meta);var primary=direction(meta-path*4);if(path>0){path--;var secondary=rotationUp(primary);double ix=pos.getX()+.5-(-dx(primary)*.5+dx(secondary)*(.5-path)),iz=pos.getZ()+.5-(-dz(primary)*.5+dz(secondary)*(.5-path));if(Math.abs(itemPos.x-ix)+Math.abs(itemPos.z-iz)>=1)return path==0?opposite(secondary):secondary;}return primary;}
 private boolean verticalSnap(Level level,BlockPos pos,Vec3 item){return kind==Kind.LIFT&&!top(level,pos)||kind==Kind.CHUTE&&(isBelt(level,pos.below())||isEnterable(level,pos.below())||item.y>pos.getY()+.25);}
 private static Vec3 clamp(Vec3 p,int x,int z){return new Vec3(net.minecraft.util.Mth.clamp(p.x,x,x+1),p.y,net.minecraft.util.Mth.clamp(p.z,z,z+1));}
 @Override public Vec3 getClosestSnappingPosition(Level level,int x,int y,int z,Vec3 item){var pos=new BlockPos(x,y,z);if(verticalSnap(level,pos,item))return new Vec3(x+.5,item.y,z+.5);var dir=travel(level,pos,item);item=clamp(item,x,z);double px=x+.5,pz=z+.5;if(dx(dir)!=0){px=item.x;if(kind==Kind.DOUBLE)pz+=item.z>pz?.25:-.25;else if(kind==Kind.TRIPLE)pz+=item.z>pz+.15?.3125:item.z<pz-.15?-.3125:0;}if(dz(dir)!=0){pz=item.z;if(kind==Kind.DOUBLE)px+=item.x>px?.25:-.25;else if(kind==Kind.TRIPLE)px+=item.x>px+.15?.3125:item.x<px-.15?-.3125:0;}return new Vec3(px,y+.25,pz);}
 @Override public Vec3 getTravelLocation(Level level,int x,int y,int z,Vec3 item,double speed){var pos=new BlockPos(x,y,z);if(kind==Kind.EXPRESS)speed*=3;if(kind==Kind.CHUTE){if(isBelt(level,pos.below())||isEnterable(level,pos.below()))speed*=5;else if(item.y>y+.25)speed*=3;}var dir=travel(level,pos,item);var snap=getClosestSnappingPosition(level,x,y,z,item);if(!verticalSnap(level,pos,item))item=clamp(item,x,z);double mx=snap.x-dx(dir)*speed-item.x,my=snap.y-dy(dir)*speed-item.y,mz=snap.z-dz(dir)*speed-item.z,len=(float)Math.sqrt(mx*mx+my*my+mz*mz);return new Vec3(item.x+mx/len*speed,item.y+my/len*speed,item.z+mz/len*speed);}
 @Override protected void entityInside(BlockState state,Level level,BlockPos pos,Entity entity){if(!level.isClientSide&&entity instanceof ItemEntity item&&item.tickCount>10&&!item.isRemoved()){var moving=new MovingItem(level);moving.setItemStack(item.getItem().copy());var snap=getClosestSnappingPosition(level,pos.getX(),pos.getY(),pos.getZ(),item.position());moving.setPos(snap);level.addFreshEntity(moving);item.discard();}}
 @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return kind==Kind.CHUTE?Shapes.block():Block.box(0,0,0,16,kind==Kind.LIFT?(top(level,pos)?8:16):4,16);}
 @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return getShape(state,level,pos,context);}
 // Source is non-opaque. Never query neighbor-dependent geometry from the asynchronous light engine.
 @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}@Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return true;}@Override protected VoxelShape getOcclusionShape(BlockState state,BlockGetter level,BlockPos pos){return Shapes.empty();}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){return List.of(ConveyorWandItem.make(kind==Kind.EXPRESS?1:kind==Kind.DOUBLE?2:kind==Kind.TRIPLE?3:0,1));}
 @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float hitX,float hitY,float hitZ,ToolType tool){if(tool!=ToolType.SCREWDRIVER)return false;int meta=level.getBlockState(pos).getValue(META),next=meta,path=path(meta);if(!player.isShiftKeyDown()){if(meta>9)meta-=8;if(meta>5)meta-=4;var rotated=rotationUp(direction(meta));next=rotated==null?6:rotated.get3DDataValue();if(bendable())next+=path*4;level.setBlock(pos,defaultBlockState().setValue(META,next),UPDATE_ALL);}else if(bendable()){if(path<2)next+=4;else{next-=8;if(kind==Kind.REGULAR){level.setBlock(pos,ConveyorContent.LIFT.get().defaultBlockState().setValue(META,next),UPDATE_ALL);return true;}}level.setBlock(pos,defaultBlockState().setValue(META,next),UPDATE_ALL);}else level.setBlock(pos,(kind==Kind.LIFT?ConveyorContent.CHUTE:ConveyorContent.REGULAR).get().defaultBlockState().setValue(META,next),UPDATE_ALL);return true;}
}

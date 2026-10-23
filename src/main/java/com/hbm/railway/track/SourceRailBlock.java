package com.hbm.railway.track;

import com.hbm.multiblock.DummyableBlock;
import com.hbm.world.structure.generated.compat.ForgeDirection;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;

/** Actual X5846 hidden IRailNTM multiblocks, with source-specific check/fill and rail motion. */
public final class SourceRailBlock extends DummyableBlock implements IRailNTM {
    public final SourceRailKind kind;
    public SourceRailBlock(SourceRailKind kind){super(Properties.of().mapColor(MapColor.METAL).strength(5F,6F).sound(SoundType.STONE).noOcclusion().dynamicShape());this.kind=kind;}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override public int[] dimensions(){return kind.dimensions.clone();}
    @Override public int offset(){return kind.offset;}
    @Override public TrackGauge getGauge(){return kind.gauge();}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    /** BlockContainer called createNewTileEntity for every switch metadata, including its real dummy cells. */
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return kind.isSwitch()?new RailSwitchEntity(pos,state):null;}
    public static boolean full(Level level,BlockPos pos){if(!level.isInWorldBounds(pos))return false;return level instanceof ServerLevel server?server.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4)!=null:level.hasChunkAt(pos);}
    private static boolean fullWithNeighbors(Level level,BlockPos pos){if(!full(level,pos))return false;for(var side:Direction.values())if(!full(level,pos.relative(side)))return false;return true;}
    @Override public BlockPos findCore(BlockGetter access,BlockPos start){var seen=new HashSet<BlockPos>();var current=start;while(seen.size()<=2000&&seen.add(current)){if(access instanceof Level level&&!full(level,current))return null;var state=access.getBlockState(current);if(!state.is(this))return null;int meta=state.getValue(LEGACY_META);if(meta>=12)return current;current=current.relative(Direction.from3DDataValue(meta>=6?meta-6:meta).getOpposite());}return null;}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){var direction=context.getHorizontalDirection().getOpposite();var origin=context.getClickedPos();var core=origin.relative(direction,-kind.offset);var legacy=ForgeDirection.getOrientation(direction.get3DDataValue());if(!fullWithNeighbors(context.getLevel(),origin)||!fullWithNeighbors(context.getLevel(),core))return null;for(var cell:SourceRailFootprints.cells(kind,origin,legacy))if(!fullWithNeighbors(context.getLevel(),cell.pos()))return null;if(!SourceRailFootprints.check(kind,context.getLevel(),origin,legacy))return null;return defaultBlockState().setValue(LEGACY_META,direction.get3DDataValue()+(origin.equals(core)?10:0));}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){if(level.isClientSide)return;var direction=placementFacing(state);var legacy=ForgeDirection.getOrientation(direction.get3DDataValue());var core=pos.relative(direction,-kind.offset);var cells=SourceRailFootprints.cells(kind,pos,legacy);if(!fullWithNeighbors(level,pos)||!fullWithNeighbors(level,core))return;for(var cell:cells)if(!fullWithNeighbors(level,cell.pos()))return;safely(()->{if(!pos.equals(core))level.removeBlock(pos,false);level.setBlock(core,defaultBlockState().setValue(LEGACY_META,direction.get3DDataValue()+10),UPDATE_ALL);for(var cell:cells)level.setBlock(cell.pos(),defaultBlockState().setValue(LEGACY_META,cell.meta()),UPDATE_ALL);});}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter access,BlockPos pos,CollisionContext context){return kind==SourceRailKind.RAMP?rampShape(access,pos):Shapes.box(0,0,0,1,.125,1);}
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter access,BlockPos pos,CollisionContext context){return getShape(state,access,pos,context);}
    private static final double[] RAMP_HEIGHTS={.1,.3,.5,.7,.9};
    private VoxelShape rampShape(BlockGetter access,BlockPos pos){var core=findCore(access,pos);if(core==null)return Shapes.empty();var rot=ForgeDirection.getOrientation(access.getBlockState(core).getValue(LEGACY_META)-10).getRotation(ForgeDirection.UP);var result=Shapes.empty();for(int i=0;i<5;i++){double a=-2.5+i,b=a+1,minZ=-1.5,maxZ=.5,minX=a,maxX=b;switch(rot){case EAST->{minX=-maxZ;maxX=-minZ;minZ=a;maxZ=b;}case SOUTH->{minX=-b;maxX=-a;minZ=-.5;maxZ=1.5;}case WEST->{minX=-1.5;maxX=.5;minZ=-b;maxZ=-a;}default->{}}result=Shapes.or(result,Shapes.box(minX+core.getX()+.5-pos.getX(),core.getY()-pos.getY(),minZ+core.getZ()+.5-pos.getZ(),maxX+core.getX()+.5-pos.getX(),core.getY()+RAMP_HEIGHTS[i]-pos.getY(),maxZ+core.getZ()+.5-pos.getZ()));}return result;}
    @Override public Vec3 getSnappingPos(Level level,BlockPos pos,double x,double y,double z){return getTravelLocation(level,pos,x,y,z,0,0,0,0,new RailContext(),new MoveContext(RailCheckType.OTHER,0));}
    @Override public Vec3 getTravelLocation(Level level,BlockPos pos,double x,double y,double z,double mx,double my,double mz,double speed,RailContext info,MoveContext context){if(!full(level,pos))return new Vec3(x,y,z);int px=pos.getX(),py=pos.getY(),pz=pos.getZ();return switch(kind){
        case NARROW_STRAIGHT->SourceRailPaths.narrowStraight(this,level,px,py,pz,x,y,z,mx,my,mz,speed,info,context).nativeVector();
        case NARROW_CURVE->SourceRailPaths.narrowCurve(this,level,px,py,pz,x,y,z,mx,my,mz,speed,info,context).nativeVector();
        case STRAIGHT->SourceRailPaths.straight(this,level,px,py,pz,x,y,z,mx,my,mz,speed,info,context).nativeVector();
        case SHORT->SourceRailPaths.shortStraight(this,level,px,py,pz,x,y,z,mx,my,mz,speed,info,context).nativeVector();
        case CURVE,CURVE7,CURVE9->SourceRailPaths.curve(this,level,px,py,pz,x,y,z,mx,my,mz,speed,info,context).nativeVector();
        case RAMP->SourceRailPaths.ramp(this,level,px,py,pz,x,y,z,mx,my,mz,speed,info,context).nativeVector();
        case BUFFER->SourceRailPaths.buffer(this,level,px,py,pz,x,y,z,mx,my,mz,speed,info,context).nativeVector();
        case SWITCH,SWITCH_FLIPPED->SourceRailPaths.waypoint(this,level,px,py,pz,x,y,z,mx,my,mz,speed,info,context).nativeVector();
    };}
    private InteractionResult activate(BlockState state,Level level,BlockPos pos,Player player,ItemStack held){if(!kind.isSwitch())return InteractionResult.PASS; // All other source rails have no GUI/action.
        if(level.isClientSide)return InteractionResult.SUCCESS;
        if(player.isShiftKeyDown()||BuiltInRegistries.ITEM.getKey(held.getItem()).equals(SourceRailContent.id("train")))return InteractionResult.PASS;
        var core=findCore(level,pos);if(core!=null&&level.getBlockEntity(core) instanceof RailSwitchEntity rail)rail.toggle();return InteractionResult.CONSUME;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return activate(state,level,pos,player,player.getMainHandItem());}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){if(!kind.isSwitch())return super.useItemOn(stack,state,level,pos,player,hand,hit);var result=activate(state,level,pos,player,stack);return result==InteractionResult.PASS?ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION:ItemInteractionResult.sidedSuccess(level.isClientSide);}
}

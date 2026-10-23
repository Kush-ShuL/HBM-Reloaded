package com.hbm.pneumatic.tube;
import api.hbm.ntl.IPneumaticConnector;
import com.hbm.api.tool.ToolableBlock;
import com.hbm.fluid.HbmFluids;
import com.hbm.fluid.network.FluidUser;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
/** Original slim pipe+arms collision, full paintable cube, and real screwdriver endpoint cycling. */
public final class PneumoTubeBlock extends BaseEntityBlock implements ToolableBlock {
 public static final BooleanProperty HIDE_OVERLAY=BooleanProperty.create("hide_overlay");public final boolean paintable;
 public PneumoTubeBlock(boolean paintable){super(properties(paintable));this.paintable=paintable;registerDefaultState(stateDefinition.any().setValue(HIDE_OVERLAY,false));}
 private static Properties properties(boolean painted){var properties=Properties.of().mapColor(MapColor.METAL).strength(painted?5:2,6).sound(painted?SoundType.STONE:com.hbm.block.pipe.PipeSounds.TYPE).requiresCorrectToolForDrops();return painted?properties:properties.noOcclusion().dynamicShape();}
 @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(HIDE_OVERLAY);}@Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new PneumoTubeBlockEntity(pos,state);}@Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,PneumaticTubeContent.ENTITY.get(),PneumoTubeBlockEntity::tick);}
 public static boolean canPneumatic(BlockGetter world,BlockPos pos,Direction side){var tile=world.getBlockEntity(pos.relative(side));return tile instanceof PneumoTubeBlockEntity||tile instanceof IPneumaticConnector connector&&connector.canConnectPneumatic(side.getOpposite());}
 public static boolean canAir(BlockGetter world,BlockPos pos,Direction side){if(world.getBlockEntity(pos) instanceof PneumoTubeBlockEntity tube&&(!tube.isCompressor()||tube.insertionDir==side||tube.ejectionDir==side))return false;var other=world.getBlockEntity(pos.relative(side));return !(other instanceof PneumoTubeBlockEntity)&&other instanceof FluidUser fluid&&fluid.canConnect(HbmFluids.AIR,side.getOpposite());}
 public static int mask(BlockGetter world,BlockPos pos,boolean air){int mask=0;for(Direction side:Direction.values())if(canPneumatic(world,pos,side)||air&&canAir(world,pos,side))mask|=1<<side.get3DDataValue();return mask;}
 @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context){if(paintable)return Shapes.block();var shape=Block.box(5,5,5,11,11,11);for(Direction side:Direction.values())if(canPneumatic(world,pos,side)||canAir(world,pos,side)){int x0=side==Direction.WEST?0:5,y0=side==Direction.DOWN?0:5,z0=side==Direction.NORTH?0:5,x1=side==Direction.EAST?16:11,y1=side==Direction.UP?16:11,z1=side==Direction.SOUTH?16:11;shape=Shapes.or(shape,Block.box(x0,y0,z0,x1,y1,z1));}return shape;}
 @Override protected VoxelShape getShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context){if(paintable)return Shapes.block();int mask=mask(world,pos,true);if(world.getBlockEntity(pos) instanceof PneumoTubeBlockEntity tube){if(tube.insertionDir!=null)mask|=1<<tube.insertionDir.get3DDataValue();if(tube.ejectionDir!=null)mask|=1<<tube.ejectionDir.get3DDataValue();}return Block.box((mask&16)!=0?0:5,(mask&1)!=0?0:5,(mask&4)!=0?0:5,(mask&32)!=0?16:11,(mask&2)!=0?16:11,(mask&8)!=0?16:11);}
 @Override protected int getLightBlock(BlockState state,BlockGetter world,BlockPos pos){return paintable?15:0;}@Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter world,BlockPos pos){return !paintable;}@Override protected VoxelShape getOcclusionShape(BlockState state,BlockGetter world,BlockPos pos){return paintable?Shapes.block():Shapes.empty();}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return open(level,pos,player);}
 @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){var tool=ToolType.getType(stack);if(tool==ToolType.SCREWDRIVER||paintable&&(tool==ToolType.HAND_DRILL||tool==ToolType.DEFUSER))return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;if(paintable&&stack.getItem() instanceof BlockItem item&&level.getBlockEntity(pos) instanceof PneumoTubeBlockEntity tube&&tube.paint==null){var paint=item.getBlock().defaultBlockState();var properties=stack.get(DataComponents.BLOCK_STATE);if(properties!=null)paint=properties.apply(paint);if(!paint.is(Blocks.GRASS_BLOCK)&&!paint.is(this)&&paint.isSolidRender(level,pos)&&paint.getRenderShape()==RenderShape.MODEL){tube.paint=paint;tube.changed();return ItemInteractionResult.sidedSuccess(level.isClientSide);}}return open(level,pos,player).consumesAction()?ItemInteractionResult.sidedSuccess(level.isClientSide):ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;}
 private InteractionResult open(Level level,BlockPos pos,Player player){if(player.isShiftKeyDown()||ToolType.getType(player.getMainHandItem())==ToolType.SCREWDRIVER)return InteractionResult.PASS;if(level.getBlockEntity(pos) instanceof PneumoTubeBlockEntity tube&&(tube.isCompressor()||tube.isEndpoint())){if(player instanceof ServerPlayer server)server.openMenu(tube,buffer->{buffer.writeBlockPos(pos);buffer.writeBoolean(tube.isEndpoint()&&!tube.isCompressor());});return InteractionResult.sidedSuccess(level.isClientSide);}return InteractionResult.PASS;}
 @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float hitX,float hitY,float hitZ,ToolType tool){if(!(level.getBlockEntity(pos) instanceof PneumoTubeBlockEntity tube))return false;if(paintable&&tool==ToolType.HAND_DRILL){tube.paint=null;tube.changed();return false;}if(paintable&&tool==ToolType.DEFUSER){level.setBlock(pos,level.getBlockState(pos).cycle(HIDE_OVERLAY),3);return true;}if(tool!=ToolType.SCREWDRIVER)return false;if(level.isClientSide)return true;Direction rotated=player.isShiftKeyDown()?tube.ejectionDir:tube.insertionDir,other=player.isShiftKeyDown()?tube.insertionDir:tube.ejectionDir;for(int i=0;i<7;i++){int next=((rotated==null?6:rotated.get3DDataValue())+1)%7;rotated=next==6?null:Direction.from3DDataValue(next);if(rotated==null)break;if(rotated==other)continue;var target=level.getBlockEntity(pos.relative(rotated));if(target instanceof PneumoTubeBlockEntity)continue;if(target instanceof Container)break;}tube.directions(player.isShiftKeyDown()?tube.insertionDir:rotated,player.isShiftKeyDown()?rotated:tube.ejectionDir);return true;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){if(stack.has(DataComponents.CUSTOM_NAME)&&level.getBlockEntity(pos) instanceof PneumoTubeBlockEntity tube)tube.setName(stack.getHoverName());}
}

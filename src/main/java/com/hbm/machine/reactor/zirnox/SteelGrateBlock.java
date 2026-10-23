package com.hbm.machine.reactor.zirnox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.*;
/** Source BlockGrate, its eighth-block heights, protruding sneaking positions and wide-grate item permeability. */
public final class SteelGrateBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED=BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,9);private final boolean wide;
    public SteelGrateBlock(boolean wide){super(Properties.of().mapColor(MapColor.METAL).strength(2,5).requiresCorrectToolForDrops().noOcclusion().isRedstoneConductor((a,b,c)->false));this.wide=wide;registerDefaultState(stateDefinition.any().setValue(META,0).setValue(WATERLOGGED,false));}
    public static double y(BlockState state){int meta=state.getValue(META);return meta==9?-.125:meta*.125;}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META,WATERLOGGED);}
    private VoxelShape shape(BlockState state){double low=y(state);return box(0,low*16,0,16,(low+.125-(wide?.001:0))*16,16);}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context){return shape(state);}
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context){if(wide&&context instanceof EntityCollisionContext contextEntity&&(contextEntity.getEntity() instanceof ItemEntity||contextEntity.getEntity() instanceof ExperienceOrb)){var entity=contextEntity.getEntity();if(entity.getY()<pos.getY()+state.getValue(META)*.125+.375){entity.setDeltaMovement(0,-.25,0);entity.setPos(entity.getX(),entity.getY()-.125,entity.getZ());}return Shapes.empty();}return shape(state);}
    @Override protected VoxelShape getBlockSupportShape(BlockState state,BlockGetter world,BlockPos pos){return state.getValue(META)>=8?Shapes.empty():shape(state);}
    @Override protected VoxelShape getOcclusionShape(BlockState state,BlockGetter world,BlockPos pos){return Shapes.empty();}
    @Override protected int getLightBlock(BlockState state,BlockGetter world,BlockPos pos){return 0;}
    @Override protected float getShadeBrightness(BlockState state,BlockGetter world,BlockPos pos){return 1;}
    @Override public SoundType getSoundType(BlockState state,LevelReader level,BlockPos pos,Entity entity){var stone=SoundType.STONE;return new SoundType(.5F,1,stone.getBreakSound(),com.hbm.registry.HbmSounds.get("step.metalBlock"),stone.getPlaceSound(),stone.getHitSound(),com.hbm.registry.HbmSounds.get("step.metalBlock"));}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){int meta=context.getClickedFace()==Direction.DOWN?7:context.getClickedFace()==Direction.UP?0:(int)Math.floor((context.getClickLocation().y-context.getClickedPos().getY())*8);return defaultBlockState().setValue(META,Math.clamp(meta,0,7)).setValue(WATERLOGGED,context.getLevel().getFluidState(context.getClickedPos()).getType()==Fluids.WATER);}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){if(!placer.isShiftKeyDown())return;int meta=state.getValue(META);if(meta==0){var below=pos.below();var other=level.getBlockState(below);var shape=other.getCollisionShape(level,below);if(!other.isAir()&&(shape.isEmpty()||shape.max(Direction.Axis.Y)-1<-.05))level.setBlock(pos,state.setValue(META,9),3);}else if(meta==7){var above=pos.above();var other=level.getBlockState(above);var shape=other.getCollisionShape(level,above);if(!other.isAir()&&(shape.isEmpty()||shape.min(Direction.Axis.Y)>.05))level.setBlock(pos,state.setValue(META,8),3);}}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moving){if(level.isClientSide)return;int meta=state.getValue(META);boolean broken=false;if(meta==9){var below=pos.below();var shape=level.getBlockState(below).getCollisionShape(level,below);broken=!shape.isEmpty()&&shape.max(Direction.Axis.Y)-1>=-.05;}else if(meta==8){var above=pos.above();var shape=level.getBlockState(above).getCollisionShape(level,above);broken=!shape.isEmpty()&&shape.min(Direction.Axis.Y)<=.05;}if(broken){popResource(level,pos,new ItemStack(this));level.removeBlock(pos,false);}}

    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return List.of(new ItemStack(this));}
    public static final class GrateItem extends BlockItem {
        private final String sourceId;
        public GrateItem(Block block,String id){super(block,new Properties());sourceId=id;}
        @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<net.minecraft.network.chat.Component> lines,TooltipFlag flag){if(getBlock() instanceof SteelGrateBlock grate&&grate.wide)lines.add(net.minecraft.network.chat.Component.translatable("tile."+sourceId+".desc"));}
    }

    @Override protected FluidState getFluidState(BlockState state){
        return state.getValue(WATERLOGGED)?Fluids.WATER.getSource(false):super.getFluidState(state);
    }

    @Override protected BlockState updateShape(BlockState state,Direction direction,BlockState neighbor,LevelAccessor world,BlockPos pos,BlockPos neighborPos){
        if(state.getValue(WATERLOGGED))world.scheduleTick(pos,Fluids.WATER,Fluids.WATER.getTickDelay(world));
        return super.updateShape(state,direction,neighbor,world,pos,neighborPos);
    }
}

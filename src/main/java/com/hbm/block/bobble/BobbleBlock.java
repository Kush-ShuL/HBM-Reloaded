package com.hbm.block.bobble;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;

public final class BobbleBlock extends BaseEntityBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
    private static final VoxelShape SHAPE=box(5.5,0,5.5,10.5,10,10.5);
    public BobbleBlock(){this(Properties.of().mapColor(MapColor.METAL).strength(0,0).sound(SoundType.STONE).noOcclusion());}
    private BobbleBlock(Properties properties){super(properties);registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends BobbleBlock> codec(){return simpleCodec(BobbleBlock::new);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(META,Mth.floor((context.getRotation()+180)*16F/360F+.5D)&15);}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){if(level.getBlockEntity(pos) instanceof BobbleBlockEntity tile)tile.setType(BobbleContent.type(stack).ordinal());}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new BobbleBlockEntity(pos,state);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context){return SHAPE;}
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context){return SHAPE;}
    @Override protected VoxelShape getBlockSupportShape(BlockState state,BlockGetter world,BlockPos pos){return Shapes.empty();}
    @Override protected BlockState rotate(BlockState state,Rotation rotation){return state.setValue(META,(state.getValue(META)+rotation.ordinal()*4)&15);}
    @Override protected BlockState mirror(BlockState state,Mirror mirror){return state.setValue(META,mirror.mirror(state.getValue(META),16));}
    @Override public ItemStack getCloneItemStack(LevelReader world,BlockPos pos,BlockState state){return BobbleContent.stack(world.getBlockEntity(pos) instanceof BobbleBlockEntity tile?tile.type.ordinal():0);}
    @Override public BlockState playerWillDestroy(Level world,BlockPos pos,BlockState state,Player player){
        if(!player.getAbilities().instabuild&&!world.isClientSide&&world.getBlockEntity(pos) instanceof BobbleBlockEntity tile){ItemEntity drop=new ItemEntity(world,pos.getX()+.5,pos.getY(),pos.getZ()+.5,BobbleContent.stack(tile.type.ordinal()));drop.setDeltaMovement(0,0,0);world.addFreshEntity(drop);}
        return super.playerWillDestroy(world,pos,state,player);
    }
    @Override public void playerDestroy(Level world,Player player,BlockPos pos,BlockState state,BlockEntity entity,ItemStack tool){player.awardStat(Stats.BLOCK_MINED.get(this));player.causeFoodExhaustion(.025F);}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){return List.of();}
    private InteractionResult activate(Level world,BlockPos pos){if(world.isClientSide&&world.getBlockEntity(pos) instanceof BobbleBlockEntity tile)BobbleContent.openScreen.accept(tile);return InteractionResult.sidedSuccess(world.isClientSide);}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level world,BlockPos pos,Player player,BlockHitResult hit){return activate(world,pos);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level world,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){activate(world,pos);return ItemInteractionResult.sidedSuccess(world.isClientSide);}
}

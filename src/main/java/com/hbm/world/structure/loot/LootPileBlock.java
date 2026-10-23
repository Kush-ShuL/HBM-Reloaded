package com.hbm.world.structure.loot;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
/** The source loot pile is a physical 1/16 block high, not a chest or a decorative empty marker. */
public final class LootPileBlock extends BaseEntityBlock {
    public static final MapCodec<LootPileBlock> CODEC=simpleCodec(properties->new LootPileBlock());
    private static final VoxelShape SHAPE=Block.box(0,0,0,16,1,16);
    public LootPileBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(0,0).noOcclusion());}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new LootPileBlockEntity(pos,state);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.INVISIBLE;}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return SHAPE;}
    @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return SHAPE;}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;level.removeBlock(pos,false);return InteractionResult.CONSUME;}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){if(!state.is(next.getBlock())&&!level.isClientSide&&level.getBlockEntity(pos) instanceof LootPileBlockEntity pile)for(var item:pile.items())if(item.quantity()>0)level.addFreshEntity(new ItemEntity(level,pos.getX()+.5,pos.getY(),pos.getZ()+.5,item.drop()));super.onRemove(state,level,pos,next,moving);}
}

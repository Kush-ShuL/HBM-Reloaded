package com.hbm.item.bobmazondeps;

import com.hbm.registry.HbmSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

/** Source cloth full-cube physics,11tick local-client squish, and exact Hundun/normal sound branches. */
public final class PlushieBlock extends TrinketBlock {
 public PlushieBlock(){this(Properties.of().mapColor(MapColor.WOOL).strength(0,0).sound(SoundType.WOOL).noOcclusion().isRedstoneConductor((state,level,pos)->false).isSuffocating((state,level,pos)->false).isViewBlocking((state,level,pos)->false).isValidSpawn((state,level,pos,type)->false));}
 private PlushieBlock(Properties properties){super(properties);}
 @Override protected MapCodec<? extends PlushieBlock> codec(){return simpleCodec(PlushieBlock::new);}
 @Override protected boolean snowglobe(){return false;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new PlushieEntity(pos,state);}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,BobmazonDependenciesContent.PLUSH_ENTITY.get(),(world,pos,current,tile)->tile.tick());}
 @Override protected BlockState rotate(BlockState state,Rotation rotation){return state.setValue(META,(state.getValue(META)+rotation.ordinal()*4)&15);}
 @Override protected BlockState mirror(BlockState state,Mirror mirror){return state.setValue(META,mirror.mirror(state.getValue(META),16));}
 private InteractionResult activate(Level level,BlockPos pos){if(level.getBlockEntity(pos)instanceof PlushieEntity tile){if(level.isClientSide)tile.squishTimer=11;else{boolean hundun=tile.type==TrinketTypes.Plushie.HUNDUN;level.playSound(null,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,HbmSounds.get(hundun?"block.hunduns_magnificent_howl":"block.squeakyToy"),SoundSource.BLOCKS,hundun?100:.25F,1);}}return InteractionResult.sidedSuccess(level.isClientSide);}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return activate(level,pos);}
 @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){activate(level,pos);return ItemInteractionResult.sidedSuccess(level.isClientSide);}
}

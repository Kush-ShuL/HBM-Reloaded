package com.hbm.block.junglemechanics;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.storage.loot.LootParams;
/** BlockGlyph: plain brick top/bottom,16 authored side textures and raw metadata drops. */
public final class JungleGlyphBlock extends Block implements JungleMetadataBlock {
 public JungleGlyphBlock(){super(JungleMechanicsContent.properties(0));registerDefaultState(stateDefinition.any().setValue(JungleMechanicsContent.META,0));}
 @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(JungleMechanicsContent.META);}
 @Override public BlockState fromLegacy(int metadata){return defaultBlockState().setValue(JungleMechanicsContent.META,metadata);}@Override public int sourceVariants(){return 16;}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){return JungleDrops.drops(this,state,params,state.getValue(JungleMechanicsContent.META),true);}
 @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){return JungleMechanicsContent.stack(asItem(),state.getValue(JungleMechanicsContent.META));}
}

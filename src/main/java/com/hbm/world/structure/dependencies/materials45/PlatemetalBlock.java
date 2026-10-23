package com.hbm.world.structure.dependencies.materials45;
import com.hbm.hazard.HbmHazards;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
/** Source BlockEnumMultiCT, full four-bit world state and15 actual source variants. CT connects across color. */
public final class PlatemetalBlock extends Block {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
 public static final List<String> NAMES=List.of("base","black","white","red","green","light_gray","blue","purple","cyan","pink","lime","yellow","light_blue","magenta","orange");
 public PlatemetalBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(StructuralMaterials45Content.PLATE_SOUND).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(META,0));}
 @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 public static int rectify(int metadata){return Math.abs(metadata%NAMES.size());}
 public BlockState fromLegacy(int metadata){return defaultBlockState().setValue(META,metadata&15);}
 public ItemStack stack(int metadata,int count){var stack=new ItemStack(this,count);stack.set(HbmHazards.LEGACY_METADATA.get(),rectify(metadata));return stack;}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){return fromLegacy(context.getItemInHand().getOrDefault(HbmHazards.LEGACY_METADATA.get(),0));}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return List.of(stack(state.getValue(META),1));}
 @Override public ItemStack getCloneItemStack(LevelReader world,BlockPos pos,BlockState state){return stack(state.getValue(META),1);}
}

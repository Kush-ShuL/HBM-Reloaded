package com.hbm.world.structure.dependencies.materials45;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
/** Source BlockNTMGlassCT(1,glass_ash,Material.glass): true translucent CT, solid collision, silk-only drop. */
public final class AshGlassBlock extends HalfTransparentBlock {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
 public AshGlassBlock(){super(Properties.of().mapColor(MapColor.NONE).strength(3,3).sound(SoundType.GLASS).noOcclusion().isValidSpawn((s,w,p,t)->false).isViewBlocking((s,w,p)->false).isSuffocating((s,w,p)->false));registerDefaultState(stateDefinition.any().setValue(META,0));}
 @Override public MapCodec<? extends HalfTransparentBlock> codec(){return MapCodec.unit(this);}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 public BlockState fromLegacy(int metadata){return defaultBlockState().setValue(META,metadata&15);}
 @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter world,BlockPos pos){return true;}
 @Override protected int getLightBlock(BlockState state,BlockGetter world,BlockPos pos){return 0;}
 @Override protected float getShadeBrightness(BlockState state,BlockGetter world,BlockPos pos){return 1F;}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){var tool=context.getOptionalParameter(LootContextParams.TOOL);if(tool==null)return List.of();var registry=context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);return EnchantmentHelper.getItemEnchantmentLevel(registry.getOrThrow(Enchantments.SILK_TOUCH),tool)>0?List.of(new ItemStack(this)):List.of();}
}

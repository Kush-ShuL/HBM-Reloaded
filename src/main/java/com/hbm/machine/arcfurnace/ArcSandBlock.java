package com.hbm.machine.arcfurnace;
import com.hbm.hazard.HbmHazards;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
/** Source BlockNTMSand: five exact authored textures, metadata and two-tick falling behavior. */
public final class ArcSandBlock extends FallingBlock {
 public static final List<String> NAMES=List.of("boron","lead","uranium","polonium","quartz");public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,4);
 public ArcSandBlock(){super(Properties.of().mapColor(MapColor.SAND).strength(.5F).sound(SoundType.SAND));registerDefaultState(stateDefinition.any().setValue(META,0));}
 @Override protected MapCodec<? extends FallingBlock> codec(){return MapCodec.unit(this);}@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){return fromLegacy(context.getItemInHand().getOrDefault(HbmHazards.LEGACY_METADATA.get(),0));}
 public BlockState fromLegacy(int meta){return defaultBlockState().setValue(META,Math.abs(meta%5));}
 @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){return ArcFurnaceContent.SAND_ITEM.get().stack(state.getValue(META));}
 public static final class SandItem extends BlockItem {
  public SandItem(ArcSandBlock block){super(block,new Properties().component(HbmHazards.LEGACY_METADATA.get(),0));}
  public ItemStack stack(int variant){var result=new ItemStack(this);result.set(HbmHazards.LEGACY_METADATA.get(),variant);return result;}
  @Override public Component getName(ItemStack stack){int meta=stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);return Component.translatable("block.hbm.sand_"+NAMES.get(Math.abs(meta%5)));}
 }
}

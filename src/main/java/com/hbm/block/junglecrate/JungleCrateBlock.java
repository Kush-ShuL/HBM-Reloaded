package com.hbm.block.junglecrate;
import com.hbm.hazard.HbmHazards;
import com.hbm.registry.HbmContent;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
/** X5846 BlockJungleCrate: stationary stone full cube, no BE/inventory/crowbar interaction. */
public final class JungleCrateBlock extends Block {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);
 private final Random lootRandom=new Random(); // Original block-owned random, independent of world.rand.
 public JungleCrateBlock(){super(Properties.of().mapColor(MapColor.STONE).strength(1F,1.5F).sound(SoundType.STONE).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(META,0));}
 @Override protected MapCodec<? extends JungleCrateBlock> codec(){return simpleCodec(properties->new JungleCrateBlock());}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
 public BlockState fromLegacy(int metadata){return defaultBlockState().setValue(META,metadata&15);}
 private static ItemStack wire(String name,int count){var stack=new ItemStack(HbmContent.item(name),count);stack.set(HbmHazards.LEGACY_METADATA.get(),7900);return stack;} // Mats.MAT_GOLD.id
 /** Original seven item identities, source nextInt bounds/order/conditional amount draw; Fortune is intentionally unused. */
 public List<ItemStack> contents(){
  synchronized(lootRandom){var result=new ArrayList<ItemStack>();
   result.add(new ItemStack(Items.GOLD_INGOT,4+lootRandom.nextInt(4)));
   result.add(new ItemStack(Items.GOLD_NUGGET,8+lootRandom.nextInt(10)));
   result.add(new ItemStack(HbmContent.item("powder_gold"),2+lootRandom.nextInt(3)));
   result.add(wire("wire_fine",4+lootRandom.nextInt(5)));
   result.add(wire("wire_dense",1+lootRandom.nextInt(2)));
   if(lootRandom.nextInt(2)==0)result.add(new ItemStack(HbmContent.item("plate_gold"),1+lootRandom.nextInt(2)));
   if(lootRandom.nextInt(3)==0)result.add(new ItemStack(HbmContent.item("crystal_gold")));
   return result;
  }
 }
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){
  var tool=context.getOptionalParameter(LootContextParams.TOOL);if(tool==null)tool=ItemStack.EMPTY;
  // Original ordinary Block.harvestBlock takes its opaque/no-BE silk branch before BlockJungleCrate.getDrops.
  var enchantments=context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
  List<ItemStack> drops=EnchantmentHelper.getItemEnchantmentLevel(enchantments.getOrThrow(Enchantments.SILK_TOUCH),tool)>0?new ArrayList<>(List.of(new ItemStack(this))):contents();
  Float radius=context.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS);
  // Original Forge dropBlockAsItemWithChance checks per ItemStack, not per contained item.
  if(radius!=null)drops.removeIf(stack->context.getLevel().random.nextFloat()>1F/radius);
  return drops;
 }
}

package com.hbm.world.meteor;

import com.hbm.world.structure.loot.LegacyStructureLoot;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

/** Original BlockMeteoriteTreasure: 1..3 complete weighted ItemPool draws; Fortune is ignored. */
public final class MeteorTreasureBlock extends Block {
 public MeteorTreasureBlock(){super(Properties.of().mapColor(MapColor.STONE).strength(15,216).requiresCorrectToolForDrops());}
 @Override protected List<ItemStack> getDrops(net.minecraft.world.level.block.state.BlockState state,LootParams.Builder context){
  var level=context.getLevel();var tool=context.getOptionalParameter(LootContextParams.TOOL);var enchantments=level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
  if(tool!=null&&EnchantmentHelper.getItemEnchantmentLevel(enchantments.getOrThrow(Enchantments.SILK_TOUCH),tool)>0)return List.of(new ItemStack(this));
  int count=1+level.random.nextInt(3);var drops=new ArrayList<ItemStack>();for(int i=0;i<count;i++)drops.add(LegacyStructureLoot.sample(level,"POOL_METEORITE_TREASURE",level.random));
  Float radius=context.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS);if(radius!=null)drops.removeIf(stack->level.random.nextFloat()>1F/radius);return drops;
 }
}

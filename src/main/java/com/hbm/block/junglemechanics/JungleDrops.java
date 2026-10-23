package com.hbm.block.junglemechanics;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
/** Source normal Block.dropBlockAsItemWithChance versus silk createStackedBlock. */
final class JungleDrops {
 static List<ItemStack> drops(Block block,BlockState state,LootParams.Builder params,int normalMetadata,boolean normalDrop){var tool=params.getOptionalParameter(LootContextParams.TOOL);boolean silk=!(block instanceof JungleTrapBlock)&&tool!=null&&EnchantmentHelper.getItemEnchantmentLevel(params.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH),tool)>0;if(!normalDrop&&!silk)return List.of();var item=block.asItem();var stack=new ItemStack(item);if(item instanceof JungleMetaItem)stack.set(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),silk?state.getValue(JungleMechanicsContent.META):normalMetadata);Float radius=params.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS);return radius!=null&&params.getLevel().random.nextFloat()>1F/radius?List.of():List.of(stack);}
 private JungleDrops(){}
}

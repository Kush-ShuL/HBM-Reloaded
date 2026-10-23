package com.hbm.world.meteor;

import com.hbm.block.ore.LegacyOreBlock;
import com.hbm.registry.HbmContent;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

/** Source BlockOre molten identity: no ordinary drop, random cooling, burn-on-step and lava after player removal. */
public final class MoltenMeteorBlock extends LegacyOreBlock {
 public MoltenMeteorBlock(){super(MeteorImpactMaterials.spec("block_meteor_molten",null,0,11),MeteorImpactMaterials.spec("block_meteor_molten",null,0,11).properties().randomTicks());}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){var tool=context.getOptionalParameter(LootContextParams.TOOL);var enchantments=context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);return tool!=null&&EnchantmentHelper.getItemEnchantmentLevel(enchantments.getOrThrow(Enchantments.SILK_TOUCH),tool)>0?List.of(new ItemStack(this)):List.of();}
 @Override protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){level.setBlock(pos,HbmContent.block("block_meteor_cobble").defaultBlockState(),3);level.playSound(null,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,SoundEvents.FIRE_EXTINGUISH,SoundSource.BLOCKS,.5F,2.6F+(level.random.nextFloat()-level.random.nextFloat())*.8F);}
 @Override protected void randomTick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){tick(state,level,pos,random);}
 @Override public void stepOn(Level level,BlockPos pos,BlockState state,Entity entity){entity.igniteForSeconds(5);}
 @Override public boolean onDestroyedByPlayer(BlockState state,Level level,BlockPos pos,Player player,boolean harvest,FluidState fluid){boolean removed=super.onDestroyedByPlayer(state,level,pos,player,harvest,fluid);if(removed&&!level.isClientSide)level.setBlock(pos,Blocks.LAVA.defaultBlockState(),3);return removed;}
}

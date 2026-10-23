package com.hbm.world.structure.templates.materials;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
/** Original BlockNTMGlassCT(0,reinforced_glass,rock): no ordinary drop, exact silk self drop, opacity0 and full collision. */
public final class TemplateReinforcedGlass extends HalfTransparentBlock {
    public TemplateReinforcedGlass(){super(Properties.of().mapColor(MapColor.STONE).strength(2F,15F).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion().isViewBlocking((state,world,pos)->false).isSuffocating((state,world,pos)->false).isValidSpawn((state,world,pos,type)->false));}
    @Override protected MapCodec<? extends HalfTransparentBlock> codec(){return MapCodec.unit(this);}
    @Override protected int getLightBlock(BlockState state,BlockGetter world,BlockPos pos){return 0;}
    @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter world,BlockPos pos){return true;}
    @Override protected float getShadeBrightness(BlockState state,BlockGetter world,BlockPos pos){return 1F;}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){var tool=context.getOptionalParameter(LootContextParams.TOOL);if(tool==null)return List.of();var ench=context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);return EnchantmentHelper.getItemEnchantmentLevel(ench.getOrThrow(Enchantments.SILK_TOUCH),tool)>0?List.of(new ItemStack(this)):List.of();}
}

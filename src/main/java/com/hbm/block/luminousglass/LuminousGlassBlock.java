package com.hbm.block.luminousglass;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

/** Actual BlockNTMGlassCT(1,glass,false): source silk-only glass, full collision, emitted5 and zero opacity. */
public final class LuminousGlassBlock extends HalfTransparentBlock {
    public LuminousGlassBlock(){super(Properties.of().mapColor(MapColor.NONE).strength(.3F).sound(SoundType.GLASS)
        .lightLevel(state->5).noOcclusion().isValidSpawn((state,world,pos,type)->false)
        .isViewBlocking((state,world,pos)->false).isSuffocating((state,world,pos)->false));}
    @Override protected MapCodec<? extends HalfTransparentBlock> codec(){return MapCodec.unit(this);}
    @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}
    @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return true;}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){
        var tool=context.getOptionalParameter(LootContextParams.TOOL);
        if(tool==null)return List.of();
        var enchantments=context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        return EnchantmentHelper.getItemEnchantmentLevel(enchantments.getOrThrow(Enchantments.SILK_TOUCH),tool)>0?List.of(new ItemStack(this)):List.of();
    }
}

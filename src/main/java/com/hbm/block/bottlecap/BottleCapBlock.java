package com.hbm.block.bottlecap;

import com.hbm.block.ore.LegacyOreBlock;
import com.hbm.block.ore.OreSpec;
import com.hbm.hazard.HbmHazards;
import com.hbm.registry.HbmContent;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

/** Actual X5846 BlockCap: six ItemBlockBase subtypes, 128 ordinary cap drops, no Fortune multiplier. */
public final class BottleCapBlock extends LegacyOreBlock {
    public static final List<String> NAMES=List.of("nuka","quantum","sparkle","rad","korl","fritz");
    private static final OreSpec SOURCE=new OreSpec("block_cap","BlockCap","iron","stone",5,6,0,true,true,
        null,null,0,128,0,"NONE",0,NAMES,"COMMON",false,0,false,false);
    public BottleCapBlock(){super(SOURCE,Properties.of().mapColor(MapColor.METAL).strength(5,6)
        .sound(SoundType.STONE).requiresCorrectToolForDrops());}
    @Override protected MapCodec<? extends LegacyOreBlock> codec(){return MapCodec.unit(this);}

    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){
        ItemStack tool=context.getOptionalParameter(LootContextParams.TOOL);
        var registry=context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        if(tool!=null&&EnchantmentHelper.getItemEnchantmentLevel(registry.getOrThrow(Enchantments.SILK_TOUCH),tool)>0){
            ItemStack result=new ItemStack(this);
            // Original Block.createStackedBlock retains raw world damage for subtype ItemBlockBase.
            result.set(HbmHazards.LEGACY_METADATA.get(),state.getValue(LEGACY_META));
            return List.of(result);
        }
        int metadata=spec.ordinal(state.getValue(LEGACY_META));
        var item=HbmContent.item("cap_"+NAMES.get(metadata));
        List<ItemStack> result=new ArrayList<>(128);
        // Separate original 1-count drops, not an invalid over-max-size cap stack or a 9-cap compression recipe.
        for(int index=0;index<128;index++){
            ItemStack cap=new ItemStack(item);
            // BlockMulti.damageDropped rectifies the source block meta even when the dropped plain Item has no subtypes.
            if(metadata!=0)cap.set(HbmHazards.LEGACY_METADATA.get(),metadata);
            result.add(cap);
        }
        Float radius=context.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS);
        if(radius!=null){float chance=1F/radius;result.removeIf(stack->context.getLevel().random.nextFloat()>chance);}
        return result;
    }
}

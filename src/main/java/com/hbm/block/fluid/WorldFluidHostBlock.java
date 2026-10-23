package com.hbm.block.fluid;

import java.util.ArrayList;
import java.util.List;
import com.hbm.block.ore.LegacyOreBlock;
import com.hbm.block.ore.OreSpec;
import com.hbm.registry.HbmContent;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

/** Three actual source reaction products; basalt belongs to HbmContent. */
public final class WorldFluidHostBlock extends LegacyOreBlock {
    public WorldFluidHostBlock(String symbol) { super(specification(symbol)); }
    private static OreSpec specification(String id) {
        boolean wood = !id.equals("sellafield_slaked"), log = id.equals("waste_log");
        return new OreSpec(id, log ? "WasteLog" : wood ? "BlockOre" : "BlockSellafieldSlaked", wood ? "wood" : "rock", wood ? "wood" : "stone",
                id.equals("waste_planks") ? .5F : 5F, wood ? 1.5F : 5F, 0, !wood, true,
                wood ? "minecraft:charcoal" : null, null, 0, log ? 2 : 1, log ? 3 : 0, log ? "NONE" : "ORE", 0,
                List.of(), "COMMON", id.equals("waste_planks"), 0, false, false);
    }
    @Override protected List<ItemStack> getDrops(BlockState state, LootParams.Builder context) {
        if (!spec.id().equals("waste_log")) return super.getDrops(state, context);
        ItemStack tool = context.getOptionalParameter(LootContextParams.TOOL);
        if (tool == null) tool = ItemStack.EMPTY;
        var enchantments = context.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        if (EnchantmentHelper.getItemEnchantmentLevel(enchantments.getOrThrow(Enchantments.SILK_TOUCH), tool) > 0) return List.of(new ItemStack(this));
        var random = context.getLevel().random;
        List<ItemStack> drops = new ArrayList<>();
        if (random.nextInt(1000) == 0) drops.add(new ItemStack(HbmContent.item("burnt_bark")));
        else { int quantity = 2 + random.nextInt(3); for (int i = 0; i < quantity; i++) drops.add(new ItemStack(Items.CHARCOAL)); }
        Float radius = context.getOptionalParameter(LootContextParams.EXPLOSION_RADIUS);
        if (radius != null) drops.removeIf(ignored -> random.nextFloat() > 1F / radius);
        return drops;
    }
}

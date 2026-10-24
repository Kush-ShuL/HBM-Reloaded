package com.hbm.item.bedrockprocessing;

import java.util.Locale;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** One fixed ore identity. The overlay tint is the original EnumBedrockOre color. */
public final class EnrichedBedrockItem extends Item {
    private final int sourceOrdinal;
    public EnrichedBedrockItem(int sourceOrdinal) {
        super(new Properties());
        if (sourceOrdinal < 0 || sourceOrdinal >= BedrockProcessingData.DATA.oldOres().size()) throw new IllegalArgumentException("Unknown enriched ore " + sourceOrdinal);
        this.sourceOrdinal = sourceOrdinal;
    }
    public int color(int pass) { return pass == 1 ? BedrockProcessingData.DATA.oldOres().get(sourceOrdinal).color() : 0xffffff; }
    @Override public String getDescriptionId(ItemStack stack) { return "item.ore_enriched"; }
    @Override public Component getName(ItemStack stack) {
        var ore = BedrockProcessingData.DATA.oldOres().get(sourceOrdinal);
        return Component.translatable("item.ore_enriched.name", Component.translatable("item.ore." + ore.oreName().toLowerCase(Locale.ROOT)));
    }
}

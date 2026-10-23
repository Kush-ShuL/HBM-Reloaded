package com.hbm.item.meteorbook;

import com.hbm.hazard.HbmHazards;
import com.hbm.item.StampItem;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** Original ItemStampBook: the same reusable item has eight actual press stamp types. */
public final class PrintingStampItem extends StampItem {
    public PrintingStampItem() {
        super(0, 1, null);
        for (int metadata = 0; metadata < 8; metadata++) addStampVariant(metadata, getStampType(this, metadata));
    }
    @Override public StampType getStampType(Item item, int metadata) {
        return StampType.values()[StampType.PRINTING1.ordinal() + metadata % 8];
    }
    public static int metadata(ItemStack stack) { return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), stack.getDamageValue()); }
    @Override public Component getName(ItemStack stack) {
        return Component.translatable("item.stamp_book." + getStampType(this, metadata(stack)).name().toLowerCase(Locale.US) + ".name");
    }
    public List<ItemStack> variants() {
        return IntStream.range(0, 8).mapToObj(metadata -> { ItemStack stack = new ItemStack(this); stack.set(HbmHazards.LEGACY_METADATA.get(), metadata); return stack; }).toList();
    }
}

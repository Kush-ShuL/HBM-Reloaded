package com.hbm.item;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class StampItem extends Item {
    public enum StampType { FLAT, PLATE, WIRE, CIRCUIT, C357, C44, C50, C9,
                            PRINTING1, PRINTING2, PRINTING3, PRINTING4, PRINTING5, PRINTING6, PRINTING7, PRINTING8 }
    private static final EnumMap<StampType, List<StampItem>> STAMPS = new EnumMap<>(StampType.class);
    private static final EnumMap<StampType, List<Variant>> VARIANTS = new EnumMap<>(StampType.class);
    private record Variant(StampItem item, int metadata) {}
    private final StampType type;

    public StampItem(int durability, int stackSize, StampType type) {
        super(properties(durability, stackSize));
        this.type = type;
        if (type != null) STAMPS.computeIfAbsent(type, ignored -> new ArrayList<>()).add(this);
    }

    private static Properties properties(int durability, int stackSize) {
        Properties properties = new Properties().stacksTo(stackSize);
        if (durability > 0) properties.durability(durability);
        return properties;
    }

    public StampType getStampType(Item item, int metadata) { return type; }
    public StampType stampType() { return type; }
    /** Source ItemStamp.getStampType uses the actual stack damage/metadata, while ordinary stamps ignore it. */
    public StampType stampType(ItemStack stack) {
        return getStampType(stack.getItem(), stack.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(), stack.getDamageValue()));
    }
    protected final void addStampVariant(int metadata, StampType type) {
        var variants = VARIANTS.computeIfAbsent(type, ignored -> new ArrayList<>());
        if (variants.stream().noneMatch(variant -> variant.item == this && variant.metadata == metadata)) variants.add(new Variant(this, metadata));
    }
    public static List<ItemStack> stamps(StampType type) {
        var result = new ArrayList<ItemStack>();
        STAMPS.getOrDefault(type, List.of()).forEach(item -> result.add(new ItemStack(item)));
        for (var variant : VARIANTS.getOrDefault(type, List.of())) {
            ItemStack stack = new ItemStack(variant.item);
            stack.set(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(), variant.metadata);
            if (result.stream().noneMatch(previous -> ItemStack.isSameItemSameComponents(previous, stack))) result.add(stack);
        }
        return List.copyOf(result);
    }
}

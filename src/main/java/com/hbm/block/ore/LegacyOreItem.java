package com.hbm.block.ore;

import java.util.ArrayList;
import java.util.List;
import com.hbm.hazard.HbmHazards;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;

/** Original ItemBlock metadata naming, placement, middle click and creative variants. */
public class LegacyOreItem extends BlockItem {
    private final OreSpec spec;
    public LegacyOreItem(LegacyOreBlock block) {
        super(block, properties(block.spec()));
        spec = block.spec();
    }
    private static Item.Properties properties(OreSpec spec) {
        Item.Properties properties = new Item.Properties().rarity(Rarity.valueOf(spec.rarity()));
        if (spec.multi()) properties.component(HbmHazards.LEGACY_METADATA.get(), 0);
        return properties;
    }
    public OreSpec spec() { return spec; }
    public int ordinal(ItemStack stack) { return spec.ordinal(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0)); }

    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        BlockState state = super.getPlacementState(context);
        int metadata = spec.multi() ? context.getItemInHand().getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0) & 15 : 0;
        return state == null ? null : state.setValue(LegacyOreBlock.LEGACY_META, metadata);
    }

    @Override public String getDescriptionId(ItemStack stack) {
        return "block.hbm." + spec.id() + (spec.multi() ? (spec.id().equals("ore_basalt") ? "_" : ".") + spec.variants().get(ordinal(stack)) : "");
    }

    public List<ItemStack> creativeStacks() {
        List<ItemStack> result = new ArrayList<>();
        for (int i = 0; i < Math.max(1, spec.variants().size()); i++) {
            ItemStack stack = new ItemStack(this);
            if (spec.multi()) stack.set(HbmHazards.LEGACY_METADATA.get(), i);
            result.add(stack);
        }
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        if (spec.depth()) lines.add(Component.translatable("trait.tile.depth").withStyle(ChatFormatting.YELLOW));
        if (spec.id().equals("ore_australium")) {
            lines.add(Component.literal("Australium ore"));
            lines.add(Component.literal("Deposit location: X:-400; Z:-400"));
            lines.add(Component.literal("Estimated quantity: 490"));
        }
    }
}

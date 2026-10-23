package com.hbm.item.sourcesnacks;

import com.hbm.hazard.HbmHazards;
import com.hbm.item.structureloot.StructureFoodItem;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Three raw metadata prices share the source texture/name and heal exactly two health. */
public final class ItemTemFlakes extends StructureFoodItem {
    public ItemTemFlakes() { super("tem_flakes", 0, 0, 64, true); }
    public static int metadata(ItemStack stack) { return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0); }
    public ItemStack stack(int metadata) {
        var stack = new ItemStack(this);
        stack.set(HbmHazards.LEGACY_METADATA.get(), metadata);
        return stack;
    }
    @Override protected void onFoodEaten(ItemStack stack, Level level, Player player) {
        // Source has no side guard: retain both logical sides, not a second healing event.
        player.heal(2F);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        int metadata = metadata(stack);
        if (metadata >= 0 && metadata <= 2) lines.add(Component.translatable("tooltip.hbm.tem_flakes." + metadata));
    }
}

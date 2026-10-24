package com.hbm.item.sourcesnacks;

import com.hbm.item.structureloot.StructureFoodItem;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Three immutable source prices share the original texture/name and heal exactly two health. */
public final class ItemTemFlakes extends StructureFoodItem {
    private final int price;
    public ItemTemFlakes(int price) {
        super("tem_flakes", 0, 0, 64, true);
        if (price < 0 || price > 2) throw new IllegalArgumentException("Not an actual source Tem price: " + price);
        this.price = price;
    }
    @Override protected void onFoodEaten(ItemStack stack, Level level, Player player) {
        // Source has no side guard: retain both logical sides, not a second healing event.
        player.heal(2F);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.tem_flakes." + price));
    }
}

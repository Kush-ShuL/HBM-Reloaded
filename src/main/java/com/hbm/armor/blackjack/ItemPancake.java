package com.hbm.armor.blackjack;

import com.hbm.armor.t51.T51Runtime;
import com.hbm.item.battery.HbmBattery;
import com.hbm.item.structureloot.StructureFoodItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Source pancake is only edible in a matching full FSB set with the actual BJ helmet. */
public final class ItemPancake extends StructureFoodItem {
    public ItemPancake() { super("pancake", 20, 20F, 64, true); }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (T51Runtime.fullSet(player, false) && player.getItemBySlot(EquipmentSlot.HEAD).is(BlackjackContent.HELMET.get())) return super.use(level, player, hand);
        if (!level.isClientSide) player.displayClientMessage(Component.translatable("message.hbm.pancake.soft_teeth").withStyle(ChatFormatting.YELLOW), false);
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }
    @Override protected void onFoodEaten(ItemStack stack, Level level, Player player) {
        // No source-side guard: retain both logical sides and every actual worn IBatteryItem, not only this family.
        for (var slot : T51Runtime.ORDER) {
            var armor = player.getItemBySlot(slot);
            if (armor.getItem() instanceof HbmBattery battery) battery.setCharge(armor, battery.getMaxCharge(armor));
        }
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.pancake.charge"));
        lines.add(Component.translatable("tooltip.hbm.pancake.molars"));
        lines.add(Component.empty());
        lines.add(Component.translatable("tooltip.hbm.pancake.burnt"));
    }
}

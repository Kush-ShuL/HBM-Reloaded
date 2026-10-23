package com.hbm.armor.gasmaskmods;

import com.hbm.armor.ArmorModItem;
import com.hbm.armor.ArmorModStorage;
import com.hbm.armor.GasMask;
import com.hbm.armor.GasMaskFilters;
import com.hbm.exposure.ExposureProtection.Hazard;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Original two attachable masks, using the unique hfrFilter/recursive installed-armor backend. */
public final class ItemModGasmask extends ArmorModItem implements GasMask {
    public final boolean mono;
    public ItemModGasmask(boolean mono) {
        super(ArmorModStorage.HELMET, true, false, false, false);
        this.mono = mono;
    }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item." + (mono ? "attachment_mask_mono" : "attachment_mask") + ".name"); }
    @Override public List<Hazard> blacklist(ItemStack stack, LivingEntity wearer) {
        return mono ? List.of(Hazard.GAS_LUNG, Hazard.GAS_BLISTERING, Hazard.BACTERIA) : List.of(Hazard.GAS_BLISTERING);
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            var filter = filter(stack, player);
            if (!filter.isEmpty()) {
                GasMaskFilters.remove(stack);
                if (!player.getInventory().add(filter)) player.drop(filter, true);
            }
        }
        // Source Item.onItemRightClick is otherwise a no-op; the attachment never equips itself as armor.
        return InteractionResultHolder.pass(stack);
    }
    @Override public Component installedDescription(ItemStack stack) {
        return Component.translatable("tooltip.hbm.gasmask_attachment.installed", stack.getHoverName()).withStyle(ChatFormatting.GREEN);
    }
    @Override public void appendInstalledDetails(ItemStack stack, ItemStack armor, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        GasMaskFilters.tooltip(stack, context, lines, flags);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.gasmask_attachment.protection").withStyle(ChatFormatting.GREEN));
        lines.add(Component.empty()); super.appendHoverText(stack, context, lines, flags);
        GasMaskFilters.tooltip(stack, context, lines, flags);
        lines.add(Component.translatable("hazard.neverProtects").withStyle(ChatFormatting.RED));
        if (mono) lines.add(Component.literal(" -").append(Component.translatable("hazard.gasChlorine")).withStyle(ChatFormatting.DARK_RED));
        lines.add(Component.literal(" -").append(Component.translatable("hazard.corrosive")).withStyle(ChatFormatting.DARK_RED));
        if (mono) lines.add(Component.literal(" -").append(Component.translatable("hazard.bacteria")).withStyle(ChatFormatting.DARK_RED));
    }
}

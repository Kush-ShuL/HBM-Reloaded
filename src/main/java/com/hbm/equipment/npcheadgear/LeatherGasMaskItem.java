package com.hbm.equipment.npcheadgear;

import com.hbm.armor.*;
import com.hbm.exposure.ExposureProtection.Hazard;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/** Actual source olde uses ModelM65 with mask_olde PNG; no source helmet overlay. */
public final class LeatherGasMaskItem extends NpcHeadgearItem implements GasMask {
    public LeatherGasMaskItem() { super(ArmorMaterials.IRON, 165, "gas_mask_olde", "textures/armor/mask_olde.png"); }
    @Override public List<Hazard> blacklist(ItemStack stack, LivingEntity wearer) { return List.of(Hazard.GAS_BLISTERING); }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            var filter = filter(stack, player);
            if (!filter.isEmpty()) {
                GasMaskFilters.remove(stack);
                if (!player.getInventory().add(filter)) player.drop(filter, true);
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
            }
        }
        return super.use(level, player, hand);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        GasMaskFilters.tooltip(stack, context, lines, flags);
        lines.add(Component.translatable("hazard.neverProtects").withStyle(ChatFormatting.RED));
        lines.add(Component.literal(" -").append(Component.translatable("hazard.corrosive")).withStyle(ChatFormatting.DARK_RED));
    }
}

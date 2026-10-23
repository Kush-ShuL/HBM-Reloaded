package com.hbm.armor.paahazmat;

import com.hbm.armor.GasMask;
import com.hbm.armor.GasMaskFilters;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Actual ArmorHazmatMask: full hood accepts every real filter and has no hazard blacklist. */
public final class PaaHazmatMaskItem extends PaaHazmatArmorItem implements GasMask {
    public PaaHazmatMaskItem() { super(Type.HELMET, "hazmat_paa_helmet"); }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var held = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            var filter = filter(held, player);
            if (!filter.isEmpty()) {
                GasMaskFilters.remove(held);
                if (!player.getInventory().add(filter)) player.drop(filter, true);
                return InteractionResultHolder.sidedSuccess(held, level.isClientSide);
            }
        }
        return super.use(level, player, hand);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        GasMaskFilters.tooltip(stack, context, lines, flag);
        super.appendHoverText(stack, context, lines, flag);
    }
}

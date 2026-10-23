package com.hbm.item.originalgasmask;

import com.hbm.armor.GasMask;
import com.hbm.armor.GasMaskFilters;
import com.hbm.equipment.vaultclothing.VaultArmorItem;
import com.hbm.exposure.ExposureProtection.Hazard;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** The original ArmorGasMask gas_mask branch, not the differently shaped M65. */
public final class OriginalGasMaskItem extends VaultArmorItem implements GasMask {
    public OriginalGasMaskItem() {
        super(ArmorMaterials.IRON, Type.HELMET, 15, "gas_mask", "textures/models/gasmask.png");
    }
    @Override public List<Hazard> blacklist(ItemStack mask, LivingEntity wearer) { return List.of(Hazard.GAS_BLISTERING); }
    @Override public boolean isValidRepairItem(ItemStack armor, ItemStack ingredient) { return ingredient.is(Items.IRON_INGOT); }

    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            ItemStack installed = filter(held, player);
            if (!installed.isEmpty()) {
                GasMaskFilters.remove(held);
                if (!player.getInventory().add(installed)) player.drop(installed, true);
                return InteractionResultHolder.sidedSuccess(held, level.isClientSide);
            }
        }
        return super.use(level, player, hand);
    }

    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        GasMaskFilters.tooltip(stack, context, lines, flag);
        lines.add(Component.translatable("hazard.neverProtects").withStyle(ChatFormatting.RED));
        lines.add(Component.literal(" -").append(Component.translatable("hazard.corrosive")).withStyle(ChatFormatting.DARK_RED));
    }
}

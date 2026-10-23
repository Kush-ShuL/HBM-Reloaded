package com.hbm.item.canteen;

import com.hbm.LegacyStartup;
import com.hbm.item.medicine.MedicineEffects;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/** Source damage is a refill cooldown, not a consumable durability budget. */
public final class ItemCanteen extends Item {
    public static final int COOLDOWN = 3 * 60;
    public ItemCanteen() { super(new Properties().durability(COOLDOWN)); }
    // Legacy Item.setMaxDamage does not change Item's default stack limit.
    // Native damageability keeps damaged stacks from merging; zero-damage stacks can merge.
    @Override public int getMaxStackSize(ItemStack stack) { return 64; }
    @Override public void inventoryTick(ItemStack stack, Level world, Entity owner, int slot, boolean selected) {
        if (stack.getDamageValue() > 0 && owner.tickCount % 20 == 0)
            stack.setDamageValue(stack.getDamageValue() - 1);
    }
    @Override public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getDamageValue() != 0 || MedicineEffects.hasPotionSickness(player))
            return InteractionResultHolder.pass(stack);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.DRINK; }
    @Override public int getUseDuration(ItemStack stack, LivingEntity owner) { return 10; }
    @Override public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity consumer) {
        if (!(consumer instanceof Player player)) return stack;
        stack.setDamageValue(COOLDOWN);
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 10 * 20, 0));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30 * 20, 2));
        MedicineEffects.potionSickness(player, 5);
        return stack;
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.canteen.cooldown"));
        lines.add(Component.translatable("tooltip.hbm.canteen.nausea"));
        lines.add(Component.translatable("tooltip.hbm.canteen.strength"));
        lines.add(Component.empty());
        lines.add(Component.translatable(LegacyStartup.polaroidId == 11
            ? "tooltip.hbm.canteen.description.P11" : "tooltip.hbm.canteen.description"));
    }
}

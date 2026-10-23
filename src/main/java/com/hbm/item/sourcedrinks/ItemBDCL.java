package com.hbm.item.sourcedrinks;

import com.hbm.item.sourcedrinks.mixin.SourceDrinkUseAccess;
import com.hbm.registry.HbmSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/** Actual ItemBDCL, including direct source itemInUseCount acceleration/early finish. */
public final class ItemBDCL extends Item {
    public ItemBDCL() { super(new Properties()); }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item.bdcl.name"); }
    @Override public int getUseDuration(ItemStack stack, LivingEntity consumer) { return 40; }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.DRINK; }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }
    @Override public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity consumer) {
        if (consumer instanceof Player player && !player.getAbilities().instabuild) stack.shrink(1);
        return stack;
    }
    @Override public void onUseTick(Level level, LivingEntity consumer, ItemStack stack, int count) {
        if (!(consumer instanceof Player player)) return;
        if (count % 5 == 0 && count >= 10) player.playSound(HbmSounds.get("player.gulp"), 1F, 1F);
        if (count == 1) {
            finishUsingItem(stack, level, player);
            player.stopUsingItem();
            ((SourceDrinkUseAccess) player).hbm$sourceDrinkRemaining(10);
            player.playSound(HbmSounds.get("player.groan"), 1F, 1F);
            return;
        }
        if (count <= 24 && count % 4 == 0) ((SourceDrinkUseAccess) player).hbm$sourceDrinkRemaining(count - 1);
    }
}

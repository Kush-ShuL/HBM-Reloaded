package com.hbm.item.lemegeton;

import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/** Actual ItemBookLemegeton: hidden, one-stack item and the real server-owned one-cell conversion menu. */
public final class LemegetonItem extends Item {
    public LemegetonItem() { super(new Properties().stacksTo(1)); }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item.book_lemegeton.name"); }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) player.openMenu(new SimpleMenuProvider((id, inventory, viewer) -> new LemegetonMenu(id, inventory), getName(player.getItemInHand(hand))));
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }
}

package com.hbm.item.plasticbag;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class PlasticBagItem extends Item {
    public PlasticBagItem() { super(new Properties().stacksTo(1)); }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item.plastic_bag.name"); }
    @Override public int getUseDuration(ItemStack stack, LivingEntity consumer) { return 1; }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer server) server.openMenu(new SimpleMenuProvider(
                (id, inventory, viewer) -> new PlasticBagMenu(id, inventory, hand),
                stack.has(DataComponents.CUSTOM_NAME) ? stack.getHoverName() : Component.translatable("container.plasticBag")),
                buffer -> buffer.writeEnum(hand));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}

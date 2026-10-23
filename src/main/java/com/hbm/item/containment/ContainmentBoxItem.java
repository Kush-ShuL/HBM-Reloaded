package com.hbm.item.containment;

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

/** Original ItemLeadBox: one held item, twenty single-item cells, and the real source inventory GUI. */
public final class ContainmentBoxItem extends Item {
    public ContainmentBoxItem(){super(new Properties().stacksTo(1));}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.containment_box.name");}
    @Override public int getUseDuration(ItemStack stack,LivingEntity holder){return 1;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var stack=player.getItemInHand(hand);
        if(player instanceof ServerPlayer server){
            var title=stack.has(DataComponents.CUSTOM_NAME)?stack.getHoverName():Component.translatable("container.leadBox");
            server.openMenu(new SimpleMenuProvider((id,inventory,viewer)->new ContainmentBoxMenu(id,inventory,hand),title),buffer->buffer.writeEnum(hand));
        }
        return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);
    }
}

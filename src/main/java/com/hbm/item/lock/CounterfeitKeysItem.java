package com.hbm.item.lock;

import com.hbm.machine.keyforge.KeyForgeContent;
import com.hbm.machine.storage.LockableBlockEntity;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;

/** Source kit replaces its held stack with one fake key and inserts/drops a second matching fake key. */
public final class CounterfeitKeysItem extends Item {
    public CounterfeitKeysItem(){super(new Item.Properties().stacksTo(1));}
    @Override public InteractionResult useOn(UseOnContext context){
        var level=context.getLevel();var player=context.getPlayer();if(player==null||!(level.getBlockEntity(context.getClickedPos()) instanceof LockableBlockEntity locked))return InteractionResult.PASS;
        if(locked.isLocked()&&locked.cheesable){if(!level.isClientSide){var key=new ItemStack(KeyForgeContent.FAKE_KEY.get());KeyPinItem.setPins(key,locked.getPins());player.setItemInHand(context.getHand(),key.copy());if(!player.getInventory().add(key.copy()))player.drop(key.copy(),false);player.inventoryMenu.broadcastChanges();player.swing(context.getHand(),true);}return InteractionResult.sidedSuccess(level.isClientSide);}
        if(!level.isClientSide&&!locked.cheesable){player.displayClientMessage(Component.translatable("message.hbm.key.counterfeit_elaborate").withStyle(ChatFormatting.LIGHT_PURPLE),false);player.displayClientMessage(Component.translatable("message.hbm.key.counterfeit_alternative").withStyle(ChatFormatting.LIGHT_PURPLE),false);}return InteractionResult.PASS;
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(Component.translatable("tooltip.hbm.key.counterfeit_kit"));}
}

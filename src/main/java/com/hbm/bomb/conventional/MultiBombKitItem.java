package com.hbm.bomb.conventional;

import com.hbm.registry.HbmContent;
import com.hbm.registry.HbmSounds;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Only the source ItemStarterKit multi_kit branch; its exact contents and unconditional stack consumption. */
public final class MultiBombKitItem extends Item {
    public MultiBombKitItem(){super(new Properties().stacksTo(1));}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);if(!level.isClientSide){player.getInventory().add(new ItemStack(MultiBombContent.ITEM.get(),6));player.getInventory().add(new ItemStack(Items.TNT,26));player.getInventory().add(new ItemStack(Items.GUNPOWDER,2));for(String symbol:new String[]{"pellet_cluster","powder_fire","powder_poison","pellet_gas"})player.getInventory().add(new ItemStack(HbmContent.item(symbol),2));level.playSound(null,player.getX(),player.getY(),player.getZ(),HbmSounds.get("item.unpack"),SoundSource.PLAYERS,1,1);stack.shrink(1);}return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(Component.translatable("tooltip.hbm.multi_kit.empty_inventory"));}
}

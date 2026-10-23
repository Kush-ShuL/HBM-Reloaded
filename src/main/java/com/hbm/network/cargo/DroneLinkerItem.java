package com.hbm.network.cargo;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
public final class DroneLinkerItem extends Item {
 public DroneLinkerItem(){super(new Properties().stacksTo(1));}
 private void chat(Player player,String key,ChatFormatting color){player.displayClientMessage(Component.literal("[").withStyle(ChatFormatting.DARK_AQUA).append(getName(new ItemStack(this)).copy().withStyle(ChatFormatting.DARK_AQUA)).append(Component.literal("] ").withStyle(ChatFormatting.DARK_AQUA)).append(Component.translatable(key).withStyle(color)),false);}
 @Override public InteractionResult useOn(UseOnContext context){var level=context.getLevel();var pos=context.getClickedPos();if(!(level.getBlockEntity(pos) instanceof DroneLinkable target))return InteractionResult.PASS;if(!level.isClientSide){var stack=context.getItemInHand();var existing=stack.get(DataComponents.CUSTOM_DATA);var tag=existing==null?new CompoundTag():existing.copyTag();if(existing==null)chat(context.getPlayer(),"message.hbm.drone.initial",ChatFormatting.AQUA);else{var old=new BlockPos(tag.getInt("x"),tag.getInt("y"),tag.getInt("z"));if(level.getBlockEntity(old) instanceof DroneLinkable previous){var dest=target.getPoint();previous.setNextTarget(dest.getX(),dest.getY(),dest.getZ());chat(context.getPlayer(),"message.hbm.drone.linked",ChatFormatting.AQUA);}else chat(context.getPlayer(),"message.hbm.drone.lost",ChatFormatting.RED);}tag.putInt("x",pos.getX());tag.putInt("y",pos.getY());tag.putInt("z",pos.getZ());stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}return InteractionResult.SUCCESS;}
 @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);if(!level.isClientSide&&stack.has(DataComponents.CUSTOM_DATA)){stack.remove(DataComponents.CUSTOM_DATA);chat(player,"message.hbm.drone.cleared",ChatFormatting.GREEN);}return InteractionResultHolder.pass(stack);}
}

package com.hbm.item.reactorsensor;
import com.hbm.machine.reactor.ReactorContent;
import com.hbm.registry.HbmSounds;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
/** Actual ItemReactorSensor only stores a clicked research-reactor cell; the source controller resolves its real core separately. */
public final class ReactorSensorItem extends Item{
    public ReactorSensorItem(){super(new Properties().stacksTo(1));}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.reactor_sensor.name");}
    @Override public InteractionResult useOn(UseOnContext context){var level=context.getLevel();var pos=context.getClickedPos();var player=context.getPlayer();if(player==null||!level.getBlockState(pos).is(ReactorContent.RESEARCH.get()))return InteractionResult.PASS;var stack=context.getItemInHand();var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();tag.putInt("x",pos.getX());tag.putInt("y",pos.getY());tag.putInt("z",pos.getZ());stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));if(!level.isClientSide)player.displayClientMessage(Component.literal("[").withStyle(ChatFormatting.DARK_AQUA).append(getName(stack).copy().withStyle(ChatFormatting.DARK_AQUA)).append(Component.literal("] ").withStyle(ChatFormatting.DARK_AQUA)).append(Component.translatable("item.hbm.reactor_sensor.position_set").withStyle(ChatFormatting.GREEN)),false);level.playSound(player,player.getX(),player.getY(),player.getZ(),HbmSounds.get("item.techBoop"),SoundSource.PLAYERS,1,1);return InteractionResult.sidedSuccess(level.isClientSide);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){var data=stack.get(DataComponents.CUSTOM_DATA);if(data==null){lines.add(Component.translatable("item.hbm.reactor_sensor.no_reactor"));return;}var tag=data.copyTag();lines.add(Component.literal("x: "+tag.getInt("x")));lines.add(Component.literal("y: "+tag.getInt("y")));lines.add(Component.literal("z: "+tag.getInt("z")));}
}

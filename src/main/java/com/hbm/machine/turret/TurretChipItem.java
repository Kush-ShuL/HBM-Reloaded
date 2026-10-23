package com.hbm.machine.turret;
import java.util.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
/** The registered ItemTurretChip inherits all actual ItemTurretBiometry data/interaction; the commented-out biometry item is not registered. */
public final class TurretChipItem extends Item {
    public TurretChipItem(){super(new Properties().stacksTo(1));}
    public static List<String> names(ItemStack stack){var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();int count=tag.getInt("playercount");var names=new ArrayList<String>();for(int i=0;i<count;i++)names.add(tag.getString("player_"+i));return names;}
    public static void add(ItemStack stack,String name){var names=names(stack);if(names.contains(name))return;var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();tag.putString("player_"+names.size(),name);tag.putInt("playercount",names.size()+1);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}
    public static void remove(ItemStack stack,int index){var names=names(stack);if(index<0||index>=names.size())return;names.remove(index);var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();tag.putInt("playercount",0);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));for(var name:names)add(stack,name);}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);add(stack,player.getDisplayName().getString());if(level.isClientSide)player.displayClientMessage(Component.translatable("message.hbm.turret_chip.added"),false);else level.playSound(null,player.getX(),player.getY(),player.getZ(),com.hbm.registry.HbmSounds.get("item.techBleep"),SoundSource.PLAYERS,1,1);player.swing(hand);return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){for(var name:names(stack))tooltip.add(Component.literal(name));}
}

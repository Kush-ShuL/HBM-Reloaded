package com.hbm.machine.teleport;

import com.hbm.registry.HbmSounds;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;

/** Source ItemTeleLink first records the clicked block's own coordinates, then programs the actual teleporter BE. */
public final class TelelinkerItem extends Item {
    public TelelinkerItem(){super(new Properties().stacksTo(1));}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.linker.name");}
    @Override public InteractionResult useOn(UseOnContext context){
        var player=context.getPlayer();var world=context.getLevel();
        if(player==null||player.isShiftKeyDown()||world.isClientSide)return InteractionResult.PASS;
        ItemStack stack=context.getItemInHand();var pos=context.getClickedPos();
        if(!(world.getBlockEntity(pos) instanceof TeleporterEntity teleporter)){
            var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();
            tag.putInt("x",pos.getX());tag.putInt("y",pos.getY());tag.putInt("z",pos.getZ());TeleportDimensions.record(tag,player.level().dimension());
            stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));
            sound(context,"item.techBleep");player.sendSystemMessage(Component.translatable("message.hbm.telelink.exit",pos.getX(),pos.getY(),pos.getZ()).withStyle(ChatFormatting.AQUA));
        }else{
            var data=stack.get(DataComponents.CUSTOM_DATA);
            if(data==null){sound(context,"item.techBoop");player.sendSystemMessage(Component.translatable("message.hbm.telelink.no_destination").withStyle(ChatFormatting.RED));return InteractionResult.PASS;}
            var tag=data.copyTag();teleporter.targetX=tag.getInt("x");teleporter.targetY=tag.getInt("y");teleporter.targetZ=tag.getInt("z");teleporter.targetDim=tag.getInt("dim");teleporter.targetDimension=tag.getString(TeleportDimensions.KEY);teleporter.setChanged();
            sound(context,"item.techBleep");player.sendSystemMessage(Component.translatable("message.hbm.telelink.programmed").withStyle(ChatFormatting.AQUA));
        }
        player.swing(context.getHand(),true);
        return InteractionResult.CONSUME;
    }
    private static void sound(UseOnContext context,String sound){var p=context.getPlayer();context.getLevel().playSound(null,p.getX(),p.getY(),p.getZ(),HbmSounds.get(sound),SoundSource.PLAYERS,1F,1F);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){
        var data=stack.get(DataComponents.CUSTOM_DATA);
        if(data==null){tooltip.add(Component.translatable("tooltip.hbm.telelink.select_exit").withStyle(ChatFormatting.RED));return;}
        var tag=data.copyTag();for(String axis:List.of("x","y","z"))tooltip.add(Component.literal(axis.toUpperCase(java.util.Locale.ROOT)+": "+tag.getInt(axis)));
        tooltip.add(Component.literal("D: "+(tag.contains(TeleportDimensions.KEY)?tag.getString(TeleportDimensions.KEY):Integer.toString(tag.getInt("dim")))));
    }
}

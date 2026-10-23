package com.hbm.item.designator;

import api.hbm.item.IDesignatorItem;
import com.hbm.registry.HbmSounds;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Entire original ItemDesingator: short-range selected block coordinates, not a forged manual/rangefinder variant. */
public final class TargetDesignatorItem extends Item implements IDesignatorItem {
    public TargetDesignatorItem(){super(new Properties().stacksTo(1));}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){
        if(stack.has(DataComponents.CUSTOM_DATA)){var tag=stack.get(DataComponents.CUSTOM_DATA).copyTag();lines.add(Component.translatable("item.hbm.designator.coordinates"));lines.add(Component.literal("X: "+tag.getInt("xCoord")));lines.add(Component.literal("Z: "+tag.getInt("zCoord")));}
        else lines.add(Component.translatable("item.hbm.designator.select_target"));
    }
    @Override public InteractionResult useOn(UseOnContext context){
        var level=context.getLevel();var pos=context.getClickedPos();var player=context.getPlayer();
        // The sole original LaunchPad registration is hbm:launch_pad. LaunchPadRusted is a different source class.
        if(BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock()).toString().equals("hbm:launch_pad"))return InteractionResult.PASS;
        CustomData.update(DataComponents.CUSTOM_DATA,context.getItemInHand(),tag->{tag.putInt("xCoord",pos.getX());tag.putInt("zCoord",pos.getZ());});
        if(player!=null){if(level.isClientSide)player.displayClientMessage(Component.translatable("item.hbm.designator.position_set"),false);level.playSound(player,player.getX(),player.getY(),player.getZ(),HbmSounds.get("item.techBleep"),SoundSource.PLAYERS,1F,1F);}
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override public boolean isReady(Level level,ItemStack stack,int x,int y,int z){return stack.has(DataComponents.CUSTOM_DATA);}
    @Override public Vec3 getCoords(Level level,ItemStack stack,int x,int y,int z){var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();return new Vec3(tag.getInt("xCoord"),0,tag.getInt("zCoord"));}
}

package com.hbm.item.pollution;

import com.hbm.pollution.PollutionSync;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Source ItemPollutionDetector, any inventory slot,10-tick update and three keyed four-second notices. */
public final class PollutionDetectorItem extends Item {
    public PollutionDetectorItem(){super(new Properties().stacksTo(1));}
    @Override public void inventoryTick(ItemStack stack,Level level,Entity entity,int slot,boolean selected){if(entity instanceof ServerPlayer player&&level.getGameTime()%10==0)PollutionSync.send(player,true);}
}

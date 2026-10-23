package com.hbm.item.bedrockresource;

import com.hbm.worldgen.bedrockresource.BedrockDensity;
import com.hbm.worldgen.bedrockresource.BedrockScannerReadings;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Source scanner runs in any player inventory slot, every5 server ticks; no invented battery/use charge. */
public final class OreDensityScannerItem extends Item {
    public OreDensityScannerItem(){super(new Properties().stacksTo(1));}
    @Override public void inventoryTick(ItemStack stack,Level level,Entity entity,int slot,boolean selected){
        if(entity instanceof ServerPlayer player&&level.getGameTime()%5==0)BedrockScannerReadings.send(player,BedrockDensity.levels(player.blockPosition().getX(),player.blockPosition().getZ()));
    }
}

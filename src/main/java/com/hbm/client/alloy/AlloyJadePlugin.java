package com.hbm.client.alloy;
import com.hbm.machine.alloy.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
/** Native server state for the thermal furnace; fuel units are never presented as HE. */
@WailaPlugin
public final class AlloyJadePlugin implements IWailaPlugin {
    @Override public void register(IWailaCommonRegistration event) { event.registerBlockDataProvider(Provider.INSTANCE,AlloyBlockEntity.class); }
    @Override public void registerClient(IWailaClientRegistration event) { event.registerBlockComponent(Provider.INSTANCE,AlloyFurnaceBlock.class); }
    private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {
        INSTANCE;
        @Override public ResourceLocation getUid() { return ResourceLocation.fromNamespaceAndPath("hbm","alloy_furnace"); }
        @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor) { if(accessor.getBlockEntity() instanceof AlloyBlockEntity furnace) { tag.putInt("hbm:alloyFuel",furnace.fuel()); tag.putInt("hbm:alloyProgress",furnace.progress()); } }
        @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config) { var tag=accessor.getServerData(); if(!tag.contains("hbm:alloyFuel")) return; com.hbm.compat.jade.HbmJadeBars.add(tooltip,tag.getInt("hbm:alloyFuel"),AlloyBlockEntity.MAX_FUEL,Component.translatableWithFallback("jade.hbm.alloy_fuel","Fuel: %s / %s",tag.getInt("hbm:alloyFuel"),AlloyBlockEntity.MAX_FUEL),0xE69B51); if(tag.getInt("hbm:alloyProgress")>0) com.hbm.compat.jade.HbmJadeBars.add(tooltip,tag.getInt("hbm:alloyProgress"),AlloyBlockEntity.PROCESS_TIME,Component.translatableWithFallback("jade.hbm.alloy_progress","Progress: %s / %s",tag.getInt("hbm:alloyProgress"),AlloyBlockEntity.PROCESS_TIME),0x71C78B); }
    }
}

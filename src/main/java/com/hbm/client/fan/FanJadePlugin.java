package com.hbm.client.fan;

import com.hbm.machine.fan.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public final class FanJadePlugin implements IWailaPlugin {
    @Override public void register(IWailaCommonRegistration registration) { registration.registerBlockDataProvider(Provider.INSTANCE,FanEntity.class); }
    @Override public void registerClient(IWailaClientRegistration registration) { registration.registerBlockComponent(Provider.INSTANCE,FanBlock.class); }
    private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {
        INSTANCE;
        @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:fan");}
        @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor) { if(accessor.getBlockEntity() instanceof FanEntity fan){var flags=new CompoundTag();flags.putBoolean("falloff",fan.falloff);flags.putBoolean("suck",fan.suck);flags.putBoolean("powered",accessor.getLevel().hasNeighborSignal(accessor.getPosition()));tag.put("hbm:fan",flags);} }
        @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config) { if(!accessor.getServerData().contains("hbm:fan"))return;var flags=accessor.getServerData().getCompound("hbm:fan");tooltip.add(Component.translatable("jade.hbm.fan.powered."+flags.getBoolean("powered")));tooltip.add(Component.translatable(flags.getBoolean("falloff")?"tile.fan.falloffOn":"tile.fan.falloffOff"));tooltip.add(Component.translatable(flags.getBoolean("suck")?"tile.fan.suckOn":"tile.fan.suckOff")); }
    }
}

package com.hbm.machine.groundwater;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

/** Only original ground/altitude diagnostics; the existing shared MachineView renders actual HE/private tanks. */
@WailaPlugin public final class GroundwaterPumpJadePlugin implements IWailaPlugin {
    @Override public void register(IWailaCommonRegistration registration){
        registration.registerBlockDataProvider(Provider.INSTANCE,SteamGroundwaterPump.class);
        registration.registerBlockDataProvider(Provider.INSTANCE,ElectricGroundwaterPump.class);
        registration.registerBlockDataProvider(Provider.INSTANCE,GroundwaterPumpPort.class);
    }
    @Override public void registerClient(IWailaClientRegistration registration){registration.registerBlockComponent(Provider.INSTANCE,GroundwaterPumpBlock.class);}
    private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {INSTANCE;
        @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:groundwater_pump");}
        @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){
            var machine=accessor.getBlockEntity() instanceof GroundwaterPumpEntity pump?pump:accessor.getBlockEntity() instanceof GroundwaterPumpPort port?port.core():null;
            if(machine!=null){tag.putBoolean("groundwaterValid",machine.onGround);tag.putBoolean("groundwaterHigh",machine.getBlockPos().getY()>70);}
        }
        @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){
            var tag=accessor.getServerData();if(!tag.contains("groundwaterValid"))return;
            if(tag.getBoolean("groundwaterHigh"))tooltip.add(Component.translatable("gui.hbm.groundwater.altitude").withStyle(ChatFormatting.RED));
            if(!tag.getBoolean("groundwaterValid"))tooltip.add(Component.translatable("gui.hbm.groundwater.invalid_ground").withStyle(ChatFormatting.RED));
        }
    }
}

package com.hbm.client.thresher;
import com.hbm.machine.thresher.*;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
/** Fluid data remains sole MachineView provider; only source suspension status is added here. */
@WailaPlugin
public final class ThresherJadePlugin implements IWailaPlugin {
    @Override public void register(IWailaCommonRegistration registration){registration.registerBlockDataProvider(Status.INSTANCE,ThresherEntity.class);}
    @Override public void registerClient(IWailaClientRegistration registration){registration.registerBlockComponent(Status.INSTANCE,ThresherBlock.class);}
    private enum Status implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {
        INSTANCE;
        @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:thresher_status");}
        @Override public void appendServerData(CompoundTag data,BlockAccessor accessor){if(accessor.getBlockEntity() instanceof ThresherEntity machine)data.putBoolean("hbm:thresher_suspended",machine.isSuspended);}
        @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){if(accessor.getServerData().getBoolean("hbm:thresher_suspended"))tooltip.add(Component.literal("! ").append(Component.translatable("tile.machine_thresher.suspended")).append(" !").withStyle(ChatFormatting.RED));}
    }
}

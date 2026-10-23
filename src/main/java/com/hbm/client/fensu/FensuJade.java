package com.hbm.client.fensu;

import com.hbm.machine.fensu.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

/** Real infinite storage telemetry, never confused with the deliberately capped HE-network provision getter. */
@WailaPlugin
public final class FensuJade implements IWailaPlugin {
    @Override public void register(IWailaCommonRegistration registration){registration.registerBlockDataProvider(Provider.INSTANCE,FensuBlock.class);}
    @Override public void registerClient(IWailaClientRegistration registration){registration.registerBlockComponent(Provider.INSTANCE,FensuBlock.class);}
    private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider{
        INSTANCE;
        @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:fensu_storage");}
        @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){if(accessor.getBlockState().getBlock() instanceof FensuBlock block){var machine=block.core(accessor.getLevel(),accessor.getPosition());if(machine!=null){tag.putString("hbmFensuPower",FensuNumbers.format(machine.storedPower()));tag.putString("hbmFensuDelta",FensuNumbers.format(machine.delta));}}}
        @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var data=accessor.getServerData();if(data.contains("hbmFensuPower")){tooltip.add(Component.translatable("jade.hbm.fensu.storage",data.getString("hbmFensuPower")));tooltip.add(Component.translatable("jade.hbm.fensu.delta",data.getString("hbmFensuDelta")));}}
    }
}

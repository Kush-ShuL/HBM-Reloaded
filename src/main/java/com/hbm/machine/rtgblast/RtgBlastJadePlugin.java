package com.hbm.machine.rtgblast;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

/** Source EnergyControl fuel/progress equivalents. Never call the mutating hasPower/canProcess from a read provider. */
@WailaPlugin public final class RtgBlastJadePlugin implements IWailaPlugin {
    @Override public void register(IWailaCommonRegistration registration){registration.registerBlockDataProvider(Provider.INSTANCE,RtgBlastEntity.class);}
    @Override public void registerClient(IWailaClientRegistration registration){registration.registerBlockComponent(Provider.INSTANCE,RtgBlastBlock.class);}
    private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {INSTANCE;
        @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:rtg_blast_furnace");}
        @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){if(accessor.getBlockEntity() instanceof RtgBlastEntity furnace)tag.putShort("rtgBlastHeat",furnace.heat());}
        @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){if(accessor.getServerData().contains("rtgBlastHeat"))tooltip.add(Component.translatable("desc.gui.rtg.heat",accessor.getServerData().getShort("rtgBlastHeat")));}
    }
}

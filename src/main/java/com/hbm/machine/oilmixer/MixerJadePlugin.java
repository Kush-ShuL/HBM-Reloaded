package com.hbm.machine.oilmixer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
/** Shared MachineView already supplies native resource/progress bars; this adds the actual per-tick demand. */
@WailaPlugin public final class MixerJadePlugin implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration e){e.registerBlockDataProvider(Provider.INSTANCE,MixerEntity.class);e.registerBlockDataProvider(Provider.INSTANCE,MixerPort.class);}@Override public void registerClient(IWailaClientRegistration e){e.registerBlockComponent(Provider.INSTANCE,MixerBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {INSTANCE;@Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:oil_mixer");}@Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){var m=accessor.getBlockEntity() instanceof MixerEntity mixer?mixer:accessor.getBlockEntity() instanceof MixerPort p?p.core():null;if(m!=null)tag.putInt("mixerConsumption",m.consumption);}@Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var tag=accessor.getServerData();if(tag.contains("mixerConsumption"))tooltip.add(Component.translatable("gui.hbm.mixer.consumption",tag.getInt("mixerConsumption")));}}
}

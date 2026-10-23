package com.hbm.client.cyclotron;
import com.hbm.machine.cyclotron.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
/** Real source EC consumption and activity; existing MachineView owns HE,tanks and task progress. */
@WailaPlugin public final class CyclotronJadePlugin implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration e){e.registerBlockDataProvider(Provider.INSTANCE,CyclotronEntity.class);e.registerBlockDataProvider(Provider.INSTANCE,CyclotronPort.class);}@Override public void registerClient(IWailaClientRegistration e){e.registerBlockComponent(Provider.INSTANCE,CyclotronBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider{INSTANCE;@Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:cyclotron_status");}@Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){var be=accessor.getBlockEntity();if(be instanceof CyclotronPort p)be=p.core();if(be instanceof CyclotronEntity c){tag.putBoolean("hbm:cyclotron_active",c.progress>0);tag.putInt("hbm:cyclotron_consumption",c.progress>0?c.getConsumption():0);}}@Override public void appendTooltip(ITooltip tip,BlockAccessor accessor,IPluginConfig config){if(accessor.getServerData().getBoolean("hbm:cyclotron_active"))tip.add(Component.translatable("gui.hbm.cyclotron.consumption",accessor.getServerData().getInt("hbm:cyclotron_consumption")));}}
}

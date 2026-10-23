package com.hbm.client.turbofan;
import com.hbm.machine.turbofan.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
/** Exact source IInfoProviderEC output/consumption; generic MachineView owns actual storage,not a fake taskbar. */
@WailaPlugin public final class TurbofanJadePlugin implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration e){e.registerBlockDataProvider(Provider.INSTANCE,TurbofanEntity.class);e.registerBlockDataProvider(Provider.INSTANCE,TurbofanPort.class);}@Override public void registerClient(IWailaClientRegistration e){e.registerBlockComponent(Provider.INSTANCE,TurbofanBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider{INSTANCE;@Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:turbofan_status");}@Override public void appendServerData(CompoundTag tag,BlockAccessor a){var be=a.getBlockEntity();if(be instanceof TurbofanPort p)be=p.core();if(be instanceof TurbofanEntity t){tag.putBoolean("hbm:turbofan_active",t.output>0);tag.putInt("hbm:turbofan_output",t.output);tag.putInt("hbm:turbofan_consumption",t.consumption);}}@Override public void appendTooltip(ITooltip tip,BlockAccessor a,IPluginConfig config){var tag=a.getServerData();if(tag.getBoolean("hbm:turbofan_active")){tip.add(Component.translatable("gui.hbm.turbofan.output",tag.getInt("hbm:turbofan_output")));tip.add(Component.translatable("gui.hbm.turbofan.consumption",tag.getInt("hbm:turbofan_consumption")));}}}
}

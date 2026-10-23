package com.hbm.client.oilliquefactor;
import com.hbm.machine.oilliquefactor.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
/** Existing MachineView provides actual HE/tank and only-real-task progress,including on inventory/power/fluid proxies. */
@WailaPlugin public final class LiquefactorJadePlugin implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration r){r.registerBlockDataProvider(Provider.INSTANCE,LiquefactorEntity.class);r.registerBlockDataProvider(Provider.INSTANCE,LiquefactorPort.class);}@Override public void registerClient(IWailaClientRegistration r){r.registerBlockComponent(Provider.INSTANCE,LiquefactorBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider{INSTANCE;@Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:liquefactor_status");}@Override public void appendServerData(CompoundTag data,BlockAccessor a){var be=a.getBlockEntity();if(be instanceof LiquefactorPort p)be=p.core();if(be instanceof LiquefactorEntity m){data.putBoolean("hbm:liquefactor_active",m.progress>0);data.putInt("hbm:liquefactor_usage",m.usage);}}@Override public void appendTooltip(ITooltip tip,BlockAccessor a,IPluginConfig c){if(a.getServerData().getBoolean("hbm:liquefactor_active"))tip.add(Component.translatable("gui.hbm.liquefactor.consumption",a.getServerData().getInt("hbm:liquefactor_usage")));}}
}

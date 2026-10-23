package com.hbm.client.solidifier;
import com.hbm.machine.solidifier.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
/** Existing MachineView provides actual HE/tank and only-real-task progress,including on inventory/power/fluid proxies. */
@WailaPlugin public final class SolidifierJadePlugin implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration r){r.registerBlockDataProvider(Provider.INSTANCE,SolidifierEntity.class);r.registerBlockDataProvider(Provider.INSTANCE,SolidifierPort.class);}@Override public void registerClient(IWailaClientRegistration r){r.registerBlockComponent(Provider.INSTANCE,SolidifierBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider{INSTANCE;@Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:solidifier_status");}@Override public void appendServerData(CompoundTag data,BlockAccessor a){var be=a.getBlockEntity();if(be instanceof SolidifierPort p)be=p.core();if(be instanceof SolidifierEntity m){data.putBoolean("hbm:solidifier_active",m.progress>0);data.putInt("hbm:solidifier_usage",m.usage);}}@Override public void appendTooltip(ITooltip tip,BlockAccessor a,IPluginConfig c){if(a.getServerData().getBoolean("hbm:solidifier_active"))tip.add(Component.translatable("gui.hbm.solidifier.consumption",a.getServerData().getInt("hbm:solidifier_usage")));}}
}

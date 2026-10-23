package com.hbm.client.poweredcondenser;
import com.hbm.machine.poweredcondenser.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
/** Source EC throughput is the requested batch even when extraCondition fails; never label it completed output. */
@WailaPlugin public final class PoweredCondenserJadePlugin implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration e){e.registerBlockDataProvider(Provider.INSTANCE,PoweredCondenserEntity.class);e.registerBlockDataProvider(Provider.INSTANCE,PoweredCondenserPort.class);}@Override public void registerClient(IWailaClientRegistration e){e.registerBlockComponent(Provider.INSTANCE,PoweredCondenserBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider{INSTANCE;@Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:powered_condenser_status");}@Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){var be=accessor.getBlockEntity();if(be instanceof PoweredCondenserPort p)be=p.core();if(be instanceof PoweredCondenserEntity c)tag.putInt("hbm:condenser_requested",c.throughput);}@Override public void appendTooltip(ITooltip tip,BlockAccessor accessor,IPluginConfig config){if(accessor.getServerData().contains("hbm:condenser_requested"))tip.add(Component.translatable("gui.hbm.powered_condenser.throughput",accessor.getServerData().getInt("hbm:condenser_requested")));}}
}

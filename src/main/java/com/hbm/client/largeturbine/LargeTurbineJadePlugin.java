package com.hbm.client.largeturbine;
import com.hbm.machine.largeturbine.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
/** Exactly the source IInfoProviderEC values; generic MachineView supplies the real buffer and tanks. */
@WailaPlugin public final class LargeTurbineJadePlugin implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration e){e.registerBlockDataProvider(Provider.INSTANCE,LargeTurbineEntity.class);e.registerBlockDataProvider(Provider.INSTANCE,LargeTurbinePort.class);}@Override public void registerClient(IWailaClientRegistration e){e.registerBlockComponent(Provider.INSTANCE,LargeTurbineBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider{INSTANCE;@Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:large_turbine_legacy_status");}@Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){var be=accessor.getBlockEntity();if(be instanceof LargeTurbinePort p)be=p.core();if(be instanceof LargeTurbineEntity t){tag.putBoolean("hbm:large_turbine_active",t.info[1]>0);tag.putDouble("hbm:large_turbine_input",t.info[0]);tag.putDouble("hbm:large_turbine_output",t.info[1]);tag.putDouble("hbm:large_turbine_energy",t.info[2]);}}@Override public void appendTooltip(ITooltip tip,BlockAccessor accessor,IPluginConfig config){var tag=accessor.getServerData();if(tag.getBoolean("hbm:large_turbine_active")){tip.add(Component.translatable("gui.hbm.large_turbine.input",tag.getDouble("hbm:large_turbine_input")));tip.add(Component.translatable("gui.hbm.large_turbine.output",tag.getDouble("hbm:large_turbine_output")));tip.add(Component.translatable("gui.hbm.large_turbine.power",tag.getDouble("hbm:large_turbine_energy")));}}}
}

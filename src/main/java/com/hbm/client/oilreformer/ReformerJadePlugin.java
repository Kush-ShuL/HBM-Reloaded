package com.hbm.client.oilreformer;
import com.hbm.machine.oilreformer.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
/** Existing MachineView supplies actual finite HE/tanks; this status exists only on actual successful source conversion. */
@WailaPlugin public final class ReformerJadePlugin implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration r){r.registerBlockDataProvider(Provider.INSTANCE,ReformerEntity.class);r.registerBlockDataProvider(Provider.INSTANCE,ReformerPort.class);}
 @Override public void registerClient(IWailaClientRegistration r){r.registerBlockComponent(Provider.INSTANCE,ReformerBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {INSTANCE;@Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:reformer_status");}@Override public void appendServerData(CompoundTag data,BlockAccessor a){var be=a.getBlockEntity();if(be instanceof ReformerPort p)be=p.core();if(be instanceof ReformerEntity m)data.putBoolean("hbm:reformer_active",m.isOn);}@Override public void appendTooltip(ITooltip tip,BlockAccessor a,IPluginConfig c){if(a.getServerData().getBoolean("hbm:reformer_active"))tip.add(Component.translatable("jade.hbm.reformer.active"));}}
}

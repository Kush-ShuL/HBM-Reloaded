package com.hbm.machine.annihilator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
@WailaPlugin
public final class AnnihilatorJadePlugin implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration e){e.registerBlockDataProvider(Provider.INSTANCE,AnnihilatorEntity.class);e.registerBlockDataProvider(Provider.INSTANCE,AnnihilatorPort.class);}@Override public void registerClient(IWailaClientRegistration e){e.registerBlockComponent(Provider.INSTANCE,AnnihilatorBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider{INSTANCE;@Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:annihilator");}@Override public void appendServerData(CompoundTag tag,BlockAccessor a){var machine=a.getBlockEntity() instanceof AnnihilatorEntity e?e:a.getBlockEntity() instanceof AnnihilatorPort p?p.core():null;if(machine!=null)tag.putString("annihilatorPool",machine.pool);}@Override public void appendTooltip(ITooltip tooltip,BlockAccessor a,IPluginConfig config){var tag=a.getServerData();if(tag.contains("annihilatorPool"))tooltip.add(Component.translatable("gui.hbm.annihilator.pool").append(": ").append(Component.literal(tag.getString("annihilatorPool"))));}}
}

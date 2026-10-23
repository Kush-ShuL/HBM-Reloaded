package com.hbm.client.network.separator;
import com.hbm.network.separator.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
@WailaPlugin
public final class NetworkSeparatorJadePlugin implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration r){r.registerBlockDataProvider(Provider.INSTANCE,NetworkSeparatorEntity.class);r.registerBlockDataProvider(Provider.INSTANCE,NetworkSeparatorPort.class);}
 @Override public void registerClient(IWailaClientRegistration r){r.registerBlockComponent(Provider.INSTANCE,NetworkSeparatorBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {INSTANCE;
  @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:network_separator_status");}
  @Override public void appendServerData(CompoundTag data,BlockAccessor a){if(a.getBlockState().getBlock() instanceof NetworkSeparatorBlock block){var tile=block.core(a.getLevel(),a.getPosition());if(tile!=null)data.put("hbm:network_separator",NetworkSeparatorReadout.data(tile));}}
  @Override public void appendTooltip(ITooltip tooltip,BlockAccessor a,IPluginConfig config){if(a.getServerData().contains("hbm:network_separator"))for(var line:NetworkSeparatorReadout.lines(a.getServerData().getCompound("hbm:network_separator")))tooltip.add(line);}
 }
}

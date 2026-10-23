package com.hbm.client.network.rtty;
import com.hbm.network.rtty.*;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
/** The original RW look overlay's actual channel and redstone state. */
@WailaPlugin
public final class RadioTorchJadePlugin implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration registry){registry.registerBlockDataProvider(Provider.INSTANCE,RadioTorchEntity.class);}
 @Override public void registerClient(IWailaClientRegistration registry){registry.registerBlockComponent(Provider.INSTANCE,RadioTorchBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {INSTANCE;
  @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:radio_torch_status");}
  @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){if(accessor.getBlockEntity() instanceof RadioTorchEntity radio){var data=new CompoundTag();data.putString("channel",radio.channel);data.putInt("signal",radio.lastState);tag.put("hbm:radio_torch",data);}}
  @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){if(!accessor.getServerData().contains("hbm:radio_torch"))return;var data=accessor.getServerData().getCompound("hbm:radio_torch");if(!data.getString("channel").isEmpty())tooltip.add(Component.translatable("jade.hbm.rtty.frequency",data.getString("channel")).withStyle(ChatFormatting.AQUA));tooltip.add(Component.translatable("jade.hbm.rtty.signal",data.getInt("signal")).withStyle(ChatFormatting.RED));}
 }
}

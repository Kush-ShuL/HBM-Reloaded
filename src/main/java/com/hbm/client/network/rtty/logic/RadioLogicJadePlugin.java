package com.hbm.client.network.rtty.logic;
import com.hbm.network.rtty.logic.*;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
@WailaPlugin
public final class RadioLogicJadePlugin implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration registry){registry.registerBlockDataProvider(Provider.INSTANCE,RadioLogicEntity.class);}@Override public void registerClient(IWailaClientRegistration registry){registry.registerBlockComponent(Provider.INSTANCE,RadioLogicBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {INSTANCE;
  @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:radio_logic_status");}
  @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){if(accessor.getBlockEntity() instanceof RadioLogicEntity logic){var data=new CompoundTag();data.putString("channel",logic.channel);data.putInt("signal",logic.lastState);tag.put("hbm:radio_logic",data);}}
  @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){if(!accessor.getServerData().contains("hbm:radio_logic"))return;var data=accessor.getServerData().getCompound("hbm:radio_logic");if(!data.getString("channel").isEmpty())tooltip.add(Component.translatable("jade.hbm.rtty.frequency",data.getString("channel")).withStyle(ChatFormatting.AQUA));tooltip.add(Component.translatable("jade.hbm.rtty.signal",data.getInt("signal")).withStyle(ChatFormatting.RED));}
 }
}

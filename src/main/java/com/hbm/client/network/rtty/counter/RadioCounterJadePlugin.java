package com.hbm.client.network.rtty.counter;
import com.hbm.network.rtty.counter.*;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
@WailaPlugin
public final class RadioCounterJadePlugin implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration registry){registry.registerBlockDataProvider(Provider.INSTANCE,RadioCounterEntity.class);}@Override public void registerClient(IWailaClientRegistration registry){registry.registerBlockComponent(Provider.INSTANCE,RadioCounterBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {INSTANCE;
  @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:radio_counter_status");}
  @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){if(accessor.getBlockEntity() instanceof RadioCounterEntity counter){var data=new CompoundTag();for(int i=0;i<3;i++){data.putString("c"+i,counter.channel[i]);data.putInt("l"+i,counter.lastCount[i]);}tag.put("hbm:radio_counter",data);}}
  @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){if(!accessor.getServerData().contains("hbm:radio_counter"))return;var data=accessor.getServerData().getCompound("hbm:radio_counter");for(int i=0;i<3;i++)if(!data.getString("c"+i).isEmpty()){tooltip.add(Component.translatable("jade.hbm.rtty.counter_frequency",i+1,data.getString("c"+i)).withStyle(ChatFormatting.AQUA));tooltip.add(Component.translatable("jade.hbm.rtty.counter_signal",i+1,data.getInt("l"+i)).withStyle(ChatFormatting.RED));}}
 }
}

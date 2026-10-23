package com.hbm.client.network.rtty.ror;
import com.hbm.network.rtty.ror.*;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
@WailaPlugin
public final class RadioRorJadePlugin implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration registry){registry.registerBlockDataProvider(Provider.INSTANCE,RadioRorEntity.class);}@Override public void registerClient(IWailaClientRegistration registry){registry.registerBlockComponent(Provider.INSTANCE,RadioRorBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {INSTANCE;
  @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:radio_ror_status");}
  @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){var data=new CompoundTag();if(accessor.getBlockEntity() instanceof RadioReaderEntity reader){data.putBoolean("reader",true);for(int i=0;i<8;i++){data.putString("c"+i,reader.channels[i]);data.putString("n"+i,reader.names[i]);}}else if(accessor.getBlockEntity() instanceof RadioControllerEntity controller)data.putString("channel",controller.channel);else return;tag.put("hbm:radio_ror",data);}
  @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){if(!accessor.getServerData().contains("hbm:radio_ror"))return;var data=accessor.getServerData().getCompound("hbm:radio_ror");if(data.getBoolean("reader")){for(int i=0;i<8;i++)if(!data.getString("c"+i).isEmpty()&&!data.getString("n"+i).isEmpty())tooltip.add(Component.translatable("jade.hbm.rtty.reader_binding",data.getString("c"+i),data.getString("n"+i)).withStyle(ChatFormatting.AQUA));}else tooltip.add(Component.translatable("jade.hbm.rtty.frequency",data.getString("channel")).withStyle(ChatFormatting.AQUA));}
 }
}

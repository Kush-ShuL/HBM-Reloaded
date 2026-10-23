package com.hbm.machine.foundry.client;
import com.hbm.machine.foundry.*;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
/** Original no-cast, installed mold, material filter and redstone mode text. Amount/cooling use shared progress bars. */
@WailaPlugin
public final class FoundryJadePlugin implements IWailaPlugin{
 @Override public void register(IWailaCommonRegistration event){event.registerBlockDataProvider(Provider.INSTANCE,FoundryBlockEntity.class);}
 @Override public void registerClient(IWailaClientRegistration event){event.registerBlockComponent(Provider.INSTANCE,FoundryBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider{INSTANCE;
  @Override public ResourceLocation getUid(){return ResourceLocation.fromNamespaceAndPath("hbm","foundry_state");}
  @Override public void appendServerData(CompoundTag data,BlockAccessor accessor){if(!(accessor.getBlockEntity() instanceof FoundryBlockEntity foundry))return;var tag=new CompoundTag();if(foundry.kind().cast()){tag.putBoolean("cast",true);var mold=foundry.mold();if(mold!=null)tag.putString("mold",Component.Serializer.toJson(mold.title(),accessor.getLevel().registryAccess()));}if(foundry.kind().outlet()){if(foundry.filter!=null)tag.putString("filter",foundry.filter.descriptionId());tag.putBoolean("invertFilter",foundry.invertFilter);tag.putBoolean("inverted",foundry.invertRedstone);}data.put("hbm:foundry",tag);}
  @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){if(!accessor.getServerData().contains("hbm:foundry"))return;var tag=accessor.getServerData().getCompound("hbm:foundry");if(tag.getBoolean("cast")){if(tag.contains("mold")){Component mold=Component.Serializer.fromJson(tag.getString("mold"),accessor.getLevel().registryAccess());if(mold!=null)tooltip.add(mold.copy().withStyle(ChatFormatting.BLUE));}else tooltip.add(Component.translatable("foundry.noCast").withStyle(ChatFormatting.RED));}if(tag.contains("filter"))tooltip.add(Component.translatable("foundry.filter",Component.translatable(tag.getString("filter"))).withStyle(ChatFormatting.YELLOW));if(tag.getBoolean("invertFilter"))tooltip.add(Component.translatable("foundry.invertFilter").withStyle(ChatFormatting.YELLOW));if(tag.getBoolean("inverted"))tooltip.add(Component.translatable("foundry.inverted").withStyle(ChatFormatting.DARK_RED));}
 }
}

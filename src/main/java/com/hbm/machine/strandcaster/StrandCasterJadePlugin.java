package com.hbm.machine.strandcaster;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
/** Source installed-mold overlay, additional to the shared MachineView progress/tank bars. */
@WailaPlugin
public final class StrandCasterJadePlugin implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration event){event.registerBlockDataProvider(Provider.INSTANCE,StrandCasterEntity.class);event.registerBlockDataProvider(Provider.INSTANCE,StrandCasterPort.class);}@Override public void registerClient(IWailaClientRegistration event){event.registerBlockComponent(Provider.INSTANCE,StrandCasterBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider{INSTANCE;@Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:strand_caster");}@Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){var c=accessor.getBlockEntity() instanceof StrandCasterEntity caster?caster:accessor.getBlockEntity() instanceof StrandCasterPort port?port.core():null;if(c==null)return;var mold=c.mold();tag.putBoolean("caster",true);if(mold!=null)tag.putString("casterMold",Component.Serializer.toJson(mold.title(),accessor.getLevel().registryAccess()));}@Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var tag=accessor.getServerData();if(!tag.getBoolean("caster"))return;if(tag.contains("casterMold")){var mold=Component.Serializer.fromJson(tag.getString("casterMold"),accessor.getLevel().registryAccess());if(mold!=null)tooltip.add(mold.copy().withStyle(ChatFormatting.BLUE));}else tooltip.add(Component.translatable("foundry.noCast").withStyle(ChatFormatting.RED));}}
}

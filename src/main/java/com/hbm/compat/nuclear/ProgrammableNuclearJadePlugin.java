package com.hbm.compat.nuclear;
import com.hbm.bomb.nuclear.programmable.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

/** Server-authoritative source timer/readiness and seven actual payload levels. No HE/FE/fluid proxy is exposed. */
@WailaPlugin
public final class ProgrammableNuclearJadePlugin implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration registration){registration.registerBlockDataProvider(Data.INSTANCE,TimedBalefireBlockEntity.class);registration.registerBlockDataProvider(Data.INSTANCE,CustomNuclearBlockEntity.class);}
 @Override public void registerClient(IWailaClientRegistration registration){registration.registerBlockComponent(Data.INSTANCE,TimedBalefireBlock.class);registration.registerBlockComponent(Data.INSTANCE,CustomNuclearBlock.class);}
 enum Data implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {
  INSTANCE;
  @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:programmable_nuclear");}
  @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){if(accessor.getBlockEntity() instanceof TimedBalefireBlockEntity bomb){tag.putInt("hbmFstTimer",bomb.timer);tag.putBoolean("hbmFstStarted",bomb.started);tag.putBoolean("hbmFstReady",bomb.isLoaded());}else if(accessor.getBlockEntity() instanceof CustomNuclearBlockEntity bomb){CompoundTag payload=new CompoundTag();bomb.payload.save(payload);tag.put("hbmCustomPayload",payload);}}
  @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var tag=accessor.getServerData();if(tag.contains("hbmFstTimer")){tooltip.add(Component.translatable("jade.hbm.fstbmb.clock",TimedBalefireBlockEntity.clock(tag.getInt("hbmFstTimer"))));tooltip.add(Component.translatable(tag.getBoolean("hbmFstStarted")?"jade.hbm.fstbmb.started":tag.getBoolean("hbmFstReady")?"jade.hbm.fstbmb.ready":"jade.hbm.fstbmb.missing"));}else if(tag.contains("hbmCustomPayload")){var p=CustomBombPayload.load(tag.getCompound("hbmCustomPayload"));float[] values={p.tnt(),p.nuke(),p.hydro(),p.amat(),p.dirty(),p.schrab()},adjusted={Math.min(p.tnt(),150),p.nukeAdjusted(),p.hydroAdjusted(),p.amatAdjusted(),Math.min(p.dirty(),100),p.schrabAdjusted()};String[] stages={"tnt","nuke","hydro","amat","dirty","schrab"};for(int i=0;i<stages.length;i++)if(values[i]>0)tooltip.add(Component.translatable("gui.hbm.custom_nuke."+stages[i],values[i],adjusted[i]));if(p.euph()>0)tooltip.add(Component.translatable("gui.hbm.custom_nuke.euph"));}}
 }
}

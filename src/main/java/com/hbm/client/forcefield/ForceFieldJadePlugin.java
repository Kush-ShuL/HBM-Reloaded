package com.hbm.client.forcefield;
import com.hbm.machine.forcefield.*;import net.minecraft.nbt.CompoundTag;import net.minecraft.network.chat.Component;import net.minecraft.resources.ResourceLocation;import snownee.jade.api.*;import snownee.jade.api.config.IPluginConfig;
@WailaPlugin
public final class ForceFieldJadePlugin implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration registration){registration.registerBlockDataProvider(Provider.INSTANCE,ForceFieldBlockEntity.class);}
 @Override public void registerClient(IWailaClientRegistration registration){registration.registerBlockComponent(Provider.INSTANCE,ForceFieldBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {INSTANCE;
  @Override public ResourceLocation getUid(){return ForceFieldContent.id("forcefield_state");}
  @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){if(accessor.getBlockEntity() instanceof ForceFieldBlockEntity field){var data=new CompoundTag();data.putBoolean("on",field.isOn);data.putInt("health",field.health);data.putInt("maxHealth",field.maxHealth);data.putFloat("radius",field.radius);data.putInt("cooldown",field.cooldown);tag.put("hbm:forcefield",data);}}
  @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){if(!accessor.getServerData().contains("hbm:forcefield"))return;var data=accessor.getServerData().getCompound("hbm:forcefield");tooltip.add(Component.translatable(data.getBoolean("on")?"jade.hbm.forcefield.on":"jade.hbm.forcefield.off"));com.hbm.compat.jade.HbmJadeBars.add(tooltip,data.getInt("health"),data.getInt("maxHealth"),Component.translatable("gui.hbm.forcefield.hp",data.getInt("health"),data.getInt("maxHealth")),0x44bb66);tooltip.add(Component.translatable("jade.hbm.forcefield.radius",data.getFloat("radius")));if(data.getInt("cooldown")>0)tooltip.add(Component.translatable("jade.hbm.forcefield.cooldown",data.getInt("cooldown")));}
 }
}

package com.hbm.client.bomb.nuclear;

import com.hbm.bomb.nuclear.NuclearBombBlock;
import com.hbm.bomb.nuclear.integration.NuclearJadePlugin;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.config.IPluginConfig;

public final class NuclearJadeClient {
 public static void register(IWailaClientRegistration registration){registration.registerBlockComponent(new Status(),NuclearBombBlock.class);}
 private static final class Status implements IBlockComponentProvider{
  @Override public ResourceLocation getUid(){return NuclearJadePlugin.UID;}
  @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var tag=accessor.getServerData();if(tag.contains("hbm:nuclear_assembly",10)){boolean ready=tag.getCompound("hbm:nuclear_assembly").getBoolean("ready");tooltip.add(Component.translatable(ready?"desc.gui.soyuz.ready":"bomb.missingComponent").withStyle(ready?ChatFormatting.GREEN:ChatFormatting.RED));}}
 }
 private NuclearJadeClient(){}
}

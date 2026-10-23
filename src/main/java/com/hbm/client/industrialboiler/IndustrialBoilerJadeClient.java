package com.hbm.client.industrialboiler;
import com.hbm.machine.industrialboiler.*;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
public final class IndustrialBoilerJadeClient {
 public static void register(IWailaClientRegistration registration){registration.registerBlockComponent(Provider.INSTANCE,IndustrialBoilerBlock.class);}
 private enum Provider implements IBlockComponentProvider {INSTANCE;@Override public ResourceLocation getUid(){return IndustrialBoilerJadePlugin.UID;}@Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var root=accessor.getServerData();if(!root.contains(IndustrialBoilerJadePlugin.DATA))return;var data=root.getCompound(IndustrialBoilerJadePlugin.DATA);tooltip.add(Component.literal(String.format(Locale.US,"%,d TU",data.getInt("heat"))));}}
}

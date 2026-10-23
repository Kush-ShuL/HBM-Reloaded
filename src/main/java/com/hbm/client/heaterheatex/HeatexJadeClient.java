package com.hbm.client.heaterheatex;
import com.hbm.machine.heaterheatex.*;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
public final class HeatexJadeClient {
 public static void register(IWailaClientRegistration registration){registration.registerBlockComponent(Provider.INSTANCE,HeatexBlock.class);}
 private enum Provider implements IBlockComponentProvider {INSTANCE;@Override public ResourceLocation getUid(){return HeatexJadePlugin.UID;}@Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var root=accessor.getServerData();if(!root.contains(HeatexJadePlugin.DATA))return;var data=root.getCompound(HeatexJadePlugin.DATA);tooltip.add(Component.literal(String.format(Locale.US,"%,d TU",data.getInt("heat"))));}}
}

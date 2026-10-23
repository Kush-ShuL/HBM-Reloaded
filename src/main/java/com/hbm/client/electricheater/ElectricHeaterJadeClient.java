package com.hbm.client.electricheater;
import com.hbm.machine.electricheater.*;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
public final class ElectricHeaterJadeClient {
 public static void register(IWailaClientRegistration registration){registration.registerBlockComponent(Provider.INSTANCE,ElectricHeaterBlock.class);}
 private enum Provider implements IBlockComponentProvider {INSTANCE;@Override public ResourceLocation getUid(){return ElectricHeaterJadePlugin.UID;}@Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var root=accessor.getServerData();if(!root.contains(ElectricHeaterJadePlugin.DATA))return;var data=root.getCompound(ElectricHeaterJadePlugin.DATA);tooltip.add(Component.literal(String.format(Locale.US,"%,d TU",data.getLong("energyTU"))));tooltip.add(Component.literal("-> ").withStyle(ChatFormatting.GREEN).append(Component.literal(data.getLong("consumptionHE")+" HE/t").withStyle(ChatFormatting.WHITE)));tooltip.add(Component.literal("<- ").withStyle(ChatFormatting.RED).append(Component.literal(data.getLong("outputTU")+" TU/t").withStyle(ChatFormatting.WHITE)));}}
}

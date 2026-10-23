package com.hbm.client.missile.launchpad.rusted;
import com.hbm.missile.launchpad.rusted.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
public final class RustedLaunchPadJadeClient {
    public static void register(IWailaClientRegistration registration){registration.registerBlockComponent(Data.INSTANCE,RustedLaunchPadBlock.class);}
    private enum Data implements IBlockComponentProvider{INSTANCE;@Override public ResourceLocation getUid(){return RustedLaunchPadJadePlugin.UID;}@Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var root=accessor.getServerData();if(root.contains(RustedLaunchPadJadePlugin.DATA))tooltip.add(Component.translatable(root.getCompound(RustedLaunchPadJadePlugin.DATA).getBoolean("loaded")?"jade.hbm.launch_pad_rusted.loaded":"jade.hbm.launch_pad_rusted.empty"));}}
}

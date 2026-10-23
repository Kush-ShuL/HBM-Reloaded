package com.hbm.client.anvil;
import com.hbm.machine.anvil.NtmAnvilBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
@WailaPlugin
public final class AnvilJadePlugin implements IWailaPlugin{
    @Override public void registerClient(IWailaClientRegistration event){event.registerBlockComponent(Provider.INSTANCE,NtmAnvilBlock.class);}
    private enum Provider implements IBlockComponentProvider{INSTANCE;@Override public ResourceLocation getUid(){return ResourceLocation.fromNamespaceAndPath("hbm","anvil_tier");}@Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){if(accessor.getBlock() instanceof NtmAnvilBlock block)tooltip.add(Component.translatable("gui.hbm.anvil.tier",block.tier().tier()).withStyle(net.minecraft.ChatFormatting.GOLD));}}
}

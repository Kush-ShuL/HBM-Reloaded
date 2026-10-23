package com.hbm.client.pedestal;
import com.hbm.machine.pedestal.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
@WailaPlugin
public final class PedestalJadePlugin implements IWailaPlugin {
    @Override public void registerClient(IWailaClientRegistration event){event.registerBlockComponent(Provider.INSTANCE,PedestalBlock.class);}
    private enum Provider implements IBlockComponentProvider{INSTANCE;
        @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:pedestal_item");}
        @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){if(accessor.getBlockEntity() instanceof PedestalBlockEntity pedestal&&!pedestal.item().isEmpty())tooltip.add(Component.translatable("gui.hbm.pedestal.contains",pedestal.item().getCount(),pedestal.item().getHoverName()));}
    }
}

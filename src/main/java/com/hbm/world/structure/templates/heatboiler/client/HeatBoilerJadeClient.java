package com.hbm.world.structure.templates.heatboiler.client;
import com.hbm.world.structure.templates.heatboiler.*;
import java.util.Locale;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
public final class HeatBoilerJadeClient {
    public static void register(IWailaClientRegistration registration){registration.registerBlockComponent(Provider.INSTANCE,HeatBoilerBlock.class);}
    private enum Provider implements IBlockComponentProvider{INSTANCE;
        @Override public ResourceLocation getUid(){return HeatBoilerJade.UID;}
        @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var data=accessor.getServerData();if(data.contains(HeatBoilerJade.DATA))tooltip.add(Component.literal(String.format(Locale.US,"%,d TU",data.getCompound(HeatBoilerJade.DATA).getInt("heat"))));}
    }
    private HeatBoilerJadeClient(){}
}

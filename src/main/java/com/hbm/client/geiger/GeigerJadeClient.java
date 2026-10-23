package com.hbm.client.geiger;

import com.hbm.geiger.GeigerBlock;
import com.hbm.geiger.GeigerReadings;
import com.hbm.geiger.compat.GeigerJadePlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.config.IPluginConfig;

public final class GeigerJadeClient {
    private GeigerJadeClient() {}
    public static void register(IWailaClientRegistration registration) { registration.registerBlockComponent(Data.INSTANCE,GeigerBlock.class); }
    private enum Data implements IBlockComponentProvider {
        INSTANCE;
        @Override public ResourceLocation getUid() { return GeigerJadePlugin.UID; }
        @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config) {
            var data=accessor.getServerData();if(!data.contains(GeigerJadePlugin.DATA)) return;
            int rads=data.getInt(GeigerJadePlugin.DATA);
            tooltip.add(Component.translatable("geiger.chunkRad").append(Component.literal(" "+rads+" RAD/s").withStyle(GeigerReadings.rateColor(rads))));
        }
    }
}

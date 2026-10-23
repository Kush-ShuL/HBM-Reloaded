package com.hbm.client.missileassembly;

import com.hbm.machine.missileassembly.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

public final class MissileAssemblyJadeClient {
    public static void register(IWailaClientRegistration event) { event.registerBlockComponent(Data.INSTANCE, MissileAssemblyBlock.class); }
    private enum Data implements IBlockComponentProvider {
        INSTANCE;
        @Override public ResourceLocation getUid() { return MissileAssemblyJadePlugin.UID; }
        @Override public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            if (accessor.getServerData().contains(MissileAssemblyJadePlugin.DATA)) tooltip.add(Component.translatable("jade.hbm.missile_assembly."
                    + (accessor.getServerData().getCompound(MissileAssemblyJadePlugin.DATA).getBoolean("ready") ? "ready" : "invalid")));
        }
    }
    private MissileAssemblyJadeClient() {}
}

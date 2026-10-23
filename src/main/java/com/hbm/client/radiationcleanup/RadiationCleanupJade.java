package com.hbm.client.radiationcleanup;

import com.hbm.machine.radiationcleanup.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

/** Static information uses the true looked-at block tier; it does not invent a client radiation-grid copy. */
@WailaPlugin
public final class RadiationCleanupJade implements IWailaPlugin {
    @Override public void registerClient(IWailaClientRegistration registration){registration.registerBlockComponent(Provider.INSTANCE,AbsorberBlock.class);registration.registerBlockComponent(Provider.INSTANCE,DeconBlock.class);}
    private enum Provider implements IBlockComponentProvider{
        INSTANCE;
        @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:radiation_cleanup");}
        @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){
            var state=accessor.getBlockState();
            if(state.getBlock() instanceof AbsorberBlock){var tier=AbsorberBlock.tier(state.getValue(AbsorberBlock.META));tooltip.add(Component.translatable("block.hbm.rad_absorber."+tier.name().toLowerCase(java.util.Locale.ROOT)));tooltip.add(Component.translatable("jade.hbm.absorber_rate",tier.amount));}
            else if(state.getBlock() instanceof DeconBlock)tooltip.add(Component.translatable("jade.hbm.decon_effect"));
        }
    }
}

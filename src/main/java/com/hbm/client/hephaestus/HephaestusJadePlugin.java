package com.hbm.client.hephaestus;
import com.hbm.machine.hephaestus.*;
import java.util.Locale;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
/** Source heat is an aggregate TU rate with no maximum or task; do not invent a progress bar. */
@WailaPlugin public final class HephaestusJadePlugin implements IWailaPlugin {
    @Override public void register(IWailaCommonRegistration e){e.registerBlockDataProvider(Provider.INSTANCE,HephaestusEntity.class);e.registerBlockDataProvider(Provider.INSTANCE,HephaestusPort.class);}
    @Override public void registerClient(IWailaClientRegistration e){e.registerBlockComponent(Provider.INSTANCE,HephaestusBlock.class);}
    private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {
        INSTANCE;
        @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:hephaestus_heat");}
        @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){var be=accessor.getBlockEntity();if(be instanceof HephaestusPort port)be=port.core();if(be instanceof HephaestusEntity machine)tag.putInt("hbm:geothermal_heat",machine.getTotalHeat());}
        @Override public void appendTooltip(ITooltip tip,BlockAccessor accessor,IPluginConfig config){if(accessor.getServerData().contains("hbm:geothermal_heat"))tip.add(Component.translatable("gui.hbm.hephaestus.heat",String.format(Locale.US,"%,d",accessor.getServerData().getInt("hbm:geothermal_heat"))));}
    }
}

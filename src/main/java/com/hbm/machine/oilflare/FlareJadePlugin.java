package com.hbm.machine.oilflare;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
/** The exact source EnergyControl active/consumption/output fields, alongside shared native resource bars. */
@WailaPlugin public final class FlareJadePlugin implements IWailaPlugin {
    @Override public void register(IWailaCommonRegistration e){e.registerBlockDataProvider(Provider.INSTANCE,FlareEntity.class);e.registerBlockDataProvider(Provider.INSTANCE,FlarePort.class);}
    @Override public void registerClient(IWailaClientRegistration e){e.registerBlockComponent(Provider.INSTANCE,FlareBlock.class);}
    private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {INSTANCE;
        @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:gas_flare");}
        @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){var f=accessor.getBlockEntity() instanceof FlareEntity flare?flare:accessor.getBlockEntity() instanceof FlarePort port?port.core():null;if(f!=null){tag.putBoolean("flareActive",f.fluidUsed>0);tag.putInt("flareUsed",f.fluidUsed);tag.putString("flareOutput",f.output.toString());tag.putBoolean("flareTilted",f.tilted);}}
        @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var tag=accessor.getServerData();if(!tag.contains("flareActive"))return;tooltip.add(Component.translatable("gui.hbm.flare.active."+tag.getBoolean("flareActive")));tooltip.add(Component.translatable("gui.hbm.flare.rate",tag.getInt("flareUsed")));tooltip.add(Component.translatable("gui.hbm.flare.power",tag.getString("flareOutput")));if(tag.getBoolean("flareTilted"))tooltip.add(Component.translatable("gui.hbm.flare.tilted"));}
    }
}

package com.hbm.client.reactor.iter.utilities;
import com.hbm.machine.reactor.iter.utilities.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
/** Original boiler look-overlay TU plus native MachineView's actual private water/steam buffers. */
@WailaPlugin
public final class FusionBoilerJade implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration registration){registration.registerBlockDataProvider(Provider.INSTANCE,FusionUtilityBlock.class);}@Override public void registerClient(IWailaClientRegistration registration){registration.registerBlockComponent(Provider.INSTANCE,FusionUtilityBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {INSTANCE;@Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:fusion_boiler_status");}@Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){var block=(FusionUtilityBlock)accessor.getBlockState().getBlock();var pos=block.findCore(accessor.getLevel(),accessor.getPosition());if(pos!=null&&accessor.getLevel().getBlockEntity(pos) instanceof FusionBoilerEntity boiler)tag.putLong("hbmFusionBoilerTU",boiler.plasmaEnergySync);}@Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var tag=accessor.getServerData();if(tag.contains("hbmFusionBoilerTU"))tooltip.add(Component.translatable("jade.hbm.fusion_boiler.heat",tag.getLong("hbmFusionBoilerTU")));}}
}

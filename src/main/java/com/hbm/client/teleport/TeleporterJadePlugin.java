package com.hbm.client.teleport;

import com.hbm.machine.teleport.TeleporterBlock;
import com.hbm.machine.teleport.TeleporterEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

/** Adds the actual destination; the sole existing MachineView provider supplies the native HE resource bar. */
@WailaPlugin
public final class TeleporterJadePlugin implements IWailaPlugin {
    @Override public void register(IWailaCommonRegistration registration){registration.registerBlockDataProvider(Provider.INSTANCE,TeleporterEntity.class);}
    @Override public void registerClient(IWailaClientRegistration registration){registration.registerBlockComponent(Provider.INSTANCE,TeleporterBlock.class);}
    private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {
        INSTANCE;
        @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:teleporter_destination");}
        @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){if(accessor.getBlockEntity() instanceof TeleporterEntity machine){var destination=new CompoundTag();destination.putInt("x",machine.targetX);destination.putInt("y",machine.targetY);destination.putInt("z",machine.targetZ);destination.putString("dimension",machine.dimensionLabel());tag.put("hbm:teleport",destination);}}
        @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){if(!accessor.getServerData().contains("hbm:teleport"))return;var destination=accessor.getServerData().getCompound("hbm:teleport");tooltip.add(destination.getInt("y")==-1?Component.translatable("overlay.hbm.teleporter.no_destination"):Component.translatable("overlay.hbm.teleporter.destination",destination.getInt("x"),destination.getInt("y"),destination.getInt("z"),destination.getString("dimension")));}
    }
}

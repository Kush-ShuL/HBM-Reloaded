package com.hbm.client.blastfurnace;

import com.hbm.machine.blastfurnace.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

/** Native MachineView supplies tanks/task-only progress; this is the actual source fuel store. */
@WailaPlugin
public final class BlastFurnaceJadePlugin implements IWailaPlugin {
    @Override public void register(IWailaCommonRegistration event){event.registerBlockDataProvider(Provider.INSTANCE,BlastFurnaceBlockEntity.class);event.registerBlockDataProvider(Provider.INSTANCE,BlastFurnacePort.class);}
    @Override public void registerClient(IWailaClientRegistration event){event.registerBlockComponent(Provider.INSTANCE,BlastFurnaceBlock.class);}
    private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {
        INSTANCE;
        @Override public ResourceLocation getUid(){return ResourceLocation.fromNamespaceAndPath("hbm","blast_furnace_fuel");}
        @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){var machine=accessor.getBlockEntity() instanceof BlastFurnaceBlockEntity core?core:accessor.getBlockEntity() instanceof BlastFurnacePort port?port.core():null;if(machine!=null)tag.putInt("hbm:blast_fuel",machine.fuel);}
        @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var data=accessor.getServerData();if(data.contains("hbm:blast_fuel"))com.hbm.compat.jade.HbmJadeBars.add(tooltip,data.getInt("hbm:blast_fuel"),BlastFurnaceBlockEntity.MAX_FUEL,Component.translatable("jade.hbm.blast.fuel",data.getInt("hbm:blast_fuel"),BlastFurnaceBlockEntity.MAX_FUEL),0xE69B51);}
    }
}

package com.hbm.client.reactor.pwr;

import com.hbm.machine.reactor.pwr.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

/** Resource heat bars remain separate from task-only fuel-depletion progress. */
@WailaPlugin
public final class PwrJadePlugin implements IWailaPlugin {
    @Override public void register(IWailaCommonRegistration registration){registration.registerBlockDataProvider(Provider.INSTANCE,PwrControllerBlockEntity.class);registration.registerBlockDataProvider(Provider.INSTANCE,PwrAssemblyBlockEntity.class);}
    @Override public void registerClient(IWailaClientRegistration registration){registration.registerBlockComponent(Provider.INSTANCE,PwrControllerBlock.class);registration.registerBlockComponent(Provider.INSTANCE,PwrAssemblyBlock.class);}
    private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {
        INSTANCE;
        @Override public ResourceLocation getUid(){return PwrContent.id("pwr_state");}
        @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){var core=accessor.getBlockEntity() instanceof PwrControllerBlockEntity direct?direct:accessor.getBlockEntity() instanceof PwrAssemblyBlockEntity part?part.core():null;if(core==null)return;var data=new CompoundTag();data.putLong("core",core.coreHeat);data.putLong("hull",core.hullHeat);data.putLong("coreMax",core.coreHeatCapacity);data.putBoolean("assembled",core.assembled);data.putInt("fuel",core.amountLoaded);data.putInt("rods",core.rodCount);data.putDouble("flux",core.flux);tag.put("hbm:pwr",data);}
        @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){var root=accessor.getServerData();if(!root.contains("hbm:pwr"))return;var data=root.getCompound("hbm:pwr");tooltip.add(Component.translatable(data.getBoolean("assembled")?"jade.hbm.pwr.assembled":"jade.hbm.pwr.unassembled"));com.hbm.compat.jade.HbmJadeBars.add(tooltip,data.getLong("core"),data.getLong("coreMax"),Component.translatable("gui.hbm.pwr.core",data.getLong("core"),data.getLong("coreMax")),0xE86033);com.hbm.compat.jade.HbmJadeBars.add(tooltip,data.getLong("hull"),PwrControllerBlockEntity.hullHeatCapacityBase,Component.translatable("gui.hbm.pwr.hull",data.getLong("hull"),PwrControllerBlockEntity.hullHeatCapacityBase),0xE69B51);tooltip.add(Component.translatable("jade.hbm.pwr.fuel",data.getInt("fuel"),data.getInt("rods")));tooltip.add(Component.translatable("jade.hbm.pwr.flux",data.getDouble("flux")));}
    }
}

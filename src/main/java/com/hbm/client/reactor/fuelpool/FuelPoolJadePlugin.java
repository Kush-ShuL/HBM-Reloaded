package com.hbm.client.reactor.fuelpool;

import com.hbm.machine.reactor.fuelpool.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

/** Actual neighboring water; the stochastic source operation has no invented progress percentage. */
@WailaPlugin
public final class FuelPoolJadePlugin implements IWailaPlugin {
    @Override public void register(IWailaCommonRegistration event){event.registerBlockDataProvider(Provider.INSTANCE,FuelPoolBlockEntity.class);}
    @Override public void registerClient(IWailaClientRegistration event){event.registerBlockComponent(Provider.INSTANCE,FuelPoolBlock.class);}
    private enum Provider implements IServerDataProvider<BlockAccessor>,IBlockComponentProvider {INSTANCE;
        @Override public ResourceLocation getUid(){return FuelPoolContent.id("fuel_pool_state");}
        @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){if(accessor.getBlockEntity() instanceof FuelPoolBlockEntity pool)tag.putInt("hbm:pool_water",pool.waterSides());}
        @Override public void appendTooltip(ITooltip tooltip,BlockAccessor accessor,IPluginConfig config){if(accessor.getServerData().contains("hbm:pool_water"))tooltip.add(Component.translatable("jade.hbm.fuel_pool.water",accessor.getServerData().getInt("hbm:pool_water")));}
    }
}

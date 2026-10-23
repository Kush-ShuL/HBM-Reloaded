package com.hbm.machine.generator.steamengine;
import com.hbm.api.machine.MachineView;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
/** Source ordinary collision cells have no BE: inspect their real core without adding fake machines. */
@WailaPlugin
public final class SteamEngineJadePlugin implements IWailaPlugin {
    @Override public void register(IWailaCommonRegistration registration){registration.registerBlockDataProvider(Provider.INSTANCE,SteamEngineBlock.class);}
    private enum Provider implements IServerDataProvider<BlockAccessor> {INSTANCE;
        @Override public ResourceLocation getUid(){return ResourceLocation.parse("hbm:piston_steam_engine_body");}
        @Override public boolean shouldRequestData(BlockAccessor accessor){return accessor.getBlockState().getBlock() instanceof SteamEngineBlock&&!(accessor.getBlockEntity() instanceof MachineView);}
        @Override public void appendServerData(CompoundTag data,BlockAccessor accessor){if(!(accessor.getBlockState().getBlock() instanceof SteamEngineBlock block)||accessor.getBlockEntity() instanceof MachineView)return;var core=block.findCore(accessor.getLevel(),accessor.getPosition());if(core==null||!(accessor.getLevel().getBlockEntity(core) instanceof SteamEngineBlockEntity engine))return;var state=new CompoundTag();state.putString("energyExact",engine.getPowerExact().toString());state.putString("energyCapacityExact",engine.getMaxPowerExact().toString());var tanks=new ListTag();for(var tank:engine.tanks()){var entry=new CompoundTag();entry.putString("name",tank.name());entry.putBoolean("literal",tank.literal());entry.putLong("amount",tank.amount());entry.putLong("capacity",tank.capacity());entry.putInt("color",tank.color());tanks.add(entry);}state.put("tanks",tanks);data.put("hbm:machine",state);}
    }
}

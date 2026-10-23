package com.hbm.world.structure.templates.heatboiler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.*;
/** Actual source look overlay: no inventory/menu exists, and a burst boiler hides its status. */
@WailaPlugin
public final class HeatBoilerJade implements IWailaPlugin {
    public static final String DATA="hbm:heat_boiler_status";public static final ResourceLocation UID=ResourceLocation.parse(DATA);
    @Override public void register(IWailaCommonRegistration registration){registration.registerBlockDataProvider(Provider.INSTANCE,BlockEntity.class);}
    @Override public void registerClient(IWailaClientRegistration registration){com.hbm.world.structure.templates.heatboiler.client.HeatBoilerJadeClient.register(registration);}
    private enum Provider implements IServerDataProvider<BlockAccessor>{INSTANCE;
        @Override public ResourceLocation getUid(){return UID;}
        @Override public boolean shouldRequestData(BlockAccessor accessor){return accessor.getBlockState().getBlock() instanceof HeatBoilerBlock;}
        @Override public void appendServerData(CompoundTag data,BlockAccessor accessor){if(!(accessor.getBlockState().getBlock() instanceof HeatBoilerBlock block))return;var pos=block.findCore(accessor.getLevel(),accessor.getPosition());if(pos==null||!(accessor.getLevel().getBlockEntity(pos) instanceof HeatBoilerBlockEntity boiler)||boiler.hasExploded)return;var state=new CompoundTag();state.putInt("heat",boiler.heat);data.put(DATA,state);}
    }
}

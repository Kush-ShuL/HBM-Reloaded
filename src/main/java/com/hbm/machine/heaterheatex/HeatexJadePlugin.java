package com.hbm.machine.heaterheatex;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.*;
/** Original look-overlay TU count; private fluid capacities use the shared resource bars. */
@WailaPlugin
public final class HeatexJadePlugin implements IWailaPlugin {
 public static final String DATA="hbm:heater_heatex_status";public static final ResourceLocation UID=ResourceLocation.fromNamespaceAndPath("hbm","heater_heatex_status");
 @Override public void register(IWailaCommonRegistration registration){registration.registerBlockDataProvider(Provider.INSTANCE,BlockEntity.class);}
 @Override public void registerClient(IWailaClientRegistration registration){com.hbm.client.heaterheatex.HeatexJadeClient.register(registration);}
 private enum Provider implements IServerDataProvider<BlockAccessor>{INSTANCE;@Override public ResourceLocation getUid(){return UID;}@Override public boolean shouldRequestData(BlockAccessor accessor){return accessor.getBlockState().getBlock() instanceof HeatexBlock;}@Override public void appendServerData(CompoundTag data,BlockAccessor accessor){if(!(accessor.getBlockState().getBlock() instanceof HeatexBlock block))return;BlockPos pos=block.findCore(accessor.getLevel(),accessor.getPosition());if(pos==null||!(accessor.getLevel().getBlockEntity(pos) instanceof HeatexBlockEntity heater))return;var state=new CompoundTag();state.putInt("heat",heater.getHeatStored());data.put(DATA,state);}}
}

package com.hbm.machine.industrialboiler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.*;
/** Original look-overlay TU count; private fluid capacities use the shared resource bars. */
@WailaPlugin
public final class IndustrialBoilerJadePlugin implements IWailaPlugin {
 public static final String DATA="hbm:industrial_boiler_status";public static final ResourceLocation UID=ResourceLocation.fromNamespaceAndPath("hbm","industrial_boiler_status");
 @Override public void register(IWailaCommonRegistration registration){registration.registerBlockDataProvider(Provider.INSTANCE,BlockEntity.class);}
 @Override public void registerClient(IWailaClientRegistration registration){com.hbm.client.industrialboiler.IndustrialBoilerJadeClient.register(registration);}
 private enum Provider implements IServerDataProvider<BlockAccessor>{INSTANCE;@Override public ResourceLocation getUid(){return UID;}@Override public boolean shouldRequestData(BlockAccessor accessor){return accessor.getBlockState().getBlock() instanceof IndustrialBoilerBlock;}@Override public void appendServerData(CompoundTag data,BlockAccessor accessor){if(!(accessor.getBlockState().getBlock() instanceof IndustrialBoilerBlock block))return;BlockPos pos=block.findCore(accessor.getLevel(),accessor.getPosition());if(pos==null||!(accessor.getLevel().getBlockEntity(pos) instanceof IndustrialBoilerBlockEntity heater))return;var state=new CompoundTag();state.putInt("heat",heater.heat);data.put(DATA,state);}}
}

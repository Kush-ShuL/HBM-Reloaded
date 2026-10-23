package com.hbm.machine.electricheater;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.*;
/** Source look-overlay heat and input/output rates. Its heat buffer has no source capacity to invent a bar for. */
@WailaPlugin
public final class ElectricHeaterJadePlugin implements IWailaPlugin {
 public static final String DATA="hbm:electric_heater_status";public static final ResourceLocation UID=ResourceLocation.fromNamespaceAndPath("hbm","electric_heater_status");
 @Override public void register(IWailaCommonRegistration registration){registration.registerBlockDataProvider(Provider.INSTANCE,BlockEntity.class);}
 @Override public void registerClient(IWailaClientRegistration registration){com.hbm.client.electricheater.ElectricHeaterJadeClient.register(registration);}
 private enum Provider implements IServerDataProvider<BlockAccessor>{INSTANCE;@Override public ResourceLocation getUid(){return UID;}@Override public boolean shouldRequestData(BlockAccessor accessor){return accessor.getBlockState().getBlock() instanceof ElectricHeaterBlock;}@Override public void appendServerData(CompoundTag data,BlockAccessor accessor){if(!(accessor.getBlockState().getBlock() instanceof ElectricHeaterBlock block))return;BlockPos pos=block.findCore(accessor.getLevel(),accessor.getPosition());if(pos==null||!(accessor.getLevel().getBlockEntity(pos) instanceof ElectricHeaterBlockEntity heater))return;var state=new CompoundTag();heater.provideExtraInfo(state);data.put(DATA,state);}}
}

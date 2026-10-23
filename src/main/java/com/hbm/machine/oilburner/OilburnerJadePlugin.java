package com.hbm.machine.oilburner;
import com.hbm.fluid.FluidTraits;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.*;
/** Original block HUD's input/output rates. Heat/fuel/smoke resource bars use shared MachineView. */
@WailaPlugin
public final class OilburnerJadePlugin implements IWailaPlugin {
 public static final String DATA="hbm:heater_oilburner_status";public static final ResourceLocation UID=ResourceLocation.fromNamespaceAndPath("hbm","heater_oilburner_status");
 @Override public void register(IWailaCommonRegistration registration){registration.registerBlockDataProvider(Provider.INSTANCE,BlockEntity.class);}@Override public void registerClient(IWailaClientRegistration registration){com.hbm.client.oilburner.OilburnerJadeClient.register(registration);}
 private enum Provider implements IServerDataProvider<BlockAccessor>{INSTANCE;@Override public ResourceLocation getUid(){return UID;}@Override public boolean shouldRequestData(BlockAccessor accessor){return accessor.getBlockState().getBlock() instanceof OilburnerBlock;}@Override public void appendServerData(CompoundTag data,BlockAccessor accessor){if(!(accessor.getBlockState().getBlock() instanceof OilburnerBlock block))return;var pos=block.findCore(accessor.getLevel(),accessor.getPosition());if(pos==null||!(accessor.getLevel().getBlockEntity(pos) instanceof OilburnerBlockEntity heater))return;var state=new CompoundTag();state.putInt("rate",heater.setting);state.putBoolean("flammable",heater.tank.getTankType().hasTrait(FluidTraits.Flammable.class));state.putInt("heat",heater.lookHeatPerTick());data.put(DATA,state);}}
}

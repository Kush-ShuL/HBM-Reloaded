package com.hbm.machine.excavator;
import com.hbm.api.machine.MachineView;import net.minecraft.nbt.*;import net.minecraft.resources.ResourceLocation;import snownee.jade.api.*;
/** Ordinary collision proxies have no BE; resolve their actual core for shared Jade bars. */
@WailaPlugin public final class ExcavatorJadePlugin implements IWailaPlugin {
 @Override public void register(IWailaCommonRegistration event){event.registerBlockDataProvider(Provider.INSTANCE,ExcavatorBlock.class);}
 private enum Provider implements IServerDataProvider<BlockAccessor>{INSTANCE;
  @Override public ResourceLocation getUid(){return ExcavatorContent.id("excavator_body");}
  @Override public boolean shouldRequestData(BlockAccessor accessor){return accessor.getBlockState().getBlock() instanceof ExcavatorBlock&&!(accessor.getBlockEntity() instanceof MachineView);}
  @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){if(!(accessor.getBlockState().getBlock() instanceof ExcavatorBlock block)||accessor.getBlockEntity() instanceof MachineView)return;var pos=block.findCore(accessor.getLevel(),accessor.getPosition());if(pos==null||!(accessor.getLevel().getBlockEntity(pos) instanceof ExcavatorBlockEntity machine))return;var data=new CompoundTag();data.putString("energyExact",machine.getPowerExact().toString());data.putString("energyCapacityExact",machine.getMaxPowerExact().toString());var tanks=new ListTag();for(var tank:machine.tanks()){var t=new CompoundTag();t.putString("name",tank.name());t.putBoolean("literal",tank.literal());t.putLong("amount",tank.amount());t.putLong("capacity",tank.capacity());t.putInt("color",tank.color());tanks.add(t);}data.put("tanks",tanks);var progress=new ListTag();for(var view:machine.progressBars()){var t=new CompoundTag();t.putString("name",view.name());t.putDouble("value",view.value());t.putDouble("maximum",view.maximum());t.putInt("color",view.color());progress.add(t);}data.put("progress",progress);tag.put("hbm:machine",data);}
 }
}

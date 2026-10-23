package com.hbm.machine.reactor.icf;
import com.hbm.energy.*;
import java.math.BigInteger;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
public final class IcfFormedEntity extends BlockEntity implements EnergyReceiver,SidedEnergyConnector{
 public BlockState original;public BlockPos controller=BlockPos.ZERO;private IcfLaserEntity cachedCore;
 public IcfFormedEntity(BlockPos pos,BlockState state){super(IcfReactorContent.FORMED_ENTITY.get(),pos,state);}
 public IcfLaserEntity core(){if(cachedCore!=null&&!cachedCore.isRemoved())return cachedCore;if(level!=null&&level.hasChunkAt(controller)&&level.getBlockEntity(controller) instanceof IcfLaserEntity core)return cachedCore=core;return null;}
 public static void tick(Level level,BlockPos pos,BlockState state,IcfFormedEntity formed){if(level.isClientSide||level.getGameTime()%20!=0||formed.original==null)return;var core=formed.core();if(core!=null?!core.assembled:level.hasChunkAt(formed.controller))IcfReactorContent.FORMED.get().restore(level,pos);}
 private boolean port(){return original!=null&&getBlockState().getValue(IcfFormedBlock.PORT);}@Override public boolean isLoaded(){return !isRemoved();}@Override public boolean canConnect(Direction side){return side!=null&&getBlockState().getValue(IcfFormedBlock.PORT);}@Override public long getPower(){var core=core();return port()&&core!=null?core.getPower():0;}@Override public long getMaxPower(){var core=core();return port()&&core!=null?core.getMaxPower():0;}@Override public void setPower(long value){var core=core();if(port()&&core!=null)core.setPower(value);}@Override public BigInteger getPowerExact(){var core=core();return port()&&core!=null?core.getPowerExact():BigInteger.ZERO;}@Override public BigInteger getMaxPowerExact(){var core=core();return port()&&core!=null?core.getMaxPowerExact():BigInteger.ZERO;}@Override public void setPowerExact(BigInteger value){var core=core();if(port()&&core!=null)core.setPowerExact(value);}
 @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);if(original!=null){tag.put("original",NbtUtils.writeBlockState(original));tag.putLong("controller",controller.asLong());}}@Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);if(tag.contains("original")){original=NbtUtils.readBlockState(lookup.lookupOrThrow(net.minecraft.core.registries.Registries.BLOCK),tag.getCompound("original"));controller=BlockPos.of(tag.getLong("controller"));}else original=null;cachedCore=null;}
}

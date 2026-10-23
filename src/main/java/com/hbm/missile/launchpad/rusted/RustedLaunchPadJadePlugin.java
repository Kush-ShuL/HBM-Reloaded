package com.hbm.missile.launchpad.rusted;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.*;
/** Optional overlay of the actual source loaded state; no fake energy/fluid or capacity bar. */
@WailaPlugin
public final class RustedLaunchPadJadePlugin implements IWailaPlugin {
    public static final String DATA="hbm:rusted_launch_pad";public static final ResourceLocation UID=ResourceLocation.parse(DATA);
    @Override public void register(IWailaCommonRegistration registration){registration.registerBlockDataProvider(Data.INSTANCE,BlockEntity.class);}
    @Override public void registerClient(IWailaClientRegistration registration){com.hbm.client.missile.launchpad.rusted.RustedLaunchPadJadeClient.register(registration);}
    private enum Data implements IServerDataProvider<BlockAccessor>{INSTANCE;@Override public ResourceLocation getUid(){return UID;}@Override public boolean shouldRequestData(BlockAccessor accessor){return accessor.getBlockState().getBlock() instanceof RustedLaunchPadBlock;}@Override public void appendServerData(CompoundTag tag,BlockAccessor accessor){if(!(accessor.getBlockState().getBlock() instanceof RustedLaunchPadBlock block))return;BlockPos core=block.findCore(accessor.getLevel(),accessor.getPosition());if(core!=null&&accessor.getLevel().getBlockEntity(core) instanceof RustedLaunchPadBlockEntity pad){var data=new CompoundTag();data.putBoolean("loaded",pad.missileLoaded);tag.put(DATA,data);}}}
}

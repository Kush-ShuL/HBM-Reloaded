package com.hbm.machine.turret.himars;
import com.hbm.client.turret.himars.HimarsTurretJadeClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
@WailaPlugin public final class HimarsTurretJadePlugin implements IWailaPlugin {
    public static final ResourceLocation UID=ResourceLocation.parse("hbm:himars_turret_status");public static final String DATA="hbm:himars_turret";
    @Override public void register(IWailaCommonRegistration event){event.registerBlockDataProvider(Data.INSTANCE,HimarsTurretEntity.class);event.registerBlockDataProvider(Data.INSTANCE,HimarsTurretProxy.class);}
    @Override public void registerClient(IWailaClientRegistration event){HimarsTurretJadeClient.register(event);}
    private enum Data implements IServerDataProvider<BlockAccessor>{INSTANCE;@Override public ResourceLocation getUid(){return UID;}@Override public void appendServerData(CompoundTag root,BlockAccessor accessor){var tile=accessor.getBlockEntity();var turret=tile instanceof HimarsTurretEntity core?core:tile instanceof HimarsTurretProxy proxy?proxy.core():null;if(turret==null)return;var tag=new CompoundTag();tag.putBoolean("active",turret.active());tag.putInt("kills",turret.stattrak);tag.putShort("mode",turret.mode);tag.putInt("queued",turret.queuedTargets());tag.putInt("ammo",turret.ammo);tag.putInt("type",turret.typeLoaded);if(turret.target!=null)tag.putString("target",Component.Serializer.toJson(turret.target.getDisplayName(),accessor.getLevel().registryAccess()));root.put(DATA,tag);}}
}

package com.hbm.machine.turret.tauon;
import com.hbm.client.turret.tauon.TauonTurretJadeClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
@WailaPlugin public final class TauonTurretJadePlugin implements IWailaPlugin {
    public static final ResourceLocation UID=ResourceLocation.parse("hbm:tauon_turret_status");public static final String DATA="hbm:tauon_turret";
    @Override public void register(IWailaCommonRegistration event){event.registerBlockDataProvider(Data.INSTANCE,TauonTurretEntity.class);event.registerBlockDataProvider(Data.INSTANCE,TauonTurretProxy.class);}
    @Override public void registerClient(IWailaClientRegistration event){TauonTurretJadeClient.register(event);}
    private enum Data implements IServerDataProvider<BlockAccessor>{INSTANCE;@Override public ResourceLocation getUid(){return UID;}@Override public void appendServerData(CompoundTag root,BlockAccessor accessor){var tile=accessor.getBlockEntity();var turret=tile instanceof TauonTurretEntity core?core:tile instanceof TauonTurretProxy proxy?proxy.core():null;if(turret==null)return;var tag=new CompoundTag();tag.putBoolean("active",turret.active());tag.putInt("kills",turret.stattrak);if(turret.target!=null)tag.putString("target",Component.Serializer.toJson(turret.target.getDisplayName(),accessor.getLevel().registryAccess()));root.put(DATA,tag);}}
}

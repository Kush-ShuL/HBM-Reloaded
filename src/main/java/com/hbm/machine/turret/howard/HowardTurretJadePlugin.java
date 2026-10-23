package com.hbm.machine.turret.howard;
import com.hbm.client.turret.howard.HowardTurretJadeClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
@WailaPlugin public final class HowardTurretJadePlugin implements IWailaPlugin {
    public static final ResourceLocation UID=ResourceLocation.parse("hbm:howard_turret_status");public static final String DATA="hbm:howard_turret";
    @Override public void register(IWailaCommonRegistration event){event.registerBlockDataProvider(Data.INSTANCE,HowardTurretEntity.class);event.registerBlockDataProvider(Data.INSTANCE,HowardTurretProxy.class);}
    @Override public void registerClient(IWailaClientRegistration event){HowardTurretJadeClient.register(event);}
    private enum Data implements IServerDataProvider<BlockAccessor>{INSTANCE;@Override public ResourceLocation getUid(){return UID;}@Override public void appendServerData(CompoundTag root,BlockAccessor accessor){var tile=accessor.getBlockEntity();var turret=tile instanceof HowardTurretEntity core?core:tile instanceof HowardTurretProxy proxy?proxy.core():null;if(turret==null)return;var tag=new CompoundTag();tag.putBoolean("active",turret.active());tag.putInt("kills",turret.stattrak);if(turret.target!=null)tag.putString("target",Component.Serializer.toJson(turret.target.getDisplayName(),accessor.getLevel().registryAccess()));root.put(DATA,tag);}}
}

package com.hbm.machine.turret.fritz;
import com.hbm.client.turret.fritz.FritzTurretJadeClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
@WailaPlugin public final class FritzTurretJadePlugin implements IWailaPlugin {
    public static final ResourceLocation UID=ResourceLocation.parse("hbm:fritz_turret_status");public static final String DATA="hbm:fritz_turret";
    @Override public void register(IWailaCommonRegistration event){event.registerBlockDataProvider(Data.INSTANCE,FritzTurretEntity.class);event.registerBlockDataProvider(Data.INSTANCE,FritzTurretProxy.class);}
    @Override public void registerClient(IWailaClientRegistration event){FritzTurretJadeClient.register(event);}
    private enum Data implements IServerDataProvider<BlockAccessor>{INSTANCE;@Override public ResourceLocation getUid(){return UID;}@Override public void appendServerData(CompoundTag root,BlockAccessor accessor){var tile=accessor.getBlockEntity();var turret=tile instanceof FritzTurretEntity core?core:tile instanceof FritzTurretProxy proxy?proxy.core():null;if(turret==null)return;var tag=new CompoundTag();tag.putBoolean("active",turret.active());tag.putInt("kills",turret.stattrak);if(turret.target!=null)tag.putString("target",Component.Serializer.toJson(turret.target.getDisplayName(),accessor.getLevel().registryAccess()));root.put(DATA,tag);}}
}

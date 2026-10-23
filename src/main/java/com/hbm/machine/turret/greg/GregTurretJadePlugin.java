package com.hbm.machine.turret.greg;
import com.hbm.client.turret.greg.GregTurretJadeClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
@WailaPlugin public final class GregTurretJadePlugin implements IWailaPlugin {
    public static final ResourceLocation UID=ResourceLocation.parse("hbm:greg_turret_status");public static final String DATA="hbm:greg_turret";
    @Override public void register(IWailaCommonRegistration event){event.registerBlockDataProvider(Data.INSTANCE,GregTurretEntity.class);event.registerBlockDataProvider(Data.INSTANCE,GregTurretProxy.class);}
    @Override public void registerClient(IWailaClientRegistration event){GregTurretJadeClient.register(event);}
    private enum Data implements IServerDataProvider<BlockAccessor>{INSTANCE;@Override public ResourceLocation getUid(){return UID;}@Override public void appendServerData(CompoundTag root,BlockAccessor accessor){var tile=accessor.getBlockEntity();var turret=tile instanceof GregTurretEntity core?core:tile instanceof GregTurretProxy proxy?proxy.core():null;if(turret==null)return;var tag=new CompoundTag();tag.putBoolean("active",turret.active());tag.putInt("kills",turret.stattrak);tag.putShort("mode",turret.mode);tag.putInt("queued",turret.targetQueue.size());if(turret.target!=null)tag.putString("target",Component.Serializer.toJson(turret.target.getDisplayName(),accessor.getLevel().registryAccess()));root.put(DATA,tag);}}
}

package com.hbm.machine.turret;
import com.hbm.client.turret.TurretJadeClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;
/** Genuine server target/activity/ammunition, alongside the existing MachineView HE provider. */
@WailaPlugin public final class TurretJadePlugin implements IWailaPlugin {
    public static final ResourceLocation UID=ResourceLocation.parse("hbm:turret_sentry_status");public static final String DATA="hbm:turret_sentry";
    @Override public void register(IWailaCommonRegistration event){event.registerBlockDataProvider(Data.INSTANCE,SentryEntity.class);}
    @Override public void registerClient(IWailaClientRegistration event){TurretJadeClient.register(event);}
    private enum Data implements IServerDataProvider<BlockAccessor> {INSTANCE;
        @Override public ResourceLocation getUid(){return UID;}
        @Override public void appendServerData(CompoundTag data,BlockAccessor accessor){if(!(accessor.getBlockEntity() instanceof SentryEntity turret))return;var tag=new CompoundTag();tag.putBoolean("active",turret.active());tag.putBoolean("damaged",turret.damaged());tag.putInt("kills",turret.stattrak);int count=0;for(int i=1;i<10;i++){var stack=turret.slots.get(i);for(var ammo:turret.displayedAmmo())if(stack.is(ammo.getItem())&&stack.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),0).equals(ammo.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),0))){count+=stack.getCount();break;}}tag.putInt("ammo",count);if(turret.target!=null)tag.putString("target",Component.Serializer.toJson(turret.target.getDisplayName(),accessor.getLevel().registryAccess()));data.put(DATA,tag);}
    }
}

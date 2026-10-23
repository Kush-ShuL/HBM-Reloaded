package com.hbm.machine.massstorage;

import com.hbm.client.massstorage.MassStorageJadeClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.*;

/** Optional Jade data reads the same actual native stockpile/filter. */
@WailaPlugin
public final class MassStorageJadePlugin implements IWailaPlugin {
    public static final ResourceLocation UID=ResourceLocation.parse("hbm:mass_storage");
    public static final String DATA="hbm:mass_storage";
    @Override public void register(IWailaCommonRegistration event){event.registerBlockDataProvider(Data.INSTANCE,MassStorageBlockEntity.class);}
    @Override public void registerClient(IWailaClientRegistration event){MassStorageJadeClient.register(event);}
    private enum Data implements IServerDataProvider<BlockAccessor> {
        INSTANCE;
        @Override public ResourceLocation getUid(){return UID;}
        @Override public void appendServerData(CompoundTag root,BlockAccessor accessor){if(accessor.getBlockEntity() instanceof MassStorageBlockEntity storage){var data=new CompoundTag();data.putInt("stack",storage.getStockpile());data.putInt("capacity",storage.getCapacity());data.putBoolean("output",storage.output);if(!storage.getFilterType().isEmpty())data.put("filter",storage.getFilterType().save(accessor.getLevel().registryAccess()));root.put(DATA,data);}}
    }
}

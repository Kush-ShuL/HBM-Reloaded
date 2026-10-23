package com.hbm.geiger.compat;

import com.hbm.HbmReloaded;
import com.hbm.client.geiger.GeigerJadeClient;
import com.hbm.geiger.GeigerBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

/** Optional Jade discovery; the extra client provider is initialized only in Jade's client callback. */
@WailaPlugin
public final class GeigerJadePlugin implements IWailaPlugin {
    public static final ResourceLocation UID=HbmReloaded.id("geiger");
    public static final String DATA="hbm:geiger";
    @Override public void register(IWailaCommonRegistration registration) { registration.registerBlockDataProvider(Data.INSTANCE,GeigerBlockEntity.class); }
    @Override public void registerClient(IWailaClientRegistration registration) { GeigerJadeClient.register(registration); }
    private enum Data implements IServerDataProvider<BlockAccessor> {
        INSTANCE;
        @Override public ResourceLocation getUid() { return UID; }
        @Override public boolean shouldRequestData(BlockAccessor accessor) { return accessor.getBlockEntity() instanceof GeigerBlockEntity; }
        @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor) { if(accessor.getBlockEntity() instanceof GeigerBlockEntity geiger) tag.putInt(DATA,(int)Math.ceil(geiger.sample())); }
    }
}

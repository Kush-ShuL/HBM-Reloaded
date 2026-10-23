package com.hbm.block.pylon.compat;

import com.hbm.HbmReloaded;
import com.hbm.block.pylon.ConnectorBlock;
import com.hbm.block.pylon.PylonBlock;
import com.hbm.block.pylon.PylonBlockEntity;
import com.hbm.client.pylon.PylonJadeClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public final class PylonJadePlugin implements IWailaPlugin {
    public static final ResourceLocation UID=HbmReloaded.id("pylon");
    public static final String DATA="hbm:pylon_links";
    public PylonJadePlugin() {}
    @Override public void register(IWailaCommonRegistration registration) { registration.registerBlockDataProvider(Data.INSTANCE,PylonBlock.class);registration.registerBlockDataProvider(Data.INSTANCE,ConnectorBlock.class); }
    @Override public void registerClient(IWailaClientRegistration registration) { PylonJadeClient.register(registration); }
    private enum Data implements IServerDataProvider<BlockAccessor> {
        INSTANCE;
        @Override public ResourceLocation getUid() { return UID; }
        @Override public boolean shouldRequestData(BlockAccessor accessor) { return true; }
        @Override public void appendServerData(CompoundTag tag,BlockAccessor accessor) {
            var pos=accessor.getPosition();var level=accessor.getLevel();
            if(accessor.getBlockState().getBlock() instanceof PylonBlock block) { var core=block.findCore(level,pos);if(core==null) return;pos=core; }
            if(level.getBlockEntity(pos) instanceof PylonBlockEntity pylon) tag.putInt(DATA,pylon.connected().size());
        }
    }
}

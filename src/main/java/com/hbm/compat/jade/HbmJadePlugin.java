package com.hbm.compat.jade;

import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

/** Jade discovers this class only when its optional integration is installed. */
@WailaPlugin
public final class HbmJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(MachineDataProvider.INSTANCE, BlockEntity.class);
        registration.registerItemStorage(StorageCrateItemStorageProvider.INSTANCE, com.hbm.machine.storage.StorageCrateBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        HbmJadeClient.register(registration);
        registration.registerItemStorageClient(StorageCrateItemStorageProvider.INSTANCE);
    }
}

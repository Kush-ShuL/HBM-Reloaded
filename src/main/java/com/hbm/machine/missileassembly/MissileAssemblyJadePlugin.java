package com.hbm.machine.missileassembly;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.*;

/** Reports actual source constructability, without invented HE/fluid/progress state. */
@WailaPlugin
public final class MissileAssemblyJadePlugin implements IWailaPlugin {
    public static final ResourceLocation UID = MissileAssemblyContent.id("missile_assembly_state");
    public static final String DATA = "hbm:missile_assembly_state";
    @Override public void register(IWailaCommonRegistration event) { event.registerBlockDataProvider(Data.INSTANCE, BlockEntity.class); }
    @Override public void registerClient(IWailaClientRegistration event) { com.hbm.client.missileassembly.MissileAssemblyJadeClient.register(event); }
    private enum Data implements IServerDataProvider<BlockAccessor> {
        INSTANCE;
        @Override public ResourceLocation getUid() { return UID; }
        @Override public boolean shouldRequestData(BlockAccessor accessor) { return accessor.getBlockEntity() instanceof MissileAssemblyBlockEntity; }
        @Override public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
            if (accessor.getBlockEntity() instanceof MissileAssemblyBlockEntity machine) {
                var state = new CompoundTag(); state.putBoolean("ready", MissileAssemblyRules.canBuild(machine)); tag.put(DATA, state);
            }
        }
    }
}

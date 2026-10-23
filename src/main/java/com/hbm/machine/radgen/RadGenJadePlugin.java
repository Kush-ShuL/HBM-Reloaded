package com.hbm.machine.radgen;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

/** Original EnergyControl output entry; ordinary MachineView still owns HE storage and active queue progress. */
@WailaPlugin
public final class RadGenJadePlugin implements IWailaPlugin {
    private static final ResourceLocation UID = ResourceLocation.parse("hbm:radgen_output");
    private static final String DATA = "hbm_radgen_output";
    @Override public void register(IWailaCommonRegistration registration) { registration.registerBlockDataProvider(Provider.INSTANCE, BlockEntity.class); }
    @Override public void registerClient(IWailaClientRegistration registration) { registration.registerBlockComponent(Provider.INSTANCE, RadGenBlock.class); }
    private enum Provider implements IServerDataProvider<BlockAccessor>, IBlockComponentProvider {
        INSTANCE;
        @Override public ResourceLocation getUid() { return UID; }
        @Override public boolean shouldRequestData(BlockAccessor accessor) { return accessor.getBlockState().getBlock() instanceof RadGenBlock; }
        @Override public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
            if (!(accessor.getBlockState().getBlock() instanceof RadGenBlock block)) return;
            BlockPos core = block.findCore(accessor.getLevel(), accessor.getPosition());
            if (core == null || !(accessor.getLevel().getBlockEntity(core) instanceof RadGenEntity machine)) return;
            var value = new CompoundTag(); machine.provideExtraInfo(value); tag.put(DATA, value);
        }
        @Override public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            if (accessor.getServerData().contains(DATA)) tooltip.add(Component.translatable("jade.hbm.radgen.output", accessor.getServerData().getCompound(DATA).getInt("output")));
        }
    }
}

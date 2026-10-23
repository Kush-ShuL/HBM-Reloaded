package com.hbm.client.shredder;

import com.hbm.machine.shredder.ShredderBlock;
import com.hbm.machine.shredder.ShredderBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

/** Extra processing/blade data; the shared MachineView provider supplies authoritative HE. */
@WailaPlugin
public final class ShredderJadePlugin implements IWailaPlugin {
    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath("hbm", "shredder_status");
    @Override public void register(IWailaCommonRegistration event) { event.registerBlockDataProvider(Data.INSTANCE, ShredderBlockEntity.class); }
    @Override public void registerClient(IWailaClientRegistration event) { event.registerBlockComponent(Tooltip.INSTANCE, ShredderBlock.class); }
    private enum Data implements IServerDataProvider<BlockAccessor> {
        INSTANCE;
        @Override public ResourceLocation getUid() { return UID; }
        @Override public void appendServerData(CompoundTag data, BlockAccessor accessor) {
            if (accessor.getBlockEntity() instanceof ShredderBlockEntity machine) {
                var tag = new CompoundTag(); tag.putInt("progress", machine.progress()); tag.putInt("left", machine.gearLeft()); tag.putInt("right", machine.gearRight());
                data.put("hbm:shredder", tag);
            }
        }
    }
    private enum Tooltip implements IBlockComponentProvider {
        INSTANCE;
        @Override public ResourceLocation getUid() { return UID; }
        @Override public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            if (!accessor.getServerData().contains("hbm:shredder")) return;
            var tag = accessor.getServerData().getCompound("hbm:shredder");
            if (tag.getInt("progress") > 0) com.hbm.compat.jade.HbmJadeBars.add(tooltip, tag.getInt("progress"), 60, Component.translatableWithFallback("jade.hbm.shredder.progress", "Processing: %s / 60 ticks", tag.getInt("progress")), 0x71C78B);
            tooltip.add(Component.translatableWithFallback("jade.hbm.shredder.blades", "Blades: %s / %s", stage(tag.getInt("left")), stage(tag.getInt("right"))));
        }
        private static Component stage(int value) { return switch (value) {
            case 1 -> Component.translatableWithFallback("jade.hbm.shredder.blade.ready", "Ready");
            case 2 -> Component.translatableWithFallback("jade.hbm.shredder.blade.worn", "Worn");
            case 3 -> Component.translatableWithFallback("jade.hbm.shredder.blade.broken", "Broken");
            default -> Component.translatableWithFallback("jade.hbm.shredder.blade.missing", "Missing");
        }; }
    }
}

package com.hbm.compat.jade;

import com.hbm.HbmReloaded;
import com.hbm.api.machine.MachineView;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

/** Machine values come from the server; clients never infer authoritative storage values. */
public enum MachineDataProvider implements IServerDataProvider<BlockAccessor> {
    INSTANCE;

    static final String DATA_KEY = "hbm:machine";
    static final ResourceLocation UID = HbmReloaded.id("machine");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public boolean shouldRequestData(BlockAccessor accessor) {
        return accessor.getBlockEntity() instanceof MachineView || accessor.getBlockEntity() instanceof com.hbm.block.pipe.NativePipeBlockEntity;
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof com.hbm.block.pipe.NativePipeBlockEntity pipe) {
            CompoundTag state = new CompoundTag();
            ListTag fluids = new ListTag();
            for (var type : pipe.networkTypes()) {
                var fluid = new CompoundTag(); fluid.putString("name", type.getConditionalName());
                fluid.putBoolean("literal", type.hasLocalizedOverride()); fluid.putInt("color", pipe.kind().exhaust() ? 0xFFFFFF : type.getColor()); fluids.add(fluid);
            }
            state.put("pipeTypes", fluids);
            if (pipe instanceof com.hbm.block.pipe.InstrumentPipeBlockEntity instrument) {
                if (pipe.kind() == com.hbm.block.pipe.PipeKind.GAUGE) { state.putLong("pipeTick", instrument.deltaTick()); state.putLong("pipeSecond", instrument.deltaLastSecond()); }
                else state.putLong("pipeCounter", instrument.counter());
            }
            data.put(DATA_KEY, state);
            return;
        }
        if (!(accessor.getBlockEntity() instanceof MachineView machine)) {
            return;
        }
        CompoundTag state = new CompoundTag();
        state.putString("energyExact", machine.energyStoredExact().toString());
        state.putString("energyCapacityExact", machine.energyCapacityExact().toString());
        ListTag tanks = new ListTag();
        for (MachineView.TankView tank : machine.tanks()) {
            CompoundTag entry = new CompoundTag();
            entry.putString("name", tank.name());
            entry.putBoolean("literal", tank.literal());
            entry.putLong("amount", tank.amount());
            entry.putLong("capacity", tank.capacity());
            entry.putInt("color", tank.color());
            tanks.add(entry);
        }
        state.put("tanks", tanks);
        ListTag progress = new ListTag();
        for (var view : machine.progressBars()) {
            if (!Double.isFinite(view.value()) || !Double.isFinite(view.maximum()) || view.maximum() <= 0) continue;
            var entry = new CompoundTag();
            entry.putString("name", view.name()); entry.putDouble("value", view.value());
            entry.putDouble("maximum", view.maximum()); entry.putInt("color", view.color());
            progress.add(entry);
        }
        state.put("progress", progress);
        data.put(DATA_KEY, state);
    }
}

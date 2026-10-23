package com.hbm.compat.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.config.IPluginConfig;

/** Loaded through Jade's client callback, keeping overlay code outside common initialization. */
final class HbmJadeClient {
    private HbmJadeClient() {}

    static void register(IWailaClientRegistration registration) {
        registration.registerBlockComponent(MachineComponent.INSTANCE, Block.class);
    }

    private enum MachineComponent implements IBlockComponentProvider {
        INSTANCE;

        @Override
        public ResourceLocation getUid() {
            return MachineDataProvider.UID;
        }

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            CompoundTag data = accessor.getServerData();
            if (!data.contains(MachineDataProvider.DATA_KEY, Tag.TAG_COMPOUND)) {
                return;
            }
            CompoundTag machine = data.getCompound(MachineDataProvider.DATA_KEY);
            if (machine.contains("pipeType")) {
                Component name = machine.getBoolean("pipeLiteral") ? Component.literal(machine.getString("pipeType")) : Component.translatable(machine.getString("pipeType"));
                tooltip.add(name.copy().withStyle(style -> style.withColor(machine.getInt("pipeColor"))));
            }
            ListTag fluids = machine.getList("pipeTypes", Tag.TAG_COMPOUND);
            for (int i = 0; i < fluids.size(); i++) {
                var fluid = fluids.getCompound(i);
                Component name = fluid.getBoolean("literal") ? Component.literal(fluid.getString("name")) : Component.translatable(fluid.getString("name"));
                tooltip.add(name.copy().withStyle(style -> style.withColor(fluid.getInt("color"))));
            }
            if (machine.contains("pipeTick")) { tooltip.add(Component.literal(String.format(java.util.Locale.US, "%,d mB/t", machine.getLong("pipeTick")))); tooltip.add(Component.literal(String.format(java.util.Locale.US, "%,d mB/s", machine.getLong("pipeSecond")))); }
            if (machine.contains("pipeCounter")) tooltip.add(Component.translatable("jade.hbm.pipe_counter", machine.getLong("pipeCounter")));
            String energy = machine.contains("energyExact") ? machine.getString("energyExact") : Long.toString(machine.getLong("energy"));
            String capacity = machine.contains("energyCapacityExact") ? machine.getString("energyCapacityExact") : Long.toString(machine.getLong("energyCapacity"));
            if (new java.math.BigInteger(capacity).signum() > 0) {
                HbmJadeBars.add(tooltip, new java.math.BigInteger(energy), new java.math.BigInteger(capacity),
                        Component.translatableWithFallback("jade.hbm.energy", "Energy: %s / %s HE", energy, capacity), 0xF2C94C);
            }
            ListTag tanks = machine.getList("tanks", Tag.TAG_COMPOUND);
            for (int i = 0; i < tanks.size(); i++) {
                CompoundTag tank = tanks.getCompound(i);
                HbmJadeBars.add(tooltip, tank.getLong("amount"), tank.getLong("capacity"),
                        Component.translatableWithFallback("jade.hbm.tank", "%s: %s / %s mB",
                        tank.getBoolean("literal") ? Component.literal(tank.getString("name")) : Component.translatable(tank.getString("name")), tank.getLong("amount"), tank.getLong("capacity")), tank.getInt("color"));
            }
            ListTag progress = machine.getList("progress", Tag.TAG_COMPOUND);
            for (int i = 0; i < progress.size(); i++) {
                var view = progress.getCompound(i);
                double value = view.getDouble("value"), maximum = view.getDouble("maximum");
                String percent = String.format(java.util.Locale.ROOT, "%.0f%%", Math.clamp(value / maximum, 0D, 1D) * 100);
                HbmJadeBars.add(tooltip, value, maximum, Component.translatable("jade.hbm.progress_bar", Component.translatable(view.getString("name")), percent), view.getInt("color"));
            }
        }
    }
}

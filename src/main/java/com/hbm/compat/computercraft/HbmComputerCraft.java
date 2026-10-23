package com.hbm.compat.computercraft;

import com.hbm.api.computer.ComputerArguments;
import com.hbm.api.computer.ComputerControl;
import com.hbm.api.computer.ComputerException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/** Optional integration: CC classes are resolved only when ComputerCraft is present. */
@EventBusSubscriber(modid = "hbm", bus = EventBusSubscriber.Bus.MOD)
public final class HbmComputerCraft {
    private HbmComputerCraft() {}

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if (ModList.get().isLoaded("computercraft")) Loaded.register(event);
    }

    private static final class Loaded {
        private static void register(RegisterCapabilitiesEvent event) {
            BuiltInRegistries.BLOCK_ENTITY_TYPE.entrySet().forEach(entry -> {
                if (!entry.getKey().location().getNamespace().equals("hbm")) return;
                event.registerBlockEntity(dan200.computercraft.api.peripheral.PeripheralCapability.get(), entry.getValue(),
                        (machine, side) -> machine instanceof ComputerControl control && control.canComputerConnect(side)
                                ? new Peripheral(machine, control) : null);
            });
        }
    }

    private static final class Peripheral implements dan200.computercraft.api.peripheral.IDynamicPeripheral {
        private final BlockEntity machine;
        private final ComputerControl control;
        private final String[] methods;

        private Peripheral(BlockEntity machine, ComputerControl control) {
            this.machine = machine;
            this.control = control;
            methods = control.computerMethods().clone();
        }

        @Override
        public String getType() {
            return control.computerType();
        }

        @Override
        public String[] getMethodNames() {
            return methods.clone();
        }

        @Override
        public Object getTarget() {
            return machine;
        }

        @Override
        public boolean equals(dan200.computercraft.api.peripheral.IPeripheral other) {
            return other instanceof Peripheral peripheral && peripheral.machine == machine;
        }

        @Override
        public dan200.computercraft.api.lua.MethodResult callMethod(
                dan200.computercraft.api.peripheral.IComputerAccess computer,
                dan200.computercraft.api.lua.ILuaContext context, int method,
                dan200.computercraft.api.lua.IArguments arguments) throws dan200.computercraft.api.lua.LuaException {
            if (method < 0 || method >= methods.length) throw new dan200.computercraft.api.lua.LuaException("No such method");
            var snapshot = new ComputerArguments(arguments.getAll());
            return context.executeMainThreadTask(() -> {
                if (machine.isRemoved()) throw new dan200.computercraft.api.lua.LuaException("Machine is no longer available");
                try {
                    return control.callComputer(methods[method], snapshot);
                } catch (ComputerException error) {
                    throw new dan200.computercraft.api.lua.LuaException(error.getMessage());
                }
            });
        }
    }
}

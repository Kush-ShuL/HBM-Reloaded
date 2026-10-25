package com.hbm.compat.ae2;

import com.hbm.hazard.HazardContainerRadiation;
import com.hbm.hazard.HazardStateUnavailable;
import com.hbm.machine.arcfurnace.ArcFurnaceBlockEntity;
import com.hbm.machine.arcfurnace.ArcFurnaceContent;
import com.hbm.machine.arcfurnace.ArcFurnacePort;
import com.hbm.machine.massstorage.MassStorageBlockEntity;
import com.hbm.machine.massstorage.MassStorageContent;
import com.hbm.multiblock.proxy.SourceProxyBindings;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/** Optional AE2 1.21.1 ME-storage bridge; no AE classes are resolved without the installed mod. */
@EventBusSubscriber(modid = "hbm", bus = EventBusSubscriber.Bus.MOD)
public final class HbmAppliedEnergistics {
    @SubscribeEvent public static void capabilities(RegisterCapabilitiesEvent event) {
        if (!ModList.get().isLoaded("ae2")) return;
        var api = Loaded.API;
        event.registerBlockEntity(api.capability, MassStorageContent.ENTITY.get(), (machine, side) -> api.storage(machine));
        event.registerBlockEntity(api.capability, ArcFurnaceContent.ENTITY.get(), (machine, side) -> api.storage(machine));
        event.registerBlockEntity(api.capability, ArcFurnaceContent.PORT.get(), (machine, side) -> api.storage(machine));
        HazardContainerRadiation.addExternalInventory(api::cellContents);
    }

    private static final class Loaded {
        static final Api API = new Api();
    }

    /** Only this optional boundary uses reflection; machine accounting and native component matching remain typed. */
    private static final class Api {
        final Class<?> storageType, itemKeyType, counterType;
        final BlockCapability<Object, Direction> capability;
        final Method keyOf, toStack, counterAdd, available, cellInventory;

        @SuppressWarnings("unchecked") Api() {
            try {
                storageType = Class.forName("appeng.api.storage.MEStorage");
                itemKeyType = Class.forName("appeng.api.stacks.AEItemKey");
                counterType = Class.forName("appeng.api.stacks.KeyCounter");
                var keyType = Class.forName("appeng.api.stacks.AEKey");
                capability = (BlockCapability<Object, Direction>) Class.forName("appeng.api.AECapabilities").getField("ME_STORAGE").get(null);
                keyOf = itemKeyType.getMethod("of", ItemStack.class);
                toStack = itemKeyType.getMethod("toStack", int.class);
                counterAdd = counterType.getMethod("add", keyType, long.class);
                available = storageType.getMethod("getAvailableStacks");
                cellInventory = Class.forName("appeng.api.storage.StorageCells").getMethod("getCellInventory", ItemStack.class,
                        Class.forName("appeng.api.storage.cells.ISaveProvider"));
            } catch (ReflectiveOperationException failure) {
                throw new IllegalStateException("Installed AE2 does not provide the 1.21.1 ME storage API", failure);
            }
        }

        Object storage(BlockEntity host) {
            return Proxy.newProxyInstance(storageType.getClassLoader(), new Class<?>[]{storageType}, (proxy, method, args) -> {
                if (method.getDeclaringClass() == Object.class) return switch (method.getName()) {
                    case "equals" -> proxy == args[0];
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "toString" -> "HBM ME storage at " + host.getBlockPos();
                    default -> throw new UnsupportedOperationException(method.toString());
                };
                var core = core(host);
                return switch (method.getName()) {
                    case "getDescription" -> host.getBlockState().getBlock().getName();
                    case "isPreferredStorageFor" -> core instanceof MassStorageBlockEntity storage
                            && matches(args[0], storage.getFilterType());
                    case "insert" -> transfer(core, args, true);
                    case "extract" -> transfer(core, args, false);
                    case "getAvailableStacks" -> {
                        Object counter = args == null || args.length == 0 ? counterType.getConstructor().newInstance() : args[0];
                        if (core instanceof MassStorageBlockEntity storage) add(counter, storage.getFilterType(), storage.getTotalStockpile());
                        else if (core instanceof ArcFurnaceBlockEntity furnace)
                            for (int slot = 0; slot < 25; slot++) {
                                var stack = furnace.getAvailableItemFromSlot(slot);
                                add(counter, stack, stack.getCount());
                            }
                        yield args == null || args.length == 0 ? counter : null;
                    }
                    default -> throw new UnsupportedOperationException("Unsupported AE2 storage operation " + method);
                };
            });
        }

        private BlockEntity core(BlockEntity host) {
            if (host.isRemoved()) return null;
            return host instanceof ArcFurnacePort ? SourceProxyBindings.originalCore(host) : host;
        }

        private boolean matches(Object key, ItemStack stack) throws ReflectiveOperationException {
            return !stack.isEmpty() && itemKeyType.isInstance(key)
                    && ItemStack.isSameItemSameComponents((ItemStack) toStack.invoke(key, 1), stack);
        }

        private long transfer(BlockEntity core, Object[] args, boolean insert) throws ReflectiveOperationException {
            long requested = (long) args[1];
            if (requested <= 0 || !itemKeyType.isInstance(args[0])) return 0L;
            // Source machines use signed-int stockpiles and stack quantities. A larger ME request can be partially fulfilled.
            int amount = (int) Math.min(requested, Integer.MAX_VALUE);
            boolean modulate = ((Enum<?>) args[2]).name().equals("MODULATE");
            var stack = (ItemStack) toStack.invoke(args[0], amount);
            if (core instanceof MassStorageBlockEntity storage) {
                if (!matches(args[0], storage.getFilterType())) return 0L;
                int remainder = insert ? storage.increaseTotalStockpile(amount, modulate) : storage.decreaseTotalStockpile(amount, modulate);
                return (long) amount - remainder;
            }
            if (core instanceof ArcFurnaceBlockEntity furnace) {
                if (insert) return (long) amount - furnace.distributeInput(stack, modulate).getCount();
                return (long) furnace.collectRequested(stack, modulate).getCount();
            }
            return 0L;
        }

        private void add(Object counter, ItemStack stack, long amount) throws ReflectiveOperationException {
            if (!stack.isEmpty() && amount > 0) counterAdd.invoke(counter, keyOf.invoke(null, stack), amount);
        }

        Optional<List<HazardContainerRadiation.StoredStack>> cellContents(ItemStack stack) {
            if (stack.isEmpty() || !BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace().equals("ae2")) return Optional.empty();
            try {
                // Reading a copy with a null save provider never mutates the player's cell or consumes its power.
                var inventory = cellInventory.invoke(null, stack.copy(), null);
                if (inventory == null) return Optional.empty();
                var stacks = new ArrayList<HazardContainerRadiation.StoredStack>();
                for (var raw : (Iterable<?>) available.invoke(inventory)) {
                    var entry = (it.unimi.dsi.fastutil.objects.Object2LongMap.Entry<?>) raw;
                    if (itemKeyType.isInstance(entry.getKey()) && entry.getLongValue() > 0)
                        stacks.add(new HazardContainerRadiation.StoredStack((ItemStack) toStack.invoke(entry.getKey(), 1), entry.getLongValue()));
                }
                return Optional.of(List.copyOf(stacks));
            } catch (ReflectiveOperationException failure) {
                throw new HazardStateUnavailable("Cannot read AE2 cell radiation: " + failure.getMessage());
            }
        }
    }

    private HbmAppliedEnergistics() {}
}

package com.hbm.multiblock;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Shared inventory-only TileEntityProxyCombo port, not an invented all-capability proxy. */
public final class Multiblocks {
    private static final DeferredRegister<BlockEntityType<?>> TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "hbm");
    private static final List<Supplier<? extends Block>> PORT_BLOCKS = new ArrayList<>();
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DummyInventoryProxy>> INVENTORY_PROXY = TYPES.register("dummy_inventory_proxy", () -> BlockEntityType.Builder.of(DummyInventoryProxy::new, PORT_BLOCKS.stream().map(Supplier::get).toArray(Block[]::new)).build(null));
    private static boolean registered;
    private Multiblocks() {}
    public static void addInventoryProxyBlock(Supplier<? extends Block> block) { PORT_BLOCKS.add(block); }
    public static void register(IEventBus bus) { if (registered) return; registered = true; TYPES.register(bus); bus.addListener(Multiblocks::capabilities); }
    private static void capabilities(RegisterCapabilitiesEvent event) { event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, INVENTORY_PROXY.get(), (proxy, side) -> side == null ? new InvWrapper(proxy) : new SidedInvWrapper(proxy, side)); }
}

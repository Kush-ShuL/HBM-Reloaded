package com.hbm.machine.radgen;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

public final class RadGenContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "hbm");
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, "hbm");
    public static final DeferredBlock<RadGenBlock> BLOCK = BLOCKS.register("machine_radgen", RadGenBlock::new);
    public static final DeferredItem<BlockItem> ITEM = ITEMS.register("machine_radgen", () -> new BlockItem(BLOCK.get(), new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RadGenEntity>> ENTITY = ENTITIES.register("radgen", () -> BlockEntityType.Builder.of(RadGenEntity::new, BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RadGenProxy>> PROXY = ENTITIES.register("radgen_proxy", () -> BlockEntityType.Builder.of(RadGenProxy::new, BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>, MenuType<RadGenMenu>> MENU = MENUS.register("radgen", () -> new MenuType<>((IContainerFactory<RadGenMenu>) RadGenMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); MENUS.register(bus); bus.addListener(RadGenContent::capabilities); }
    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ENTITY.get(), (machine, side) -> side == null ? new InvWrapper(machine) : new SidedInvWrapper(machine, side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, PROXY.get(), (machine, side) -> side == null ? new InvWrapper(machine) : new SidedInvWrapper(machine, side));
    }
    public static List<ItemStack> creativeStacks(String tab) { return tab.equals("machine") ? List.of(new ItemStack(ITEM.get())) : List.of(); }
    private RadGenContent() {}
}

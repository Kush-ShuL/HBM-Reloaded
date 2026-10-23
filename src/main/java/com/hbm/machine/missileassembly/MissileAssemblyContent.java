package com.hbm.machine.missileassembly;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

public final class MissileAssemblyContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "hbm");
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, "hbm");
    public static final DeferredBlock<MissileAssemblyBlock> BLOCK = BLOCKS.register("machine_missile_assembly", MissileAssemblyBlock::new);
    public static final DeferredItem<BlockItem> ITEM = ITEMS.registerSimpleBlockItem("machine_missile_assembly", BLOCK);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MissileAssemblyBlockEntity>> ENTITY = ENTITIES.register("machine_missile_assembly", () -> BlockEntityType.Builder.of(MissileAssemblyBlockEntity::new, BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>, MenuType<MissileAssemblyMenu>> MENU = MENUS.register("machine_missile_assembly", () -> new MenuType<>((IContainerFactory<MissileAssemblyMenu>) MissileAssemblyMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("hbm", path); }
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); MENUS.register(bus); }
    public static List<ItemStack> creativeStacks(String tab) { return "missile".equals(tab) ? List.of(new ItemStack(ITEM.get())) : List.of(); }
    private MissileAssemblyContent() {}
}

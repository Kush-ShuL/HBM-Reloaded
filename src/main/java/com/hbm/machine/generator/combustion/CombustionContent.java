package com.hbm.machine.generator.combustion;

import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

public final class CombustionContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<CombustionBlock> BLOCK=BLOCKS.register("machine_combustion_engine",CombustionBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
    public static final DeferredItem<PistonSetItem> PISTONS=ITEMS.register("piston_set",PistonSetItem::new);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CombustionBlockEntity>> ENTITY=ENTITIES.register("combustion_engine",()->BlockEntityType.Builder.of(CombustionBlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CombustionPort>> PORT=ENTITIES.register("combustion_engine_port",()->BlockEntityType.Builder.of(CombustionPort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<CombustionMenu>> MENU=MENUS.register("combustion_engine",()->new MenuType<>((IContainerFactory<CombustionMenu>)CombustionMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(CombustionControl::register);}
    public static List<ItemStack> pistonStacks(){var result=new ArrayList<ItemStack>();for(int i=0;i<4;i++){var stack=new ItemStack(PISTONS.get());stack.set(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),i);result.add(stack);}return result;}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):tab.equals("control")?pistonStacks():List.of();}
    private CombustionContent(){}
}

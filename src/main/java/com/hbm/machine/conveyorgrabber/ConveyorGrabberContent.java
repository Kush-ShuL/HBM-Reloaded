package com.hbm.machine.conveyorgrabber;

import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

public final class ConveyorGrabberContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<ConveyorGrabberBlock> BLOCK=BLOCKS.register("crane_grabber",ConveyorGrabberBlock::new);
    public static final DeferredItem<ConveyorGrabberItem> ITEM=ITEMS.register("crane_grabber",()->new ConveyorGrabberItem(BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ConveyorGrabberEntity>> ENTITY=ENTITIES.register("crane_grabber",()->BlockEntityType.Builder.of(ConveyorGrabberEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<ConveyorGrabberMenu>> MENU=MENUS.register("crane_grabber",()->net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create((IContainerFactory<ConveyorGrabberMenu>)ConveyorGrabberMenu::new));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(ConveyorGrabberControl::register);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    public static Map<String,Item> indexedItems(){return Map.of("crane_grabber",ITEM.get());}
    private ConveyorGrabberContent(){}
}

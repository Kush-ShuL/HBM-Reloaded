package com.hbm.machine.fensu;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

public final class FensuContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<FensuBlock> BLOCK=BLOCKS.register("machine_battery_redd",FensuBlock::new);
    public static final DeferredItem<FensuItem> ITEM=ITEMS.register("machine_battery_redd",()->new FensuItem(BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FensuBlockEntity>> ENTITY=ENTITIES.register("machine_battery_redd",()->BlockEntityType.Builder.of(FensuBlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FensuPort>> PORT=ENTITIES.register("battery_redd_port",()->BlockEntityType.Builder.of(FensuPort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<FensuMenu>> MENU=MENUS.register("machine_battery_redd",()->new MenuType<>((IContainerFactory<FensuMenu>)FensuMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(FensuPackets::register);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    private FensuContent(){}
}

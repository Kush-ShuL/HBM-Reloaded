package com.hbm.machine.oilflare;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

/** The actual X5846 machine_flare identity; no generic placeholder or alternate fuel registry. */
public final class FlareContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> BES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<FlareBlock> BLOCK=BLOCKS.register("machine_flare",FlareBlock::new);
    public static final DeferredItem<FlareItem> ITEM=ITEMS.register("machine_flare",()->new FlareItem(BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FlareEntity>> ENTITY=BES.register("machine_flare",()->BlockEntityType.Builder.of(FlareEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FlarePort>> PORT=BES.register("gas_flare_port",()->BlockEntityType.Builder.of(FlarePort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<FlareMenu>> MENU=MENUS.register("machine_flare",()->new MenuType<>((IContainerFactory<FlareMenu>)FlareMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);BES.register(bus);MENUS.register(bus);bus.addListener(FlareControl::register);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    private FlareContent(){}
}

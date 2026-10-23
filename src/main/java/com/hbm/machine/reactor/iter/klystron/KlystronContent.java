package com.hbm.machine.reactor.iter.klystron;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

public final class KlystronContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<KlystronBlock> BLOCK=BLOCKS.register("fusion_klystron",KlystronBlock::new);public static final DeferredItem<KlystronItem> ITEM=ITEMS.register("fusion_klystron",KlystronItem::new);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<KlystronEntity>> ENTITY=ENTITIES.register("fusion_klystron",()->BlockEntityType.Builder.of(KlystronEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<KlystronPort>> PORT=ENTITIES.register("fusion_klystron_port",()->BlockEntityType.Builder.of(KlystronPort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<KlystronMenu>> MENU=MENUS.register("fusion_klystron",()->new MenuType<>((IContainerFactory<KlystronMenu>)KlystronMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(KlystronControl::register);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    private KlystronContent(){}
}

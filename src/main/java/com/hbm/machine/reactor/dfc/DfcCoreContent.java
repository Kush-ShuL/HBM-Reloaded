package com.hbm.machine.reactor.dfc;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.*;
/** Only the actual DFC core registration; its four components and twenty source materials are separate real modules. */
public final class DfcCoreContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<DfcCoreBlock> BLOCK=BLOCKS.register("dfc_core",DfcCoreBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.register("dfc_core",()->new BlockItem(BLOCK.get(),new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<DfcCoreEntity>> ENTITY=ENTITIES.register("dfc_core",()->BlockEntityType.Builder.of(DfcCoreEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<DfcCoreMenu>> MENU=MENUS.register("dfc_core",()->IMenuTypeExtension.create(DfcCoreMenu::new));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    private DfcCoreContent(){}
}

package com.hbm.block.devtesters;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
public final class DevTestersContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<EventTesterBlock> EVENT=BLOCKS.register("event_tester",EventTesterBlock::new);
    public static final DeferredBlock<ObjTesterBlock> OBJ=BLOCKS.register("obj_tester",ObjTesterBlock::new);
    public static final DeferredItem<BlockItem> EVENT_ITEM=ITEMS.registerSimpleBlockItem("event_tester",EVENT);
    public static final DeferredItem<BlockItem> OBJ_ITEM=ITEMS.registerSimpleBlockItem("obj_tester",OBJ);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<EventTesterEntity>> STORAGE_ENTITY=ENTITIES.register("event_tester",()->BlockEntityType.Builder.of(EventTesterEntity::new,EVENT.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ObjTesterEntity>> OBJ_ENTITY=ENTITIES.register("obj_tester",()->BlockEntityType.Builder.of(ObjTesterEntity::new,OBJ.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<EventTesterMenu>> MENU=MENUS.register("event_tester",()->new MenuType<>((IContainerFactory<EventTesterMenu>)EventTesterMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return List.of();}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(EVENT_ITEM.get()),new ItemStack(OBJ_ITEM.get()));}
    private DevTestersContent(){}
}

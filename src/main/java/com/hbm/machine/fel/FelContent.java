package com.hbm.machine.fel;
import java.util.List;import net.minecraft.core.registries.Registries;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.flag.FeatureFlags;import net.minecraft.world.inventory.MenuType;import net.minecraft.world.item.*;import net.minecraft.world.level.block.entity.BlockEntityType;import net.neoforged.bus.api.IEventBus;import net.neoforged.neoforge.network.IContainerFactory;import net.neoforged.neoforge.registries.*;
/** No invented recipe type, fluid capability, or computer API: the source has none. */
public final class FelContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
 public static final DeferredBlock<FelBlock> BLOCK=BLOCKS.register("machine_fel",FelBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FelBlockEntity>> ENTITY=ENTITIES.register("fel",()->BlockEntityType.Builder.of(FelBlockEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FelPort>> PORT=ENTITIES.register("fel_port",()->BlockEntityType.Builder.of(FelPort::new,BLOCK.get()).build(null));
 public static final DeferredHolder<MenuType<?>,MenuType<FelMenu>> MENU=MENUS.register("fel",()->new MenuType<>((IContainerFactory<FelMenu>)FelMenu::new,FeatureFlags.DEFAULT_FLAGS));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(FelControl::register);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
 public static ResourceLocation id(String name){return ResourceLocation.fromNamespaceAndPath("hbm",name);}private FelContent(){}
}

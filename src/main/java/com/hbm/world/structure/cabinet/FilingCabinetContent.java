package com.hbm.world.structure.cabinet;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
/** The real source BlockDecoContainer and eight-slot, lockable, non-automatable cabinet. */
public final class FilingCabinetContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
 public static final DeferredBlock<FilingCabinetBlock> BLOCK=BLOCKS.register("filing_cabinet",FilingCabinetBlock::new);
 public static final DeferredItem<FilingCabinetItem> ITEM=ITEMS.register("filing_cabinet",()->new FilingCabinetItem(BLOCK.get()));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FilingCabinetBlockEntity>> ENTITY=ENTITIES.register("file_cabinet",()->BlockEntityType.Builder.of(FilingCabinetBlockEntity::new,BLOCK.get()).build(null));
 public static final DeferredHolder<MenuType<?>,MenuType<FilingCabinetMenu>> MENU=MENUS.register("file_cabinet",()->new MenuType<>((IContainerFactory<FilingCabinetMenu>)FilingCabinetMenu::new,FeatureFlags.DEFAULT_FLAGS));
 public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(CabinetAnimationPacket::register);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(FilingCabinetItem.stack(0),FilingCabinetItem.stack(1)):List.of();}
 private FilingCabinetContent(){}
}

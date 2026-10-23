package com.hbm.machine.annihilator;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
public final class AnnihilatorContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> BES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
 public static final DeferredBlock<AnnihilatorBlock> BLOCK=BLOCKS.register("machine_annihilator",AnnihilatorBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem("machine_annihilator",BLOCK);
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<AnnihilatorEntity>> ENTITY=BES.register("machine_annihilator",()->BlockEntityType.Builder.of(AnnihilatorEntity::new,BLOCK.get()).build(null));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<AnnihilatorPort>> PORT=BES.register("annihilator_port",()->BlockEntityType.Builder.of(AnnihilatorPort::new,BLOCK.get()).build(null));
 public static final DeferredHolder<MenuType<?>,MenuType<AnnihilatorMenu>> MENU=MENUS.register("machine_annihilator",()->new MenuType<>((IContainerFactory<AnnihilatorMenu>)AnnihilatorMenu::new,FeatureFlags.DEFAULT_FLAGS));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);BES.register(bus);MENUS.register(bus);AnnihilatorPackets.register(bus);AnnihilatorRecipes.register(bus);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
}

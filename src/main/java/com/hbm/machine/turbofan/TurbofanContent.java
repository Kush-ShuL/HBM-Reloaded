package com.hbm.machine.turbofan;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
public final class TurbofanContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
 public static final DeferredBlock<TurbofanBlock> BLOCK=BLOCKS.register("machine_turbofan",TurbofanBlock::new);public static final DeferredItem<TurbofanItem> ITEM=ITEMS.register("machine_turbofan",()->new TurbofanItem(BLOCK.get()));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<TurbofanEntity>> ENTITY=ENTITIES.register("turbofan",()->BlockEntityType.Builder.of(TurbofanEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<TurbofanPort>> PORT=ENTITIES.register("turbofan_port",()->BlockEntityType.Builder.of(TurbofanPort::new,BLOCK.get()).build(null));public static final DeferredHolder<MenuType<?>,MenuType<TurbofanMenu>> MENU=MENUS.register("turbofan",()->new MenuType<>((IContainerFactory<TurbofanMenu>)TurbofanMenu::new,FeatureFlags.DEFAULT_FLAGS));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener((RegisterCapabilitiesEvent e)->e.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side)));}public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}private TurbofanContent(){}
}

package com.hbm.machine.ashpit;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
public final class AshpitContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
 public static final DeferredBlock<AshpitBlock> BLOCK=BLOCKS.register("machine_ashpit",AshpitBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem("machine_ashpit",BLOCK);public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<AshpitBlockEntity>> ENTITY=ENTITIES.register("machine_ashpit",()->BlockEntityType.Builder.of(AshpitBlockEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<MenuType<?>,MenuType<AshpitMenu>> MENU=MENUS.register("machine_ashpit",()->new MenuType<>((IContainerFactory<AshpitMenu>)AshpitMenu::new,FeatureFlags.DEFAULT_FLAGS));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<AshpitProxy>> PROXY=ENTITIES.register("ashpit_proxy",()->BlockEntityType.Builder.of(AshpitProxy::new,BLOCK.get()).build(null));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener((RegisterCapabilitiesEvent event)->{event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(be,side)->new SidedInvWrapper(be,side==null?net.minecraft.core.Direction.UP:side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PROXY.get(),(be,side)->new SidedInvWrapper(be,side==null?net.minecraft.core.Direction.UP:side));});}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}private AshpitContent(){}
}

package com.hbm.machine.reactor.dfc.components;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.*;
public final class DfcComponentsContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
 private static final Map<DfcComponentKind,DeferredBlock<DfcComponentBlock>> BLOCK_MAP=new EnumMap<>(DfcComponentKind.class);private static final Map<DfcComponentKind,DeferredItem<BlockItem>> ITEM_MAP=new EnumMap<>(DfcComponentKind.class);private static final Map<DfcComponentKind,DeferredHolder<BlockEntityType<?>,BlockEntityType<? extends DfcComponentEntity>>> ENTITY_MAP=new EnumMap<>(DfcComponentKind.class);private static final Map<DfcComponentKind,DeferredHolder<MenuType<?>,MenuType<DfcComponentMenu>>> MENU_MAP=new EnumMap<>(DfcComponentKind.class);
 static {for(var kind:DfcComponentKind.values()){var block=BLOCKS.register(kind.id,()->new DfcComponentBlock(kind));BLOCK_MAP.put(kind,block);ITEM_MAP.put(kind,ITEMS.register(kind.id,()->new BlockItem(block.get(),new Item.Properties())));ENTITY_MAP.put(kind,ENTITIES.register(kind.id,()->BlockEntityType.Builder.of((pos,state)->switch(kind){case EMITTER->new DfcEmitterEntity(pos,state);case INJECTOR->new DfcInjectorEntity(pos,state);case RECEIVER->new DfcReceiverEntity(pos,state);case STABILIZER->new DfcStabilizerEntity(pos,state);},block.get()).build(null)));MENU_MAP.put(kind,MENUS.register(kind.id,()->IMenuTypeExtension.create((id,inventory,data)->new DfcComponentMenu(kind,id,inventory,data.readBlockPos()))));}}
 public static DfcComponentBlock block(DfcComponentKind kind){return BLOCK_MAP.get(kind).get();}public static Item item(DfcComponentKind kind){return ITEM_MAP.get(kind).get();}public static BlockEntityType<? extends DfcComponentEntity> entity(DfcComponentKind kind){return ENTITY_MAP.get(kind).get();}public static MenuType<DfcComponentMenu> menu(DfcComponentKind kind){return MENU_MAP.get(kind).get();}
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(DfcComponentControl::register);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?Arrays.stream(DfcComponentKind.values()).map(kind->new ItemStack(item(kind))).toList():List.of();}
 private DfcComponentsContent(){}
}

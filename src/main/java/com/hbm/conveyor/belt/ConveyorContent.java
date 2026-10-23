package com.hbm.conveyor.belt;

import java.util.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Six genuine source blocks, hidden individually, and the four real stackable construction variants. */
public final class ConveyorContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredBlock<ConveyorBeltBlock> REGULAR=block("conveyor",ConveyorBeltBlock.Kind.REGULAR),EXPRESS=block("conveyor_express",ConveyorBeltBlock.Kind.EXPRESS),DOUBLE=block("conveyor_double",ConveyorBeltBlock.Kind.DOUBLE),TRIPLE=block("conveyor_triple",ConveyorBeltBlock.Kind.TRIPLE),LIFT=block("conveyor_lift",ConveyorBeltBlock.Kind.LIFT),CHUTE=block("conveyor_chute",ConveyorBeltBlock.Kind.CHUTE);
 public static final DeferredItem<ConveyorWandItem> WAND=ITEMS.register("conveyor_wand",ConveyorWandItem::new);
 private static DeferredBlock<ConveyorBeltBlock> block(String name,ConveyorBeltBlock.Kind kind){var holder=BLOCKS.register(name,()->new ConveyorBeltBlock(kind));ITEMS.register(name,()->new ConveyorBlockItem(holder.get(),name));return holder;}
 public static DeferredBlock<ConveyorBeltBlock> belt(ConveyorWandItem.Type type){return switch(type){case EXPRESS->EXPRESS;case DOUBLE->DOUBLE;case TRIPLE->TRIPLE;default->REGULAR;};}
 public static ResourceLocation id(String name){return ResourceLocation.fromNamespaceAndPath("hbm",name);}public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(ConveyorWandItem::beforeBreak);}public static List<ItemStack> creativeStacks(String tab){if(!tab.equals("machine"))return List.of();return java.util.stream.IntStream.range(0,4).mapToObj(i->ConveyorWandItem.make(i,1)).toList();}private ConveyorContent(){}
}

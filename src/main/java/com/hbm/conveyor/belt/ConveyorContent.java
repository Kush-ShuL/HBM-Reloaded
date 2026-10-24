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
 private static final Map<ConveyorWandItem.Type,DeferredItem<ConveyorWandItem>> WANDS=new EnumMap<>(ConveyorWandItem.Type.class);
 static{for(var type:ConveyorWandItem.Type.values())WANDS.put(type,ITEMS.register(wandId(type).getPath(),()->new ConveyorWandItem(type)));}
 public static ConveyorWandItem wand(ConveyorWandItem.Type type){return WANDS.get(type).get();}
 public static ResourceLocation wandId(ConveyorWandItem.Type type){return id("conveyor_wand_"+type.name().toLowerCase(Locale.ROOT));}
 private static DeferredBlock<ConveyorBeltBlock> block(String name,ConveyorBeltBlock.Kind kind){var holder=BLOCKS.register(name,()->new ConveyorBeltBlock(kind));ITEMS.register(name,()->new ConveyorBlockItem(holder.get(),name));return holder;}
 public static DeferredBlock<ConveyorBeltBlock> belt(ConveyorWandItem.Type type){return switch(type){case EXPRESS->EXPRESS;case DOUBLE->DOUBLE;case TRIPLE->TRIPLE;default->REGULAR;};}
 public static ResourceLocation id(String name){return ResourceLocation.fromNamespaceAndPath("hbm",name);}public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(ConveyorWandItem::beforeBreak);}public static List<ItemStack> creativeStacks(String tab){if(!tab.equals("machine"))return List.of();return Arrays.stream(ConveyorWandItem.Type.values()).map(type->ConveyorWandItem.make(type,1)).toList();}private ConveyorContent(){}
}

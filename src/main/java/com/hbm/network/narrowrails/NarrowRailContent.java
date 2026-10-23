package com.hbm.network.narrowrails;
import java.util.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;
public final class NarrowRailContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final Map<SourceCartRail.Kind,DeferredBlock<SourceCartRail>> RAILS=new EnumMap<>(SourceCartRail.Kind.class);
 public static final Map<SourceCartRail.Kind,DeferredItem<SourceCartRailItem>> RAIL_ITEMS=new EnumMap<>(SourceCartRail.Kind.class);
 static{for(var kind:SourceCartRail.Kind.values()){var block=BLOCKS.register(kind.id,()->new SourceCartRail(kind));RAILS.put(kind,block);RAIL_ITEMS.put(kind,ITEMS.register(kind.id,()->new SourceCartRailItem(block.get())));}}
 public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);bus.addListener(NarrowRailContent::creative);}
 private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES))for(var kind:SourceCartRail.Kind.values())event.accept(RAIL_ITEMS.get(kind).get());}
 private NarrowRailContent(){}
}

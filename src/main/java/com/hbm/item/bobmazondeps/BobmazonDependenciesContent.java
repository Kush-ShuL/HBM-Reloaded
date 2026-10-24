package com.hbm.item.bobmazondeps;

import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;

public final class BobmazonDependenciesContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredItem<MatchstickItem> MATCH=ITEMS.register("matchstick",MatchstickItem::new);
 public static final DeferredBlock<SnowglobeBlock> SNOWGLOBE=BLOCKS.register("snowglobe",SnowglobeBlock::new);public static final DeferredBlock<PlushieBlock> PLUSHIE=BLOCKS.register("plushie",PlushieBlock::new);
 private static final List<DeferredItem<TrinketItem>> SNOW_ITEMS=registerTypes(true),PLUSH_ITEMS=registerTypes(false);
 private static List<DeferredItem<TrinketItem>> registerTypes(boolean snow){var items=new ArrayList<DeferredItem<TrinketItem>>();Enum<?>[] types=snow?TrinketTypes.Snowglobe.values():TrinketTypes.Plushie.values();for(int i=0;i<types.length;i++){int type=i;items.add(ITEMS.register((snow?"snowglobe_":"plushie_")+types[i].name().toLowerCase(Locale.ROOT),()->new TrinketItem(snow?SNOWGLOBE.get():PLUSHIE.get(),snow,type)));}return List.copyOf(items);}
 public static List<TrinketItem> items(boolean snow){return (snow?SNOW_ITEMS:PLUSH_ITEMS).stream().map(DeferredItem::get).toList();}
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SnowglobeEntity>> SNOW_ENTITY=ENTITIES.register("snowglobe",()->BlockEntityType.Builder.of(SnowglobeEntity::new,SNOWGLOBE.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PlushieEntity>> PLUSH_ENTITY=ENTITIES.register("plushie",()->BlockEntityType.Builder.of(PlushieEntity::new,PLUSHIE.get()).build(null));
 public static java.util.function.Consumer<SnowglobeEntity> CLIENT_OPEN=tile->{};
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);bus.addListener((BuildCreativeModeTabContentsEvent event)->{if(event.getTabKey()==CreativeModeTabs.TOOLS_AND_UTILITIES)event.accept(MATCH.get());});}
 public static int metadata(ItemStack stack){return stack.getItem()instanceof TrinketItem item?item.sourceType:0;}
 public static ItemStack stack(boolean snow,int meta){var items=snow?SNOW_ITEMS:PLUSH_ITEMS;return new ItemStack(items.get(Math.abs(meta%items.size())).get());}
 public static List<ItemStack> visibleTrinkets(){var result=new ArrayList<ItemStack>();for(int i=1;i<TrinketTypes.Snowglobe.values().length;i++)result.add(stack(true,i));for(int i=1;i<TrinketTypes.Plushie.values().length;i++)result.add(stack(false,i));return result;}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?visibleTrinkets():List.of();}
 public static List<ItemStack> indexedItems(){var result=new ArrayList<ItemStack>();result.add(new ItemStack(MATCH.get()));for(int i=0;i<TrinketTypes.Snowglobe.values().length;i++)result.add(stack(true,i));for(int i=0;i<TrinketTypes.Plushie.values().length;i++)result.add(stack(false,i));return result;}
 public static List<ItemStack> hiddenTrinkets(){return List.of(stack(true,0),stack(false,0));}
 public static List<ItemStack> allStacks(){return indexedItems();}
 private BobmazonDependenciesContent(){}
}

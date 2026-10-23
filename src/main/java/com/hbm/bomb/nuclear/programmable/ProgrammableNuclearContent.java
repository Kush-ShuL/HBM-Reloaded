package com.hbm.bomb.nuclear.programmable;
import com.hbm.item.BasicBlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Closed source programmable bombs, kept separate from the original nine static assembly layouts. */
public final class ProgrammableNuclearContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
 public static final DeferredBlock<TimedBalefireBlock> TIMED=BLOCKS.register("nuke_fstbmb",TimedBalefireBlock::new);
 public static final DeferredItem<BasicBlockItem> TIMED_ITEM=ITEMS.register("nuke_fstbmb",()->new BasicBlockItem(TIMED.get(),new Item.Properties(),"NukeBalefire","nuke_fstbmb"));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<TimedBalefireBlockEntity>> TIMED_ENTITY=BLOCK_ENTITIES.register("nuke_fstbmb",()->BlockEntityType.Builder.of(TimedBalefireBlockEntity::new,TIMED.get()).build(null));
 public static final DeferredHolder<MenuType<?>,MenuType<TimedBalefireMenu>> TIMED_MENU=MENUS.register("nuke_fstbmb",()->net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create(TimedBalefireMenu::new));
 public static final DeferredRegister<net.minecraft.world.entity.EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
 public static final DeferredBlock<CustomNuclearBlock> CUSTOM=BLOCKS.register("nuke_custom",CustomNuclearBlock::new);
 public static final DeferredItem<BasicBlockItem> CUSTOM_ITEM=ITEMS.register("nuke_custom",()->new BasicBlockItem(CUSTOM.get(),new Item.Properties(),"NukeCustom","nuke_custom"));
 public static final DeferredItem<com.hbm.bomb.nuclear.NuclearStarterKit> CUSTOM_KIT=ITEMS.register("custom_kit",()->new com.hbm.bomb.nuclear.NuclearStarterKit("custom_kit"));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CustomNuclearBlockEntity>> CUSTOM_ENTITY=BLOCK_ENTITIES.register("nuke_custom",()->BlockEntityType.Builder.of(CustomNuclearBlockEntity::new,CUSTOM.get()).build(null));
 public static final DeferredHolder<MenuType<?>,MenuType<CustomNuclearMenu>> CUSTOM_MENU=MENUS.register("nuke_custom",()->net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create(CustomNuclearMenu::new));
 public static final DeferredHolder<net.minecraft.world.entity.EntityType<?>,net.minecraft.world.entity.EntityType<NuclearRainbowCloud>> RAINBOW=ENTITIES.register("cloud_rainbow",()->net.minecraft.world.entity.EntityType.Builder.<NuclearRainbowCloud>of(NuclearRainbowCloud::new,net.minecraft.world.entity.MobCategory.MISC).sized(1,4).fireImmune().clientTrackingRange(63).updateInterval(1).build("hbm:cloud_rainbow"));
 public static final DeferredHolder<net.minecraft.world.entity.EntityType<?>,net.minecraft.world.entity.EntityType<FallingCustomNuclearBomb>> FALLING=ENTITIES.register("falling_bomb",()->net.minecraft.world.entity.EntityType.Builder.<FallingCustomNuclearBomb>of(FallingCustomNuclearBomb::new,net.minecraft.world.entity.MobCategory.MISC).sized(.98F,.98F).clientTrackingRange(63).updateInterval(1).build("hbm:falling_bomb"));
 public static final java.util.List<DeferredItem<SourceCircuitItem>> CIRCUITS=SourceCircuitItem.register(ITEMS);
 public static final DeferredItem<SourceSemtexItem> SEMTEX=ITEMS.register("ingot_semtex",SourceSemtexItem::new);
 public static final DeferredItem<SourceNuclearWasteItem> WASTE=ITEMS.register("nuclear_waste",()->new SourceNuclearWasteItem());
 public static final DeferredItem<SourceNuclearWasteItem> TRINITITE=ITEMS.register("trinitite",()->new SourceNuclearWasteItem());
 public static final DeferredItem<SourceDroppedCellItem> ANTIMATTER=ITEMS.register("cell_antimatter",()->new SourceDroppedCellItem(false));
 public static final DeferredItem<SourceDroppedCellItem> ANTI_SCHRAB=ITEMS.register("cell_anti_schrabidium",()->new SourceDroppedCellItem(true));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);BLOCK_ENTITIES.register(bus);MENUS.register(bus);ENTITIES.register(bus);}
 public static java.util.List<ItemStack> creativeStacks(String tab){return switch(tab){case "nuke"->java.util.List.of(new ItemStack(TIMED_ITEM.get()),new ItemStack(CUSTOM_ITEM.get()),new ItemStack(CUSTOM_KIT.get()));case "parts"->java.util.stream.Stream.concat(SourceCircuitItem.creativeStacks().stream(),java.util.stream.Stream.of(new ItemStack(SEMTEX.get()),new ItemStack(WASTE.get()),new ItemStack(TRINITITE.get()))).toList();case "control"->java.util.List.of(new ItemStack(ANTIMATTER.get()),new ItemStack(ANTI_SCHRAB.get()));default->java.util.List.of();};}

 private ProgrammableNuclearContent(){}
}

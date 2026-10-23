package com.hbm.block.rebar;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
public final class RebarContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
 public static final DeferredBlock<RebarBlock> BLOCK=BLOCKS.register("rebar",RebarBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);public static final DeferredItem<RebarPlacerItem> PLACER=ITEMS.register("rebar_placer",RebarPlacerItem::new);public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RebarBlockEntity>> ENTITY=ENTITIES.register("rebar",()->BlockEntityType.Builder.of(RebarBlockEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<MenuType<?>,MenuType<RebarPlacerMenu>> MENU=MENUS.register("rebar_placer",()->new MenuType<>((IContainerFactory<RebarPlacerMenu>)RebarPlacerMenu::new,FeatureFlags.DEFAULT_FLAGS));
 public static void register(IEventBus bus,ModContainer container){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);container.registerConfig(ModConfig.Type.CLIENT,RebarConfig.SPEC,"hbm-rebar-client.toml");}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(new ItemStack(ITEM.get())):tab.equals("consumable")?List.of(new ItemStack(PLACER.get())):List.of();}
 private RebarContent(){}
}

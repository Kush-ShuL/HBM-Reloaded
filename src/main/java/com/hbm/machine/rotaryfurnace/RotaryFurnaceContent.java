package com.hbm.machine.rotaryfurnace;
import java.util.*;
import net.minecraft.core.registries.*;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
/** Private source fluids, no energy or invented computer interface. */
public final class RotaryFurnaceContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
 public static final DeferredBlock<RotaryFurnaceBlock> BLOCK=BLOCKS.register("machine_rotary_furnace",RotaryFurnaceBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RotaryFurnaceEntity>> ENTITY=ENTITIES.register("rotary_furnace",()->BlockEntityType.Builder.of(RotaryFurnaceEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RotaryFurnacePort>> PORT=ENTITIES.register("rotary_furnace_port",()->BlockEntityType.Builder.of(RotaryFurnacePort::new,BLOCK.get()).build(null));
 public static final DeferredHolder<MenuType<?>,MenuType<RotaryFurnaceMenu>> MENU=MENUS.register("rotary_furnace",()->new MenuType<>((IContainerFactory<RotaryFurnaceMenu>)RotaryFurnaceMenu::new,FeatureFlags.DEFAULT_FLAGS));public static final DeferredHolder<RecipeType<?>,RecipeType<RotaryFurnaceRecipe>> TYPE=TYPES.register("rotary_furnace",()->new RecipeType<RotaryFurnaceRecipe>(){});public static final DeferredHolder<RecipeSerializer<?>,RotaryFurnaceRecipe.Serializer> SERIALIZER=SERIALIZERS.register("rotary_furnace",RotaryFurnaceRecipe.Serializer::new);
 public static void register(IEventBus bus,net.neoforged.fml.ModContainer container){RotaryFurnaceConfig.register(bus,container);BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);bus.addListener(RotaryFurnaceContent::capabilities);RotaryFurnaceRecipes.register();}
 private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PORT.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}public static List<ItemStack> allStacks(){return List.of(new ItemStack(ITEM.get()));}public static List<Item> indexedItems(){return List.of(ITEM.get());}private RotaryFurnaceContent(){}
}

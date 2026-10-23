package com.hbm.machine.electrolyser;
import java.util.List;
import net.minecraft.core.registries.Registries;
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
public final class ElectrolyserContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
 public static final DeferredBlock<ElectrolyserBlock> BLOCK=BLOCKS.register("machine_electrolyser",ElectrolyserBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.register("machine_electrolyser",()->new BlockItem(BLOCK.get(),new Item.Properties()));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ElectrolyserBlockEntity>> ENTITY=ENTITIES.register("electrolyser",()->BlockEntityType.Builder.of(ElectrolyserBlockEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ElectrolyserPort>> PORT=ENTITIES.register("electrolyser_port",()->BlockEntityType.Builder.of(ElectrolyserPort::new,BLOCK.get()).build(null));
 public static final DeferredHolder<MenuType<?>,MenuType<ElectrolyserMenu>> FLUID_MENU=MENUS.register("electrolyser_fluid",()->new MenuType<>((IContainerFactory<ElectrolyserMenu>)((id,inv,buf)->new ElectrolyserMenu(id,inv,buf,false)),FeatureFlags.DEFAULT_FLAGS)),METAL_MENU=MENUS.register("electrolyser_metal",()->new MenuType<>((IContainerFactory<ElectrolyserMenu>)((id,inv,buf)->new ElectrolyserMenu(id,inv,buf,true)),FeatureFlags.DEFAULT_FLAGS));
 public static final DeferredHolder<RecipeType<?>,RecipeType<ElectrolyserRecipe>> TYPE=TYPES.register("electrolyser",()->new RecipeType<ElectrolyserRecipe>(){});public static final DeferredHolder<RecipeSerializer<?>,ElectrolyserRecipe.Serializer> SERIALIZER=SERIALIZERS.register("electrolyser",ElectrolyserRecipe.Serializer::new);
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);ElectrolyserRecipes.register();bus.addListener(ElectrolyserContent::capabilities);bus.addListener(ElectrolyserControl::register);}
 private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PORT.get(),(port,side)->{var machine=port.core();return machine==null?null:side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side);});}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
 private ElectrolyserContent(){}
}

package com.hbm.machine.foundry.crucible;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
public final class CrucibleContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
 public static final DeferredBlock<ThermalBlock> CRUCIBLE=BLOCKS.register("machine_crucible",()->new ThermalBlock(false)),FIREBOX=BLOCKS.register("heater_firebox",()->new ThermalBlock(true));
 public static final DeferredItem<BlockItem> CRUCIBLE_ITEM=ITEMS.registerSimpleBlockItem("machine_crucible",CRUCIBLE),FIREBOX_ITEM=ITEMS.registerSimpleBlockItem("heater_firebox",FIREBOX);
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CrucibleBlockEntity>> CRUCIBLE_ENTITY=ENTITIES.register("machine_crucible",()->BlockEntityType.Builder.of(CrucibleBlockEntity::new,CRUCIBLE.get()).build(null));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FireboxBlockEntity>> FIREBOX_ENTITY=ENTITIES.register("heater_firebox",()->BlockEntityType.Builder.of(FireboxBlockEntity::new,FIREBOX.get()).build(null));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ThermalProxy>> PROXY=ENTITIES.register("crucible_firebox_proxy",()->BlockEntityType.Builder.of(ThermalProxy::new,CRUCIBLE.get(),FIREBOX.get()).build(null));
 public static final DeferredHolder<MenuType<?>,MenuType<ThermalMenu>> CRUCIBLE_MENU=MENUS.register("machine_crucible",()->new MenuType<>((IContainerFactory<ThermalMenu>)(id,inventory,buffer)->new ThermalMenu(id,inventory,buffer,false),FeatureFlags.DEFAULT_FLAGS)),FIREBOX_MENU=MENUS.register("heater_firebox",()->new MenuType<>((IContainerFactory<ThermalMenu>)(id,inventory,buffer)->new ThermalMenu(id,inventory,buffer,true),FeatureFlags.DEFAULT_FLAGS));
 public static final DeferredHolder<RecipeType<?>,RecipeType<CrucibleRecipe>> TYPE=TYPES.register("crucible_alloy",()->new RecipeType<CrucibleRecipe>(){});public static final DeferredHolder<RecipeSerializer<?>,CrucibleRecipe.Serializer> SERIALIZER=SERIALIZERS.register("crucible_alloy",CrucibleRecipe.Serializer::new);
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);bus.addListener(CrucibleSelection::register);bus.addListener(CrucibleContent::capabilities);CrucibleRecipeConfig.register();}
 private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,CRUCIBLE_ENTITY.get(),(be,side)->new SidedInvWrapper(be,side==null?net.minecraft.core.Direction.UP:side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,FIREBOX_ENTITY.get(),(be,side)->new SidedInvWrapper(be,side==null?net.minecraft.core.Direction.UP:side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PROXY.get(),(be,side)->new SidedInvWrapper(be,side==null?net.minecraft.core.Direction.UP:side));}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(FIREBOX_ITEM.get()),new ItemStack(CRUCIBLE_ITEM.get())):List.of();}private CrucibleContent(){}
}

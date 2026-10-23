package com.hbm.machine.reactor.iter.breeder;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
public final class FusionBreederContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
 public static final DeferredBlock<FusionBreederBlock> BLOCK=BLOCKS.register("fusion_breeder",FusionBreederBlock::new);public static final DeferredItem<FusionBreederItem> ITEM=ITEMS.register("fusion_breeder",FusionBreederItem::new);public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FusionBreederEntity>> ENTITY=ENTITIES.register("fusion_breeder",()->BlockEntityType.Builder.of(FusionBreederEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FusionBreederPort>> PORT=ENTITIES.register("fusion_breeder_port",()->BlockEntityType.Builder.of(FusionBreederPort::new,BLOCK.get()).build(null));public static final DeferredHolder<MenuType<?>,MenuType<FusionBreederMenu>> MENU=MENUS.register("fusion_breeder",()->new MenuType<>((IContainerFactory<FusionBreederMenu>)FusionBreederMenu::new,FeatureFlags.DEFAULT_FLAGS));
 public static final DeferredHolder<RecipeType<?>,RecipeType<FluidBreedingRecipe>> FLUID_TYPE=TYPES.register("fluid_breeding",()->new RecipeType<>(){});public static final DeferredHolder<RecipeSerializer<?>,FluidBreedingRecipe.Serializer> FLUID_SERIALIZER=SERIALIZERS.register("fluid_breeding",FluidBreedingRecipe.Serializer::new);
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);FluidBreedingRecipes.register();}public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
 private FusionBreederContent(){}
}

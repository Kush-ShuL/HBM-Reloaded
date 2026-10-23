package com.hbm.machine.oilcatalyticcracker;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Actual steam-only machine: source has no energy API, inventory, screen, OC or ROR. */
public final class CatalyticCrackerContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
 public static final DeferredBlock<CatalyticCrackerBlock> BLOCK=BLOCKS.register("machine_catalytic_cracker",CatalyticCrackerBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CatalyticCrackerEntity>> ENTITY=ENTITIES.register("catalytic_cracker",()->BlockEntityType.Builder.of(CatalyticCrackerEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CatalyticCrackerPort>> PORT=ENTITIES.register("catalytic_cracker_port",()->BlockEntityType.Builder.of(CatalyticCrackerPort::new,BLOCK.get()).build(null));
 public static final DeferredHolder<RecipeType<?>,RecipeType<CatalyticCrackerRecipe>> TYPE=TYPES.register("catalytic_cracking",()->new RecipeType<CatalyticCrackerRecipe>(){});public static final DeferredHolder<RecipeSerializer<?>,CatalyticCrackerRecipe.Serializer> SERIALIZER=SERIALIZERS.register("catalytic_cracking",CatalyticCrackerRecipe.Serializer::new);
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);CatalyticCrackerRecipes.register();}public static List<ItemStack> creativeStacks(String tab){return "machine".equals(tab)?List.of(new ItemStack(ITEM.get())):List.of();}private CatalyticCrackerContent(){}
}

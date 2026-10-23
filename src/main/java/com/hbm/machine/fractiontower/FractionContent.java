package com.hbm.machine.fractiontower;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Original two actual modular fractioning blocks. No source inventory, HE or GUI. */
public final class FractionContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
 public static final DeferredBlock<FractionBlock> BLOCK=BLOCKS.register("machine_fraction_tower",()->new FractionBlock(false)),SPACER=BLOCKS.register("fraction_spacer",()->new FractionBlock(true));
 public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK),SPACER_ITEM=ITEMS.registerSimpleBlockItem(SPACER);
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FractionEntity>> ENTITY=ENTITIES.register("machine_fraction_tower",()->BlockEntityType.Builder.of(FractionEntity::new,BLOCK.get()).build(null));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FractionPort>> PORT=ENTITIES.register("fraction_tower_port",()->BlockEntityType.Builder.of(FractionPort::new,BLOCK.get()).build(null));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FractionSpacerEntity>> SPACER_ENTITY=ENTITIES.register("fraction_spacer",()->BlockEntityType.Builder.of(FractionSpacerEntity::new,SPACER.get()).build(null));
 public static final DeferredHolder<RecipeType<?>,RecipeType<FractionRecipe>> TYPE=TYPES.register("fractioning",()->new RecipeType<FractionRecipe>(){});public static final DeferredHolder<RecipeSerializer<?>,FractionRecipe.Serializer> SERIALIZER=SERIALIZERS.register("fractioning",FractionRecipe.Serializer::new);
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);FractionRecipeConfig.register();}
 public static List<ItemStack> creativeStacks(String tab){return "machine".equals(tab)?List.of(new ItemStack(ITEM.get()),new ItemStack(SPACER_ITEM.get())):List.of();}private FractionContent(){}
}

package com.hbm.machine.compressor;
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
public final class CompressorContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
 public static final DeferredBlock<CompressorBlock> BLOCK=BLOCKS.register("machine_compressor",()->new CompressorBlock(false)),COMPACT=BLOCKS.register("machine_compressor_compact",()->new CompressorBlock(true));
 public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK),COMPACT_ITEM=ITEMS.registerSimpleBlockItem(COMPACT);
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CompressorBlockEntity>> ENTITY=ENTITIES.register("compressor",()->BlockEntityType.Builder.of(CompressorBlockEntity::new,BLOCK.get(),COMPACT.get()).build(null));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CompressorPort>> PORT=ENTITIES.register("compressor_port",()->BlockEntityType.Builder.of(CompressorPort::new,BLOCK.get(),COMPACT.get()).build(null));
 public static final DeferredHolder<MenuType<?>,MenuType<CompressorMenu>> MENU=MENUS.register("compressor",()->new MenuType<>((IContainerFactory<CompressorMenu>)CompressorMenu::new,FeatureFlags.DEFAULT_FLAGS));
 public static final DeferredHolder<RecipeType<?>,RecipeType<CompressorRecipe>> TYPE=TYPES.register("compressor",()->new RecipeType<CompressorRecipe>(){});public static final DeferredHolder<RecipeSerializer<?>,CompressorRecipe.Serializer> SERIALIZER=SERIALIZERS.register("compressor",CompressorRecipe.Serializer::new);
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);bus.addListener(CompressorControl::register);bus.addListener(CompressorContent::capabilities);CompressorRecipeConfig.register();}
 private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(be,side)->side==null?new InvWrapper(be):new SidedInvWrapper(be,side));}
 public static List<ItemStack> stacks(){return List.of(new ItemStack(ITEM.get()),new ItemStack(COMPACT_ITEM.get()));}public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?stacks():List.of();}
 private CompressorContent(){}
}

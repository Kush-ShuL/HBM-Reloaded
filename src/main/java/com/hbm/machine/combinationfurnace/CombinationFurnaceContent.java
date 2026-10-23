package com.hbm.machine.combinationfurnace;
import com.hbm.item.BasicBlockItem;
import com.hbm.multiblock.Multiblocks;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.registries.*;
/** The actual registered Combination Furnace; neither a generic shell nor a vanilla furnace alias. */
public final class CombinationFurnaceContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredBlock<CombinationFurnaceBlock> BLOCK=BLOCKS.register("furnace_combination",CombinationFurnaceBlock::new);
    public static final DeferredItem<BasicBlockItem> ITEM=ITEMS.register("furnace_combination",()->new BasicBlockItem(BLOCK.get(),new Item.Properties(),"BlockGenericTooltip","furnace_combination"));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CombinationFurnaceEntity>> ENTITY=ENTITIES.register("furnace_combination",()->BlockEntityType.Builder.of(CombinationFurnaceEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<CombinationFurnaceMenu>> MENU=MENUS.register("furnace_combination",()->new MenuType<>(CombinationFurnaceMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<RecipeType<?>,RecipeType<CombinationFurnaceRecipe>> TYPE=TYPES.register("combination",()->new RecipeType<CombinationFurnaceRecipe>(){});
    public static final DeferredHolder<RecipeSerializer<?>,CombinationFurnaceRecipe.Serializer> SERIALIZER=SERIALIZERS.register("combination",CombinationFurnaceRecipe.Serializer::new);
    public static void register(IEventBus bus){Multiblocks.addInventoryProxyBlock(BLOCK);BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);CombinationFurnaceRecipes.register();bus.addListener(CombinationFurnaceContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
    public static Map<String,Item> indexedItems(){return Map.of("furnace_combination",ITEM.get());}public static List<ItemStack> allStacks(){return List.of(new ItemStack(ITEM.get()));}public static List<ItemStack> creativeStacks(String tab){return "machine".equals(tab)?allStacks():List.of();}
    private CombinationFurnaceContent(){}
}

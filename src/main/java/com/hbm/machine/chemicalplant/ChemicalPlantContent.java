package com.hbm.machine.chemicalplant;
import com.hbm.recipe.generic.Blueprints;
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
public final class ChemicalPlantContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredBlock<ChemicalPlantBlock> BLOCK=BLOCKS.register("machine_chemical_plant",ChemicalPlantBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ChemicalPlantBlockEntity>> ENTITY=ENTITIES.register("chemical_plant",()->BlockEntityType.Builder.of(ChemicalPlantBlockEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ChemicalPlantPort>> PORT=ENTITIES.register("chemical_plant_port",()->BlockEntityType.Builder.of(ChemicalPlantPort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<ChemicalPlantMenu>> MENU=MENUS.register("chemical_plant",()->new MenuType<>((IContainerFactory<ChemicalPlantMenu>)ChemicalPlantMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<RecipeType<?>,RecipeType<ChemicalPlantRecipe>> TYPE=TYPES.register("chemical",()->new RecipeType<ChemicalPlantRecipe>(){});public static final DeferredHolder<RecipeSerializer<?>,ChemicalPlantRecipe.Serializer> SERIALIZER=SERIALIZERS.register("chemical",ChemicalPlantRecipe.Serializer::new);
    private ChemicalPlantContent(){}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);Blueprints.register(bus);Blueprints.declareRecipeSet(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("hbm","chemical"),"ChemicalPlantRecipes");bus.addListener(ChemicalPlantSelection::register);bus.addListener(ChemicalPlantRecipeConfig::setup);bus.addListener(ChemicalPlantContent::capabilities);ChemicalPlantRecipeConfig.register();}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PORT.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
    public static List<ItemStack> creativeItems(){return List.of(new ItemStack(ITEM.get()));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?creativeItems():List.of();}
}

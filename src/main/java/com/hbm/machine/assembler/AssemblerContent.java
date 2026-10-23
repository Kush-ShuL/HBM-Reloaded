package com.hbm.machine.assembler;
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
public final class AssemblerContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredBlock<AssemblerBlock> BLOCK=BLOCKS.register("machine_assembly_machine",AssemblerBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<AssemblerBlockEntity>> ENTITY=ENTITIES.register("assembly_machine",()->BlockEntityType.Builder.of(AssemblerBlockEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<AssemblerPort>> PORT=ENTITIES.register("assembly_port",()->BlockEntityType.Builder.of(AssemblerPort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<AssemblerMenu>> MENU=MENUS.register("assembly_machine",()->new MenuType<>((IContainerFactory<AssemblerMenu>)AssemblerMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<RecipeType<?>,RecipeType<AssemblerRecipe>> TYPE=TYPES.register("assembly",()->new RecipeType<AssemblerRecipe>(){});public static final DeferredHolder<RecipeSerializer<?>,AssemblerRecipe.Serializer> SERIALIZER=SERIALIZERS.register("assembly",AssemblerRecipe.Serializer::new);
    private AssemblerContent(){}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);Blueprints.register(bus);Blueprints.declareRecipeSet(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("hbm","assembly"),"AssemblyMachineRecipes");bus.addListener(AssemblerSelection::register);bus.addListener(AssemblerRecipeConfig::setup);bus.addListener(AssemblerContent::capabilities);AssemblerRecipeConfig.register();}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PORT.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
    public static List<ItemStack> creativeItems(){return List.of(new ItemStack(ITEM.get()));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?creativeItems():tab.equals("template")?Blueprints.creativeStacks():List.of();}
}

package com.hbm.machine.custommachine;
import com.hbm.hazard.HbmHazards;
import java.util.*;
import java.util.function.Supplier;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
public final class CustomMachineContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredBlock<CustomMachineBlock> BLOCK=BLOCKS.register("custom_machine",CustomMachineBlock::new);public static final DeferredItem<CustomMachineItem> ITEM=ITEMS.register("custom_machine",()->new CustomMachineItem(BLOCK.get()));
    public static final DeferredBlock<CMAnchorBlock> ANCHOR=BLOCKS.register("custom_machine_anchor",CMAnchorBlock::new);public static final DeferredItem<BlockItem> ANCHOR_ITEM=ITEMS.registerSimpleBlockItem(ANCHOR);public static final DeferredItem<CMStructureItem> STRUCTURE_TOOL=ITEMS.register("structure_custommachine",CMStructureItem::new);
    public static final Map<CMComponentBlock.Kind,DeferredBlock<CMComponentBlock>> COMPONENTS=new EnumMap<>(CMComponentBlock.Kind.class);public static final Map<CMComponentBlock.Kind,DeferredItem<CMComponentItem>> COMPONENT_ITEMS=new EnumMap<>(CMComponentBlock.Kind.class);
    static {for(var kind:CMComponentBlock.Kind.values()){var block=BLOCKS.register(kind.id,()->kind==CMComponentBlock.Kind.PORT?new CMPortBlock():new CMComponentBlock(kind));COMPONENTS.put(kind,block);COMPONENT_ITEMS.put(kind,ITEMS.register(kind.id,()->new CMComponentItem(block.get())));}}
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CustomMachineEntity>> ENTITY=ENTITIES.register("custom_machine",()->BlockEntityType.Builder.of(CustomMachineEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CMPortEntity>> PORT=ENTITIES.register("custom_machine_port",()->BlockEntityType.Builder.of(CMPortEntity::new,COMPONENTS.get(CMComponentBlock.Kind.PORT).get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<CustomMachineMenu>> MENU=MENUS.register("custom_machine",()->new MenuType<>((IContainerFactory<CustomMachineMenu>)CustomMachineMenu::new,FeatureFlags.DEFAULT_FLAGS));public static final DeferredHolder<RecipeType<?>,RecipeType<CustomMachineRecipe>> TYPE=TYPES.register("custom_machine",()->new RecipeType<CustomMachineRecipe>(){});public static final DeferredHolder<RecipeSerializer<?>,CustomMachineRecipe.Serializer> SERIALIZER=SERIALIZERS.register("custom_machine",CustomMachineRecipe.Serializer::new);
    public static Supplier<String> LANGUAGE=()->"en_US";
    public static void register(IEventBus bus){com.hbm.multiblock.proxy.SourceProxyContent.register(bus);BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);CustomMachineRecipes.register();bus.addListener(CustomMachineNetwork::register);bus.addListener(CustomMachineContent::capabilities);bus.addListener((FMLCommonSetupEvent event)->event.enqueueWork(CustomMachinesConfig::initialize));}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->new SidedInvWrapper(machine,side==null?Direction.UP:side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PORT.get(),(machine,side)->new SidedInvWrapper(machine,side==null?Direction.UP:side));}
    public static ItemStack controller(int metadata){var stack=new ItemStack(ITEM.get());stack.set(HbmHazards.LEGACY_METADATA.get(),metadata);return stack;}
    public static ItemStack component(CMComponentBlock.Kind kind,int metadata){var stack=new ItemStack(COMPONENT_ITEMS.get(kind).get());if(kind.variants.length>1||metadata!=0)stack.set(HbmHazards.LEGACY_METADATA.get(),metadata);return stack;}
    public static List<ItemStack> controllers(){var result=new ArrayList<ItemStack>();for(int i=0;i<CustomMachinesConfig.niceList.size();i++)result.add(controller(i+100));return result;}
    public static List<ItemStack> creativeStacks(String tab){var result=new ArrayList<ItemStack>();if(tab.equals("consumable")){result.add(new ItemStack(STRUCTURE_TOOL.get()));return result;}if(!tab.equals("machine"))return result;result.addAll(controllers());result.add(new ItemStack(ANCHOR_ITEM.get()));for(var kind:CMComponentBlock.Kind.values())for(int i=0;i<kind.variants.length;i++)result.add(component(kind,i));return result;}
    public static List<ItemStack> allStacks(){var result=new ArrayList<>(creativeStacks("machine"));result.add(new ItemStack(STRUCTURE_TOOL.get()));return result;}
    public static Map<String,Item> indexedItems(){var map=new LinkedHashMap<String,Item>();map.put("custom_machine",ITEM.get());map.put("cm_anchor",ANCHOR_ITEM.get());map.put("structure_custommachine",STRUCTURE_TOOL.get());COMPONENT_ITEMS.forEach((kind,item)->map.put(kind.id,item.get()));return map;}
    private CustomMachineContent(){}
}

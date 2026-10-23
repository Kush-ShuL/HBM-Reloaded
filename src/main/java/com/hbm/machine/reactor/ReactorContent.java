package com.hbm.machine.reactor;
import com.hbm.hazard.HbmHazards;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
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
/** Legacy research-reactor family, source registry names preserved. */
public final class ReactorContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<EntityType<?>> MOB_ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredBlock<ResearchReactorBlock> RESEARCH=BLOCKS.register("machine_reactor_small",ResearchReactorBlock::new);public static final DeferredItem<BlockItem> RESEARCH_ITEM=ITEMS.registerSimpleBlockItem(RESEARCH);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ResearchReactorBlockEntity>> RESEARCH_ENTITY=ENTITIES.register("research_reactor",()->BlockEntityType.Builder.of(ResearchReactorBlockEntity::new,RESEARCH.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<ResearchReactorMenu>> RESEARCH_MENU=MENUS.register("research_reactor",()->new MenuType<>((IContainerFactory<ResearchReactorMenu>)ResearchReactorMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<EntityType<?>,EntityType<WastePlateEntity>> WASTE_ENTITY=MOB_ENTITIES.register("item_waste",()->EntityType.Builder.<WastePlateEntity>of(WastePlateEntity::new,MobCategory.MISC).sized(.25F,.25F).clientTrackingRange(6).updateInterval(20).build("hbm:item_waste"));
    public static final DeferredBlock<BreedingReactorBlock> BREEDING=BLOCKS.register("machine_reactor",BreedingReactorBlock::new);public static final DeferredItem<BlockItem> BREEDING_ITEM=ITEMS.registerSimpleBlockItem(BREEDING);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<BreedingReactorBlockEntity>> BREEDING_ENTITY=ENTITIES.register("breeding_reactor",()->BlockEntityType.Builder.of(BreedingReactorBlockEntity::new,BREEDING.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ReactorInventoryPort>> INVENTORY_PORT=ENTITIES.register("research_breeder_port",()->BlockEntityType.Builder.of(ReactorInventoryPort::new,BREEDING.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<BreedingReactorMenu>> BREEDING_MENU=MENUS.register("breeding_reactor",()->new MenuType<>((IContainerFactory<BreedingReactorMenu>)BreedingReactorMenu::new,FeatureFlags.DEFAULT_FLAGS));
    private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredHolder<RecipeType<?>,RecipeType<BreedingRecipe>> BREEDING_TYPE=TYPES.register("research_breeding",()->new RecipeType<BreedingRecipe>(){});
    public static final DeferredHolder<RecipeSerializer<?>,BreedingRecipe.Serializer> BREEDING_SERIALIZER=SERIALIZERS.register("research_breeding",BreedingRecipe.Serializer::new);
    private static final Map<String,DeferredItem<PlateFuelItem>> FUELS=new LinkedHashMap<>();private static final Map<String,DeferredItem<WastePlateItem>> WASTES=new LinkedHashMap<>();
    private static final Map<String,DeferredItem<WastePlateItem>> DEPLETED_FUELS=new LinkedHashMap<>();
    static{fuel("u233",2200000,PlateFuelItem.Function.SQUARE_ROOT,50);fuel("u235",2200000,PlateFuelItem.Function.SQUARE_ROOT,40);fuel("mox",2400000,PlateFuelItem.Function.LOGARITHM,50);fuel("pu239",2000000,PlateFuelItem.Function.NEGATIVE_QUADRATIC,50);fuel("sa326",2000000,PlateFuelItem.Function.LINEAR,80);fuel("ra226be",1300000,PlateFuelItem.Function.PASSIVE,30);fuel("pu238be",1000000,PlateFuelItem.Function.PASSIVE,50);}
    static{for(String type:List.of("natural_uranium","uranium","thorium","mox","plutonium","u233","u235","schrabidium","zfb_mox"))DEPLETED_FUELS.put(type,ITEMS.register("waste_"+type,WastePlateItem::new));}
    private ReactorContent(){}
    private static void fuel(String id,int life,PlateFuelItem.Function function,int reactivity){FUELS.put(id,ITEMS.register("plate_fuel_"+id,()->new PlateFuelItem(life,function,reactivity)));WASTES.put(id,ITEMS.register("waste_plate_"+id,WastePlateItem::new));}
    public static ItemStack spent(PlateFuelItem plate){for(var entry:FUELS.entrySet())if(entry.getValue().get()==plate){var stack=new ItemStack(WASTES.get(entry.getKey()).get());stack.set(HbmHazards.LEGACY_METADATA.get(),1);return stack;}throw new IllegalArgumentException("Unknown source plate fuel");}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);MOB_ENTITIES.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);BreedingRecipeConfig.register();bus.addListener(ReactorControlPacket::register);bus.addListener(ReactorContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,BREEDING_ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,INVENTORY_PORT.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,RESEARCH_ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
    public static List<ItemStack> creativeStacks(String tab){if(tab.equals("machine"))return List.of(new ItemStack(RESEARCH_ITEM.get()),new ItemStack(BREEDING_ITEM.get()));if(tab.equals("control"))return FUELS.values().stream().map(item->new ItemStack(item.get())).toList();if(tab.equals("parts")){var stacks=new ArrayList<ItemStack>();for(var item:DEPLETED_FUELS.values())for(int metadata=0;metadata<2;metadata++){var stack=new ItemStack(item.get());stack.set(HbmHazards.LEGACY_METADATA.get(),metadata);stacks.add(stack);}return stacks;}return List.of();}
    public static List<Item> fuelItems(){return FUELS.values().stream().map(value->(Item)value.get()).toList();}
    public static List<Item> wasteItems(){return java.util.stream.Stream.concat(WASTES.values().stream(),DEPLETED_FUELS.values().stream()).map(value->(Item)value.get()).toList();}
}

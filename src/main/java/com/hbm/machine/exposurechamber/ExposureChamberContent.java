package com.hbm.machine.exposurechamber;
import com.google.gson.*;
import java.util.*;
import net.minecraft.core.registries.*;
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
/** No external energy/fluid or invented computer interface. */
public final class ExposureChamberContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
 public static final DeferredBlock<ExposureChamberBlock> BLOCK=BLOCKS.register("machine_exposure_chamber",ExposureChamberBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ExposureChamberEntity>> ENTITY=ENTITIES.register("exposure_chamber",()->BlockEntityType.Builder.of(ExposureChamberEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ExposureChamberPort>> PORT=ENTITIES.register("exposure_chamber_port",()->BlockEntityType.Builder.of(ExposureChamberPort::new,BLOCK.get()).build(null));
 public static final DeferredHolder<MenuType<?>,MenuType<ExposureChamberMenu>> MENU=MENUS.register("exposure_chamber",()->new MenuType<>((IContainerFactory<ExposureChamberMenu>)ExposureChamberMenu::new,FeatureFlags.DEFAULT_FLAGS));public static final DeferredHolder<RecipeType<?>,RecipeType<ExposureChamberRecipe>> TYPE=TYPES.register("exposure_chamber",()->new RecipeType<ExposureChamberRecipe>(){});public static final DeferredHolder<RecipeSerializer<?>,ExposureChamberRecipe.Serializer> SERIALIZER=SERIALIZERS.register("exposure_chamber",ExposureChamberRecipe.Serializer::new);
 private static Set<String> sourceUpgrades=Set.of();
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);bus.addListener(ExposureChamberContent::capabilities);ExposureChamberRecipes.register();try(var stream=ExposureChamberContent.class.getResourceAsStream("/hbm/machine-exposure-chamber92-upgrades.json")){if(stream==null)throw new IllegalStateException("Missing original ItemMachineUpgrade identities");var ids=new HashSet<String>();for(var value:JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonArray())ids.add(value.getAsString());sourceUpgrades=Set.copyOf(ids);}catch(java.io.IOException error){throw new IllegalStateException(error);}}
 public static boolean isSourceMachineUpgrade(ItemStack stack){return sourceUpgrades.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());}
 private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PORT.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}public static List<ItemStack> allStacks(){return List.of(new ItemStack(ITEM.get()));}public static List<Item> indexedItems(){return List.of(ITEM.get());}private ExposureChamberContent(){}
}

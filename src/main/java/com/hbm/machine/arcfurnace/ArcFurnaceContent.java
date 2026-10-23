package com.hbm.machine.arcfurnace;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
public final class ArcFurnaceContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
 public static final DeferredBlock<ArcFurnaceBlock> BLOCK=BLOCKS.register("machine_arc_furnace",ArcFurnaceBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem("machine_arc_furnace",BLOCK);
 public static final DeferredBlock<ArcSandBlock> SAND=BLOCKS.register("sand_mix",ArcSandBlock::new);public static final DeferredItem<ArcSandBlock.SandItem> SAND_ITEM=ITEMS.register("sand_mix",()->new ArcSandBlock.SandItem(SAND.get()));
 public static final DeferredItem<ArcElectrodeItem> ELECTRODE=ITEMS.register("arc_electrode",()->new ArcElectrodeItem(false)),BURNT=ITEMS.register("arc_electrode_burnt",()->new ArcElectrodeItem(true));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ArcFurnaceBlockEntity>> ENTITY=ENTITIES.register("machine_arc_furnace",()->BlockEntityType.Builder.of(ArcFurnaceBlockEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ArcFurnacePort>> PORT=ENTITIES.register("arc_furnace_port",()->BlockEntityType.Builder.of(ArcFurnacePort::new,BLOCK.get()).build(null));
 public static final DeferredHolder<MenuType<?>,MenuType<ArcFurnaceMenu>> MENU=MENUS.register("machine_arc_furnace",()->new MenuType<>((IContainerFactory<ArcFurnaceMenu>)ArcFurnaceMenu::new,FeatureFlags.DEFAULT_FLAGS));
 public static final DeferredHolder<RecipeType<?>,RecipeType<ArcFurnaceRecipe>> RECIPE_TYPE=TYPES.register("arc_furnace",()->new RecipeType<ArcFurnaceRecipe>(){});public static final DeferredHolder<RecipeSerializer<?>,ArcFurnaceRecipe.Serializer> SERIALIZER=SERIALIZERS.register("arc_furnace",ArcFurnaceRecipe.Serializer::new);
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);bus.addListener(ArcFurnaceControl::register);bus.addListener(ArcFurnaceContent::capabilities);ArcFurnaceRecipes.register();}
 private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(be,side)->new SidedInvWrapper(be,side==null?net.minecraft.core.Direction.DOWN:side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PORT.get(),(be,side)->new SidedInvWrapper(be,side==null?net.minecraft.core.Direction.DOWN:side));}
 public static List<ItemStack> creativeStacks(String tab){var result=new ArrayList<ItemStack>();if(tab.equals("machine")){result.add(new ItemStack(ITEM.get()));for(int i=0;i<5;i++)result.add(SAND_ITEM.get().stack(i));}if(tab.equals("control"))for(var item:List.of(ELECTRODE.get(),BURNT.get()))for(int i=0;i<4;i++)result.add(item.stack(i));return result;}
 private ArcFurnaceContent(){}
}

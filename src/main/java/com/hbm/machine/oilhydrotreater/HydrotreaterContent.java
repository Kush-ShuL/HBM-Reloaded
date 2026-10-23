package com.hbm.machine.oilhydrotreater;
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
public final class HydrotreaterContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredBlock<HydrotreaterBlock> BLOCK=BLOCKS.register("machine_hydrotreater",HydrotreaterBlock::new);public static final DeferredItem<HydrotreaterItem> ITEM=ITEMS.register("machine_hydrotreater",()->new HydrotreaterItem(BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<HydrotreaterBlockEntity>> ENTITY=ENTITIES.register("hydrotreater",()->BlockEntityType.Builder.of(HydrotreaterBlockEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<HydrotreaterPort>> PORT=ENTITIES.register("hydrotreater_port",()->BlockEntityType.Builder.of(HydrotreaterPort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<HydrotreaterMenu>> MENU=MENUS.register("hydrotreater",()->new MenuType<>((IContainerFactory<HydrotreaterMenu>)HydrotreaterMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<RecipeType<?>,RecipeType<HydrotreaterRecipe>> TYPE=TYPES.register("hydrotreater",()->new RecipeType<HydrotreaterRecipe>(){});public static final DeferredHolder<RecipeSerializer<?>,HydrotreaterRecipe.Serializer> SERIALIZER=SERIALIZERS.register("hydrotreater",HydrotreaterRecipe.Serializer::new);
    private HydrotreaterContent(){}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);HydrotreaterRecipes.register();bus.addListener(HydrotreaterContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
}

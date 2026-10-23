package com.hbm.machine.refinery;
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
public final class RefineryContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredBlock<RefineryBlock> BLOCK=BLOCKS.register("machine_refinery",RefineryBlock::new);public static final DeferredItem<RefineryItem> ITEM=ITEMS.register("machine_refinery",()->new RefineryItem(BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RefineryBlockEntity>> ENTITY=ENTITIES.register("refinery",()->BlockEntityType.Builder.of(RefineryBlockEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RefineryPort>> PORT=ENTITIES.register("refinery_port",()->BlockEntityType.Builder.of(RefineryPort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<RefineryMenu>> MENU=MENUS.register("refinery",()->new MenuType<>((IContainerFactory<RefineryMenu>)RefineryMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<RecipeType<?>,RecipeType<RefineryRecipe>> TYPE=TYPES.register("refinery",()->new RecipeType<RefineryRecipe>(){});public static final DeferredHolder<RecipeSerializer<?>,RefineryRecipe.Serializer> SERIALIZER=SERIALIZERS.register("refinery",RefineryRecipe.Serializer::new);
    private RefineryContent(){}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);RefineryRecipeConfig.register();bus.addListener(RefineryContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PORT.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
}

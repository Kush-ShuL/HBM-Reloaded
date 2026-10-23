package com.hbm.machine.oilreformer;
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
public final class ReformerContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredBlock<ReformerBlock> BLOCK=BLOCKS.register("machine_catalytic_reformer",ReformerBlock::new);public static final DeferredItem<ReformerItem> ITEM=ITEMS.register("machine_catalytic_reformer",()->new ReformerItem(BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ReformerEntity>> ENTITY=ENTITIES.register("oil_reforming",()->BlockEntityType.Builder.of(ReformerEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ReformerPort>> PORT=ENTITIES.register("oil_reforming_port",()->BlockEntityType.Builder.of(ReformerPort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<ReformerMenu>> MENU=MENUS.register("oil_reforming",()->new MenuType<>((IContainerFactory<ReformerMenu>)ReformerMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<RecipeType<?>,RecipeType<ReformerRecipe>> TYPE=TYPES.register("oil_reforming",()->new RecipeType<ReformerRecipe>(){});public static final DeferredHolder<RecipeSerializer<?>,ReformerRecipe.Serializer> SERIALIZER=SERIALIZERS.register("oil_reforming",ReformerRecipe.Serializer::new);
    private ReformerContent(){}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);ReformerRecipeConfig.register();bus.addListener(ReformerContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
}

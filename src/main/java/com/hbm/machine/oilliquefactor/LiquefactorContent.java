package com.hbm.machine.oilliquefactor;
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
public final class LiquefactorContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredBlock<LiquefactorBlock> BLOCK=BLOCKS.register("machine_liquefactor",LiquefactorBlock::new);public static final DeferredItem<LiquefactorItem> ITEM=ITEMS.register("machine_liquefactor",()->new LiquefactorItem(BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<LiquefactorEntity>> ENTITY=ENTITIES.register("oil_liquefaction",()->BlockEntityType.Builder.of(LiquefactorEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<LiquefactorPort>> PORT=ENTITIES.register("oil_liquefaction_port",()->BlockEntityType.Builder.of(LiquefactorPort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<LiquefactorMenu>> MENU=MENUS.register("oil_liquefaction",()->new MenuType<>((IContainerFactory<LiquefactorMenu>)LiquefactorMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<RecipeType<?>,RecipeType<LiquefactorRecipe>> TYPE=TYPES.register("oil_liquefaction",()->new RecipeType<LiquefactorRecipe>(){});public static final DeferredHolder<RecipeSerializer<?>,LiquefactorRecipe.Serializer> SERIALIZER=SERIALIZERS.register("oil_liquefaction",LiquefactorRecipe.Serializer::new);
    private LiquefactorContent(){}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);LiquefactionRecipes.register();bus.addListener(LiquefactorContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PORT.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
}

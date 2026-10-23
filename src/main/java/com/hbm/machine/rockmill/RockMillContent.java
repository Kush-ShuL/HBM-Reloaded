package com.hbm.machine.rockmill;
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
public final class RockMillContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredBlock<RockMillBlock> BLOCK=BLOCKS.register("machine_rockmill",RockMillBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RockMillEntity>> ENTITY=ENTITIES.register("rockmill",()->BlockEntityType.Builder.of(RockMillEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RockMillPort>> PORT=ENTITIES.register("rockmill_port",()->BlockEntityType.Builder.of(RockMillPort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<RockMillMenu>> MENU=MENUS.register("rockmill",()->new MenuType<>((IContainerFactory<RockMillMenu>)RockMillMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<RecipeType<?>,RecipeType<RockMillRecipe>> TYPE=TYPES.register("rockmill",()->new RecipeType<RockMillRecipe>(){});public static final DeferredHolder<RecipeSerializer<?>,RockMillRecipe.Serializer> SERIALIZER=SERIALIZERS.register("rockmill",RockMillRecipe.Serializer::new);
    private RockMillContent(){}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);Blueprints.register(bus);Blueprints.declareRecipeSet(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("hbm","rockmill"),"RockMillRecipes");bus.addListener(RockMillSelection::register);bus.addListener(RockMillContent::capabilities);RockMillRecipeConfig.register();}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PORT.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
    public static List<ItemStack> creativeItems(){return List.of(new ItemStack(ITEM.get()));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?creativeItems():List.of();}
}

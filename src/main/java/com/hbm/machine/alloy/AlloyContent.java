package com.hbm.machine.alloy;
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
public final class AlloyContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    private static final DeferredRegister<net.neoforged.neoforge.common.crafting.IngredientType<?>> INGREDIENTS=DeferredRegister.create(net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.INGREDIENT_TYPES,"hbm");
    public static final DeferredHolder<net.neoforged.neoforge.common.crafting.IngredientType<?>,net.neoforged.neoforge.common.crafting.IngredientType<AlloyStackIngredient>> STACK_INGREDIENT=INGREDIENTS.register("alloy_legacy_stack",()->new net.neoforged.neoforge.common.crafting.IngredientType<>(AlloyStackIngredient.CODEC));
    public static final DeferredBlock<AlloyFurnaceBlock> OFF=BLOCKS.register("machine_difurnace_off",()->new AlloyFurnaceBlock(false));
    public static final DeferredBlock<AlloyFurnaceBlock> ON=BLOCKS.register("machine_difurnace_on",()->new AlloyFurnaceBlock(true));
    public static final DeferredBlock<AlloyExtensionBlock> EXTENSION=BLOCKS.register("machine_difurnace_extension",AlloyExtensionBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(OFF),ON_ITEM=ITEMS.registerSimpleBlockItem(ON),EXTENSION_ITEM=ITEMS.registerSimpleBlockItem(EXTENSION);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<AlloyBlockEntity>> ENTITY=ENTITIES.register("machine_difurnace",()->BlockEntityType.Builder.of(AlloyBlockEntity::new,OFF.get(),ON.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<AlloyExtensionProxy>> PROXY=ENTITIES.register("difurnace_extension",()->BlockEntityType.Builder.of(AlloyExtensionProxy::new,EXTENSION.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<AlloyMenu>> MENU=MENUS.register("difurnace",()->new MenuType<>((IContainerFactory<AlloyMenu>)AlloyMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<RecipeType<?>,RecipeType<AlloyRecipe>> TYPE=TYPES.register("alloy",()->new RecipeType<AlloyRecipe>() { @Override public String toString() { return "hbm:alloy"; } });
    public static final DeferredHolder<RecipeSerializer<?>,AlloyRecipe.Serializer> SERIALIZER=SERIALIZERS.register("alloy",AlloyRecipe.Serializer::new);
    private AlloyContent() {}
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); MENUS.register(bus); TYPES.register(bus); SERIALIZERS.register(bus); INGREDIENTS.register(bus); bus.addListener(AlloyContent::capabilities); AlloyRecipeConfig.register(bus); }
    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PROXY.get(),(proxy,side)->side==null?new InvWrapper(proxy):new SidedInvWrapper(proxy,side));
    }
    public static List<ItemStack> creativeItems() { return List.of(new ItemStack(ITEM.get()),new ItemStack(EXTENSION_ITEM.get())); }
}

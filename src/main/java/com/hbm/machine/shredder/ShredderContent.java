package com.hbm.machine.shredder;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ShredderContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "hbm");
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, "hbm");
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, "hbm");
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, "hbm");
    public static final DeferredBlock<ShredderBlock> BLOCK = BLOCKS.register("machine_shredder", ShredderBlock::new);
    public static final DeferredItem<BlockItem> ITEM = ITEMS.registerSimpleBlockItem(BLOCK);
    public static final DeferredItem<ShredderBladeItem> STEEL = ITEMS.register("blades_steel", () -> new ShredderBladeItem(400));
    public static final DeferredItem<ShredderBladeItem> TITANIUM = ITEMS.register("blades_titanium", () -> new ShredderBladeItem(500));
    public static final DeferredItem<ShredderBladeItem> DESH = ITEMS.register("blades_desh", () -> new ShredderBladeItem(0));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ShredderBlockEntity>> ENTITY = ENTITIES.register("machine_shredder", () -> BlockEntityType.Builder.of(ShredderBlockEntity::new, BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>, MenuType<ShredderMenu>> MENU = MENUS.register("machine_shredder", () -> new MenuType<>(ShredderMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<RecipeType<?>, RecipeType<ShredderRecipe>> RECIPE_TYPE = TYPES.register("shredder", () -> new RecipeType<ShredderRecipe>() { @Override public String toString() { return "hbm:shredder"; } });
    public static final DeferredHolder<RecipeSerializer<?>, ShredderRecipe.Serializer> SERIALIZER = SERIALIZERS.register("shredder", ShredderRecipe.Serializer::new);
    private ShredderContent() {}
    public static void register(IEventBus bus) {
        BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); MENUS.register(bus); TYPES.register(bus); SERIALIZERS.register(bus);
        bus.addListener(ShredderContent::capabilities); ShredderRecipes.register();
    }
    public static void register(IEventBus bus, ModContainer container) { register(bus); ShredderConfig.register(container); }
    private static void capabilities(RegisterCapabilitiesEvent event) { event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ENTITY.get(), (machine, side) -> side == null ? new InvWrapper(machine) : new SidedInvWrapper(machine, side)); }
    public static List<ItemStack> creativeItems(String tab) {
        return tab.equals("machine") ? List.of(new ItemStack(ITEM.get())) : tab.equals("control") ? blades() : List.of();
    }
    public static List<ItemStack> blades() { return List.of(new ItemStack(STEEL.get()), new ItemStack(TITANIUM.get()), new ItemStack(DESH.get())); }
}

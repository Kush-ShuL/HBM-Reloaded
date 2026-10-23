package com.hbm.machine.press;

import com.hbm.multiblock.Multiblocks;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PressContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "hbm");
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, "hbm");
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, "hbm");
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, "hbm");
    public static final DeferredBlock<PressBlock> BLOCK = BLOCKS.register("machine_press", PressBlock::new);
    public static final DeferredItem<BlockItem> ITEM = ITEMS.register("machine_press", () -> new BlockItem(BLOCK.get(), new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PressBlockEntity>> ENTITY = ENTITIES.register("machine_press", () -> BlockEntityType.Builder.of(PressBlockEntity::new, BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>, MenuType<PressMenu>> MENU = MENUS.register("machine_press", () -> new MenuType<>((IContainerFactory<PressMenu>) PressMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<RecipeType<?>, RecipeType<PressRecipe>> RECIPE_TYPE = TYPES.register("press", () -> new RecipeType<PressRecipe>() { @Override public String toString() { return "hbm:press"; } });
    public static final DeferredHolder<RecipeSerializer<?>, PressRecipe.Serializer> SERIALIZER = SERIALIZERS.register("press", PressRecipe.Serializer::new);
    static { Multiblocks.addInventoryProxyBlock(BLOCK); }
    private PressContent() {}
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); MENUS.register(bus); TYPES.register(bus); SERIALIZERS.register(bus); Multiblocks.register(bus); bus.addListener(PressContent::capabilities); PressWireRecipes.register(); }
    private static void capabilities(RegisterCapabilitiesEvent event) { event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ENTITY.get(), (machine, side) -> side == null ? new InvWrapper(machine) : new SidedInvWrapper(machine, side)); }
    public static List<ItemStack> creativeItems() { return List.of(new ItemStack(ITEM.get())); }
}

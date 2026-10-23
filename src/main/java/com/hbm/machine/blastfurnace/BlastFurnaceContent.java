package com.hbm.machine.blastfurnace;

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

/** X5846's new fuel-driven blast furnace; no HE or standard fluid capability. */
public final class BlastFurnaceContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredBlock<BlastFurnaceBlock> BLOCK=BLOCKS.register("machine_blast_furnace",BlastFurnaceBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<BlastFurnaceBlockEntity>> ENTITY=ENTITIES.register("machine_blast_furnace",()->BlockEntityType.Builder.of(BlastFurnaceBlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<BlastFurnacePort>> PORT=ENTITIES.register("blast_furnace_port",()->BlockEntityType.Builder.of(BlastFurnacePort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<BlastFurnaceMenu>> MENU=MENUS.register("blast_furnace",()->new MenuType<>((IContainerFactory<BlastFurnaceMenu>)BlastFurnaceMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<RecipeType<?>,RecipeType<BlastFurnaceRecipe>> TYPE=TYPES.register("blast_furnace",()->new RecipeType<BlastFurnaceRecipe>(){});
    public static final DeferredHolder<RecipeSerializer<?>,BlastFurnaceRecipe.Serializer> SERIALIZER=SERIALIZERS.register("blast_furnace",BlastFurnaceRecipe.Serializer::new);
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);bus.addListener(BlastFurnaceContent::capabilities);BlastFurnaceRecipes.register();}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PORT.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    private BlastFurnaceContent(){}
}

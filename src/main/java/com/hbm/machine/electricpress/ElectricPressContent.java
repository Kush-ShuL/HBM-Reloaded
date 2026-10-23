package com.hbm.machine.electricpress;

import com.hbm.multiblock.Multiblocks;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

public final class ElectricPressContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<ElectricPressBlock> ELECTRIC=BLOCKS.register("machine_epress",ElectricPressBlock::new);
    public static final DeferredBlock<ConveyorPressBlock> CONVEYOR=BLOCKS.register("machine_conveyor_press",ConveyorPressBlock::new);
    public static final DeferredItem<ElectricPressItem> ELECTRIC_ITEM=ITEMS.register("machine_epress",()->new ElectricPressItem(ELECTRIC.get(),false));
    public static final DeferredItem<ElectricPressItem> CONVEYOR_ITEM=ITEMS.register("machine_conveyor_press",()->new ElectricPressItem(CONVEYOR.get(),true));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ElectricPressEntity>> ELECTRIC_ENTITY=ENTITIES.register("electric_press",()->BlockEntityType.Builder.of(ElectricPressEntity::new,ELECTRIC.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ConveyorPressEntity>> CONVEYOR_ENTITY=ENTITIES.register("conveyor_press",()->BlockEntityType.Builder.of(ConveyorPressEntity::new,CONVEYOR.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<ElectricPressMenu>> MENU=MENUS.register("electric_press",()->new MenuType<>((IContainerFactory<ElectricPressMenu>)ElectricPressMenu::new,FeatureFlags.DEFAULT_FLAGS));
    private static final DeferredRegister<net.neoforged.neoforge.common.crafting.IngredientType<?>> INGREDIENTS=DeferredRegister.create(net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.INGREDIENT_TYPES,"hbm");
    public static final DeferredHolder<net.neoforged.neoforge.common.crafting.IngredientType<?>,net.neoforged.neoforge.common.crafting.IngredientType<com.hbm.machine.press.PressSourceIngredient>> SOURCE_INGREDIENT=INGREDIENTS.register("press_source_stack",()->new net.neoforged.neoforge.common.crafting.IngredientType<>(com.hbm.machine.press.PressSourceIngredient.CODEC));
    static{Multiblocks.addInventoryProxyBlock(ELECTRIC);}
    private ElectricPressContent(){}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);INGREDIENTS.register(bus);Multiblocks.register(bus);bus.addListener(ElectricPressContent::capabilities);com.hbm.machine.press.PressRecipeConfig.register();}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ELECTRIC_ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,CONVEYOR_ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ELECTRIC_ITEM.get()),new ItemStack(CONVEYOR_ITEM.get())):List.of();}
}

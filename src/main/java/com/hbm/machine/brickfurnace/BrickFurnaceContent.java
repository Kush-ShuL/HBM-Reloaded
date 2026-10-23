package com.hbm.machine.brickfurnace;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.registries.*;

public final class BrickFurnaceContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<BrickFurnaceBlock> OFF=BLOCKS.register("machine_furnace_brick_off",()->new BrickFurnaceBlock(false));
    public static final DeferredBlock<BrickFurnaceBlock> ON=BLOCKS.register("machine_furnace_brick_on",()->new BrickFurnaceBlock(true));
    public static final DeferredItem<BlockItem> OFF_ITEM=ITEMS.registerSimpleBlockItem(OFF);
    public static final DeferredItem<BlockItem> ON_ITEM=ITEMS.registerSimpleBlockItem(ON);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<BrickFurnaceEntity>> ENTITY=ENTITIES.register("brick_furnace",()->BlockEntityType.Builder.of(BrickFurnaceEntity::new,OFF.get(),ON.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<BrickFurnaceMenu>> MENU=MENUS.register("brick_furnace",()->new MenuType<>(BrickFurnaceMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(BrickFurnaceContent::capabilities);bus.addListener(BrickFurnaceContent::setup);}
    private static void setup(FMLCommonSetupEvent event){event.enqueueWork(BrickFurnaceEntity::initializeBurnSpeeds);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(furnace,side)->side==null?new InvWrapper(furnace):new SidedInvWrapper(furnace,side));}
    public static List<ItemStack> creativeStacks(String tab){return "machine".equals(tab)?List.of(new ItemStack(OFF_ITEM.get())):List.of();}
    private BrickFurnaceContent(){}
}

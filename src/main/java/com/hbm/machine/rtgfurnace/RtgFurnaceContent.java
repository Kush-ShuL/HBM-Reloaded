package com.hbm.machine.rtgfurnace;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.registries.*;

/** Both true deprecated registrations, deliberately no invented creative entry or recipe. */
public final class RtgFurnaceContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<RtgFurnaceBlock> OFF=BLOCKS.register("machine_rtg_furnace_off",()->new RtgFurnaceBlock(false));
    public static final DeferredBlock<RtgFurnaceBlock> ON=BLOCKS.register("machine_rtg_furnace_on",()->new RtgFurnaceBlock(true));
    public static final DeferredItem<net.minecraft.world.item.BlockItem> OFF_ITEM=ITEMS.registerSimpleBlockItem(OFF);
    public static final DeferredItem<net.minecraft.world.item.BlockItem> ON_ITEM=ITEMS.registerSimpleBlockItem(ON);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RtgFurnaceEntity>> ENTITY=ENTITIES.register("rtg_furnace",()->BlockEntityType.Builder.of(RtgFurnaceEntity::new,OFF.get(),ON.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<RtgFurnaceMenu>> MENU=MENUS.register("rtg_furnace",()->new MenuType<>(RtgFurnaceMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(RtgFurnaceContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(furnace,side)->side==null?new InvWrapper(furnace):new SidedInvWrapper(furnace,side));}
    private RtgFurnaceContent(){}
}

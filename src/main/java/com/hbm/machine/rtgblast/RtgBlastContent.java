package com.hbm.machine.rtgblast;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.registries.*;

public final class RtgBlastContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<RtgBlastBlock> OFF=BLOCKS.register("machine_difurnace_rtg_off",()->new RtgBlastBlock(false));
    public static final DeferredBlock<RtgBlastBlock> ON=BLOCKS.register("machine_difurnace_rtg_on",()->new RtgBlastBlock(true));
    public static final DeferredItem<BlockItem> OFF_ITEM=ITEMS.registerSimpleBlockItem(OFF),ON_ITEM=ITEMS.registerSimpleBlockItem(ON);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RtgBlastEntity>> ENTITY=ENTITIES.register("rtg_blast_furnace",()->BlockEntityType.Builder.of(RtgBlastEntity::new,OFF.get(),ON.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<RtgBlastMenu>> MENU=MENUS.register("rtg_blast_furnace",()->new MenuType<>(RtgBlastMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(RtgBlastContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(furnace,side)->side==null?new InvWrapper(furnace):new SidedInvWrapper(furnace,side));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(OFF_ITEM.get())):List.of();}
    private RtgBlastContent(){}
}

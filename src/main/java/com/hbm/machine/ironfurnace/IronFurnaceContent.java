package com.hbm.machine.ironfurnace;

import com.hbm.item.BasicBlockItem;
import com.hbm.multiblock.Multiblocks;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.registries.*;

/** The actual passive five-slot, two-fuel, eight-cell source iron furnace. */
public final class IronFurnaceContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<IronFurnaceBlock> BLOCK=BLOCKS.register("furnace_iron",IronFurnaceBlock::new);
    public static final DeferredItem<BasicBlockItem> ITEM=ITEMS.register("furnace_iron",()->new BasicBlockItem(BLOCK.get(),new Item.Properties(),"BlockGenericTooltip","furnace_iron"));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<IronFurnaceEntity>> ENTITY=ENTITIES.register("furnace_iron",()->BlockEntityType.Builder.of(IronFurnaceEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<IronFurnaceMenu>> MENU=MENUS.register("furnace_iron",()->new MenuType<>(IronFurnaceMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus){Multiblocks.addInventoryProxyBlock(BLOCK);BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(IronFurnaceContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
    public static Map<String,Item> indexedItems(){return Map.of("furnace_iron",ITEM.get());}
    public static List<ItemStack> allStacks(){return List.of(new ItemStack(ITEM.get()));}
    public static List<ItemStack> creativeStacks(String tab){return "machine".equals(tab)?allStacks():List.of();}
    private IronFurnaceContent(){}
}

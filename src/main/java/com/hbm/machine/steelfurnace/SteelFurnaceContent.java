package com.hbm.machine.steelfurnace;
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
/** Actual three-lane, private-TU source steel furnace. */
public final class SteelFurnaceContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<SteelFurnaceBlock> BLOCK=BLOCKS.register("furnace_steel",SteelFurnaceBlock::new);
    public static final DeferredItem<BasicBlockItem> ITEM=ITEMS.register("furnace_steel",()->new BasicBlockItem(BLOCK.get(),new Item.Properties(),"BlockGenericTooltip","furnace_steel"));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SteelFurnaceEntity>> ENTITY=ENTITIES.register("furnace_steel",()->BlockEntityType.Builder.of(SteelFurnaceEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<SteelFurnaceMenu>> MENU=MENUS.register("furnace_steel",()->new MenuType<>(SteelFurnaceMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus){Multiblocks.addInventoryProxyBlock(BLOCK);BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(SteelFurnaceContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
    public static Map<String,Item> indexedItems(){return Map.of("furnace_steel",ITEM.get());}
    public static List<ItemStack> allStacks(){return List.of(new ItemStack(ITEM.get()));}
    public static List<ItemStack> creativeStacks(String tab){return "machine".equals(tab)?allStacks():List.of();}
    private SteelFurnaceContent(){}
}

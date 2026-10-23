package com.hbm.machine.reactor.rbmk.boiler;
import com.hbm.item.BasicBlockItem;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.*;
public final class RbmkBoilerContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<RbmkBoilerBlock> BOILER=BLOCKS.register("rbmk_boiler",RbmkBoilerBlock::new);
    public static final DeferredBlock<RbmkLoaderBlock> LOADER=BLOCKS.register("rbmk_loader",RbmkLoaderBlock::new);
    public static final DeferredBlock<RbmkReasimPortBlock> INLET=BLOCKS.register("rbmk_steam_inlet",()->new RbmkReasimPortBlock(false)),OUTLET=BLOCKS.register("rbmk_steam_outlet",()->new RbmkReasimPortBlock(true));
    public static final DeferredItem<BlockItem> BOILER_ITEM=ITEMS.registerSimpleBlockItem(BOILER);
    public static final DeferredItem<BasicBlockItem> LOADER_ITEM=ITEMS.register("rbmk_loader",()->new BasicBlockItem(LOADER.get(),new Item.Properties(),"BlockGenericTooltip","rbmk_loader"));
    public static final DeferredItem<BasicBlockItem> INLET_ITEM=ITEMS.register("rbmk_steam_inlet",()->new BasicBlockItem(INLET.get(),new Item.Properties(),"BlockGenericTooltip","rbmk_steam_inlet")),OUTLET_ITEM=ITEMS.register("rbmk_steam_outlet",()->new BasicBlockItem(OUTLET.get(),new Item.Properties(),"BlockGenericTooltip","rbmk_steam_outlet"));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RbmkBoilerEntity>> ENTITY=ENTITIES.register("rbmk_boiler",()->BlockEntityType.Builder.of(RbmkBoilerEntity::new,BOILER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RbmkBoilerProxy>> PROXY=ENTITIES.register("rbmk_boiler_port",()->BlockEntityType.Builder.of(RbmkBoilerProxy::new,BOILER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RbmkReasimPort>> REASIM_PORT=ENTITIES.register("rbmk_reasim_port",()->BlockEntityType.Builder.of(RbmkReasimPort::new,INLET.get(),OUTLET.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<RbmkBoilerMenu>> MENU=MENUS.register("rbmk_boiler",()->IMenuTypeExtension.create(RbmkBoilerMenu::new));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);RbmkBoilerEffects.register(bus);RbmkBoilerControl.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(BOILER_ITEM.get()),new ItemStack(LOADER_ITEM.get()),new ItemStack(INLET_ITEM.get()),new ItemStack(OUTLET_ITEM.get())):List.of();}
    private RbmkBoilerContent(){}
}

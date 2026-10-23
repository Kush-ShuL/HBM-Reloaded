package com.hbm.world.structure.templates.funnel;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
/** The original machine_funnel: item compression, without a fluid or energy interface. */
public final class FunnelContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<FunnelBlock> BLOCK=BLOCKS.register("machine_funnel",FunnelBlock::new);
    public static final DeferredItem<FunnelItem> ITEM=ITEMS.register("machine_funnel",()->new FunnelItem(BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FunnelBlockEntity>> ENTITY=ENTITIES.register("machine_funnel",()->BlockEntityType.Builder.of(FunnelBlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<FunnelMenu>> MENU=MENUS.register("machine_funnel",()->new MenuType<>((IContainerFactory<FunnelMenu>)FunnelMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(FunnelContent::capabilities);bus.addListener(FunnelControl::register);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    private FunnelContent(){}
}

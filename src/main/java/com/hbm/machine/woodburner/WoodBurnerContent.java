package com.hbm.machine.woodburner;
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
public final class WoodBurnerContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<WoodBurnerBlock> BLOCK=BLOCKS.register("machine_wood_burner",WoodBurnerBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<WoodBurnerBlockEntity>> ENTITY=ENTITIES.register("wood_burner",()->BlockEntityType.Builder.of(WoodBurnerBlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<WoodBurnerInventoryProxy>> INVENTORY_PROXY=ENTITIES.register("wood_burner_inventory_proxy",()->BlockEntityType.Builder.of(WoodBurnerInventoryProxy::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<WoodBurnerPort>> PORT=ENTITIES.register("wood_burner_port",()->BlockEntityType.Builder.of(WoodBurnerPort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<WoodBurnerMenu>> MENU=MENUS.register("wood_burner",()->new MenuType<>((IContainerFactory<WoodBurnerMenu>)WoodBurnerMenu::new,FeatureFlags.DEFAULT_FLAGS));
    private WoodBurnerContent() {}
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); MENUS.register(bus); bus.addListener(WoodBurnerContent::capabilities); }
    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,INVENTORY_PROXY.get(),(proxy,side)->side==null?new InvWrapper(proxy):new SidedInvWrapper(proxy,side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PORT.get(),(proxy,side)->side==null?new InvWrapper(proxy):new SidedInvWrapper(proxy,side));
    }
    public static List<ItemStack> creativeItems() { return List.of(new ItemStack(ITEM.get())); }
}

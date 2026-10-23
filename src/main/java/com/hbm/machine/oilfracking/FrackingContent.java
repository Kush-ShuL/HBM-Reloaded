package com.hbm.machine.oilfracking;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
public final class FrackingContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> BES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<FrackingBlock> BLOCK=BLOCKS.register("machine_fracking_tower",FrackingBlock::new);
    public static final DeferredItem<FrackingItem> ITEM=ITEMS.register("machine_fracking_tower",()->new FrackingItem(BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FrackingEntity>> ENTITY=BES.register("machine_fracking_tower",()->BlockEntityType.Builder.of(FrackingEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FrackingPort>> PORT=BES.register("fracking_port",()->BlockEntityType.Builder.of(FrackingPort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<FrackingMenu>> MENU=MENUS.register("machine_fracking_tower",()->new MenuType<>((IContainerFactory<FrackingMenu>)FrackingMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);BES.register(bus);MENUS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    private FrackingContent(){}
}

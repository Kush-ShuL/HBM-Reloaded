package com.hbm.missile.launchpad.rusted;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
public final class RustedLaunchPadContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<RustedLaunchPadBlock> BLOCK=BLOCKS.register("launch_pad_rusted",RustedLaunchPadBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem("launch_pad_rusted",BLOCK);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RustedLaunchPadBlockEntity>> ENTITY=ENTITIES.register("launch_pad_rusted",()->BlockEntityType.Builder.of(RustedLaunchPadBlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<RustedLaunchPadMenu>> MENU=MENUS.register("launch_pad_rusted",()->new MenuType<>((IContainerFactory<RustedLaunchPadMenu>)RustedLaunchPadMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return "missile".equals(tab)?List.of(new ItemStack(ITEM.get())):List.of();}
    private RustedLaunchPadContent(){}
}

package com.hbm.machine.launchpadsoyuz;

import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

/** True independent large launchpad, not a second registration of the admitted deprecated SoyuzLauncher. */
public final class LaunchpadSoyuzContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> TILES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<LaunchpadSoyuzBlock> BLOCK=BLOCKS.register("launchpad_soyuz",LaunchpadSoyuzBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem("launchpad_soyuz",BLOCK);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<LaunchpadSoyuzEntity>> ENTITY=TILES.register("launchpad_soyuz",()->BlockEntityType.Builder.of(LaunchpadSoyuzEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<LaunchpadSoyuzPort>> PORT=TILES.register("launchpad_soyuz_port",()->BlockEntityType.Builder.of(LaunchpadSoyuzPort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<LaunchpadSoyuzMenu>> MENU=MENUS.register("launchpad_soyuz",()->new MenuType<>((IContainerFactory<LaunchpadSoyuzMenu>)LaunchpadSoyuzMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);TILES.register(bus);MENUS.register(bus);bus.addListener(LaunchpadSoyuzControl::register);bus.addListener(LaunchpadSoyuzContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->new SidedInvWrapper(machine,side==null?Direction.DOWN:side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PORT.get(),(machine,side)->new SidedInvWrapper(machine,side==null?Direction.DOWN:side));
    }
    public static List<ItemStack> creativeStacks(String tab){return "missile".equals(tab)?List.of(new ItemStack(ITEM.get())):List.of();}
    private LaunchpadSoyuzContent(){}
}

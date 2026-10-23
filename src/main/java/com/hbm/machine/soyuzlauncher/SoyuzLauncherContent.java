package com.hbm.machine.soyuzlauncher;

import com.hbm.entity.rocket.EntityRocketSoyuz;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

/** The two real active deprecated blocks and the missing ascending carrier only. Existing Soyuz items/capsule are reused. */
public final class SoyuzLauncherContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> TILES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<SoyuzLauncherBlock> LAUNCHER=BLOCKS.register("soyuz_launcher",SoyuzLauncherBlock::new);
    public static final DeferredBlock<SoyuzStructureBlock> STRUCTURE=BLOCKS.register("struct_soyuz_core",SoyuzStructureBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem("soyuz_launcher",LAUNCHER),STRUCTURE_ITEM=ITEMS.registerSimpleBlockItem("struct_soyuz_core",STRUCTURE);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SoyuzLauncherBlockEntity>> ENTITY=TILES.register("soyuz_launcher",()->BlockEntityType.Builder.of(SoyuzLauncherBlockEntity::new,LAUNCHER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SoyuzLauncherPort>> PORT=TILES.register("soyuz_launcher_port",()->BlockEntityType.Builder.of(SoyuzLauncherPort::new,LAUNCHER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SoyuzStructureEntity>> STRUCTURE_ENTITY=TILES.register("soyuz_structure",()->BlockEntityType.Builder.of(SoyuzStructureEntity::new,STRUCTURE.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<SoyuzLauncherMenu>> MENU=MENUS.register("soyuz_launcher",()->new MenuType<>((IContainerFactory<SoyuzLauncherMenu>)SoyuzLauncherMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<EntityType<?>,EntityType<EntityRocketSoyuz>> ROCKET=ENTITIES.register("entity_soyuz",()->EntityType.Builder.<EntityRocketSoyuz>of(EntityRocketSoyuz::new,MobCategory.MISC).sized(5,50).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_soyuz"));
    public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);TILES.register(bus);MENUS.register(bus);ENTITIES.register(bus);bus.addListener(SoyuzLauncherSync::register);bus.addListener(SoyuzLauncherContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){
        // Source ISidedInventory exposes no accessible slots and rejects insertion/extraction, including unsided automation.
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->new SidedInvWrapper(machine,side==null?Direction.DOWN:side));
    }
    public static List<ItemStack> creativeStacks(String tab){return "missile".equals(tab)?List.of(new ItemStack(ITEM.get()),new ItemStack(STRUCTURE_ITEM.get())):List.of();}
    private SoyuzLauncherContent(){}
}

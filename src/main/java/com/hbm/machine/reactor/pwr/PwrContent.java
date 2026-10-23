package com.hbm.machine.reactor.pwr;

import com.hbm.registry.HbmContent;
import java.util.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

/** Only missing real registrations: existing plain components, fuel pillar/hot/spent enums remain authoritative. */
public final class PwrContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<Block> CONTROL=BLOCKS.register("pwr_control",PwrContent::pillar),CHANNEL=BLOCKS.register("pwr_channel",PwrContent::pillar);
    public static final DeferredBlock<PwrControllerBlock> CONTROLLER=BLOCKS.register("pwr_controller",PwrControllerBlock::new);
    public static final DeferredBlock<PwrAssemblyBlock> ASSEMBLY=BLOCKS.register("pwr_block",PwrAssemblyBlock::new);
    public static final DeferredItem<PwrComponentItem> CONTROL_ITEM=ITEMS.register("pwr_control",()->new PwrComponentItem(CONTROL.get(),"pwr_control")),CHANNEL_ITEM=ITEMS.register("pwr_channel",()->new PwrComponentItem(CHANNEL.get(),"pwr_channel")),CONTROLLER_ITEM=ITEMS.register("pwr_controller",()->new PwrComponentItem(CONTROLLER.get(),"pwr_controller"));
    public static final DeferredItem<BlockItem> ASSEMBLY_ITEM=ITEMS.registerSimpleBlockItem(ASSEMBLY);
    public static final DeferredItem<PwrFuelItem> FUEL=ITEMS.register("pwr_fuel",PwrFuelItem::new);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PwrControllerBlockEntity>> ENTITY=ENTITIES.register("pwr_controller",()->BlockEntityType.Builder.of(PwrControllerBlockEntity::new,CONTROLLER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PwrAssemblyBlockEntity>> PART=ENTITIES.register("pwr_block",()->BlockEntityType.Builder.of(PwrAssemblyBlockEntity::new,ASSEMBLY.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<PwrMenu>> MENU=MENUS.register("pwr_controller",()->new MenuType<>((IContainerFactory<PwrMenu>)PwrMenu::new,FeatureFlags.DEFAULT_FLAGS));
    private static Block pillar(){return new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops());}
    public static Block block(String name){return switch(name){case "pwr_control"->CONTROL.get();case "pwr_channel"->CHANNEL.get();case "pwr_controller"->CONTROLLER.get();case "pwr_block"->ASSEMBLY.get();default->BuiltInRegistries.BLOCK.getOptional(id(name)).orElseThrow(()->new IllegalStateException("PWR requires real source block "+name));};}
    public static ItemStack spent(int metadata){var item=BuiltInRegistries.ITEM.getOptional(id("pwr_fuel_hot")).orElseThrow(()->new IllegalStateException("PWR requires real pwr_fuel_hot enum"));var stack=new ItemStack(item);stack.set(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),metadata);return stack;}
    public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(PwrContent::capabilities);bus.addListener(PwrPackets::register);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PART.get(),(part,side)->part.isPort()?(side==null?new InvWrapper(part):new SidedInvWrapper(part,side)):null);}
    public static List<ItemStack> creativeStacks(String tab){if(tab.equals("machine"))return List.of(new ItemStack(CONTROL_ITEM.get()),new ItemStack(CHANNEL_ITEM.get()),new ItemStack(CONTROLLER_ITEM.get()));if(tab.equals("control"))return Arrays.stream(PwrFuelType.values()).map(FUEL.get()::stack).toList();return List.of();}
    private PwrContent(){}
}

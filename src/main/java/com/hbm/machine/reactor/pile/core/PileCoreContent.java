package com.hbm.machine.reactor.pile.core;
import java.util.*;import java.util.function.Consumer;import net.minecraft.core.registries.Registries;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.entity.Entity;import net.minecraft.world.item.*;import net.minecraft.world.level.block.*;import net.minecraft.world.level.block.entity.*;import net.minecraft.world.level.block.state.properties.IntegerProperty;import net.neoforged.bus.api.IEventBus;import net.neoforged.neoforge.capabilities.*;import net.neoforged.neoforge.items.wrapper.*;import net.neoforged.neoforge.registries.*;
/** Three actual X5846 MK2 pile blocks; no surrogate PWR or plate-fuel registration. */
public final class PileCoreContent {
 public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,11);
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredBlock<PileBrickBlock> BRICK=BLOCKS.register("pile_brick",PileBrickBlock::new);public static final DeferredBlock<PileBodyBlock> BODY=BLOCKS.register("pile_block",PileBodyBlock::new);public static final DeferredBlock<PileDeviceBlock> DEVICE=BLOCKS.register("pile_device",PileDeviceBlock::new);
 public static final DeferredItem<BlockItem> BRICK_ITEM=ITEMS.registerSimpleBlockItem(BRICK),BODY_ITEM=ITEMS.registerSimpleBlockItem(BODY);public static final DeferredItem<PileDeviceItem> DEVICE_ITEM=ITEMS.register("pile_device",()->new PileDeviceItem(DEVICE.get()));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<TileEntityPileCore>> CORE=ENTITIES.register("pile_core",()->BlockEntityType.Builder.of(TileEntityPileCore::new,BODY.get()).build(null));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<TileEntityPileBaseMK2>> PART=ENTITIES.register("pile_block",()->BlockEntityType.Builder.of(TileEntityPileBaseMK2::new,BODY.get()).build(null));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<TileEntityPileLoader>> LOADER=ENTITIES.register("pile_loader",()->BlockEntityType.Builder.of(TileEntityPileLoader::new,DEVICE.get()).build(null));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<TileEntityPileVent>> VENT=ENTITIES.register("pile_vent",()->BlockEntityType.Builder.of(TileEntityPileVent::new,DEVICE.get()).build(null));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<TileEntityPileControl>> CONTROL=ENTITIES.register("pile_control",()->BlockEntityType.Builder.of(TileEntityPileControl::new,DEVICE.get()).build(null));
 public static Consumer<Entity> CLIENT_FIRE=bullet->{};
 public static void register(IEventBus bus){PileHandDrillContent.register(bus);BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);bus.addListener(PileCoreContent::capabilities);Objects.requireNonNull(TileEntityPileCore.pile_debris);}
 private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,LOADER.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
 public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
 public static List<ItemStack> creativeStacks(String tab){if(!tab.equals("machine"))return PileHandDrillContent.creativeStacks(tab);return List.of(new ItemStack(BRICK_ITEM.get()),DEVICE_ITEM.get().stack(0),DEVICE_ITEM.get().stack(1),DEVICE_ITEM.get().stack(2));}
 private PileCoreContent(){}
}

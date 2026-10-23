package com.hbm.block.light.floodlight;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** The actual two source identities: native-HE Floodlight and its owned endpoint light-space host. */
public final class FloodlightContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredBlock<FloodlightBlock> BLOCK=BLOCKS.register("floodlight",FloodlightBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
 public static final DeferredBlock<FloodlightBeamBlock> BEAM=BLOCKS.register("floodlight_beam",FloodlightBeamBlock::new);public static final DeferredItem<BlockItem> BEAM_ITEM=ITEMS.registerSimpleBlockItem(BEAM);
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FloodlightBlockEntity>> ENTITY=ENTITIES.register("floodlight",()->BlockEntityType.Builder.of(FloodlightBlockEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FloodlightBeamEntity>> BEAM_ENTITY=ENTITIES.register("floodlight_beam",()->BlockEntityType.Builder.of(FloodlightBeamEntity::new,BEAM.get()).build(null));
 public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(new ItemStack(ITEM.get())):List.of();}
 private FloodlightContent(){}
}

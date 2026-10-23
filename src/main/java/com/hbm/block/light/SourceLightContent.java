package com.hbm.block.light;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Six real source on/off hosts plus their real union-of-directions luminous air-space block. */
public final class SourceLightContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final Map<SpotlightKind,DeferredBlock<SpotlightBlock>> ON=new EnumMap<>(SpotlightKind.class),OFF=new EnumMap<>(SpotlightKind.class);public static final Map<String,DeferredItem<BlockItem>> ITEM=new LinkedHashMap<>();
 static {for(var kind:SpotlightKind.values())for(boolean on:new boolean[]{true,false}){var block=BLOCKS.register(kind.id(on),()->new SpotlightBlock(kind,on));(on?ON:OFF).put(kind,block);ITEM.put(kind.id(on),ITEMS.registerSimpleBlockItem(block));}}
 public static final DeferredBlock<SpotlightBeamBlock> BEAM=BLOCKS.register("spotlight_beam",SpotlightBeamBlock::new);
 public static final DeferredItem<BlockItem> BEAM_ITEM=ITEMS.registerSimpleBlockItem(BEAM);
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SpotlightBeamEntity>> BEAM_ENTITY=ENTITIES.register("spotlight_beam",()->BlockEntityType.Builder.of(SpotlightBeamEntity::new,BEAM.get()).build(null));
 public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?Arrays.stream(SpotlightKind.values()).map(kind->new ItemStack(ON.get(kind).get())).toList():List.of();}
 private SourceLightContent(){}
}

package com.hbm.block.structurebasic;
import com.hbm.hazard.HbmHazards;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.*;
public final class BasicStructureContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredBlock<SourceReedsBlock> REEDS=BLOCKS.register("reeds",SourceReedsBlock::new);
 public static final DeferredBlock<SourceVinylTileBlock> VINYL=BLOCKS.register("vinyl_tile",SourceVinylTileBlock::new);
 public static final DeferredBlock<MarkedConcreteBlock> MARKED=BLOCKS.register("brick_concrete_marked",MarkedConcreteBlock::new);
 public static final DeferredItem<BlockItem> REEDS_ITEM=ITEMS.register("reeds",()->new BlockItem(REEDS.get(),new Item.Properties()));
 public static final DeferredItem<VinylTileItem> VINYL_ITEM=ITEMS.register("vinyl_tile",()->new VinylTileItem(VINYL.get()));
 public static final DeferredItem<MarkedConcreteItem> MARKED_ITEM=ITEMS.register("brick_concrete_marked",()->new MarkedConcreteItem(MARKED.get()));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ReedsRenderEntity>> REEDS_ENTITY=ENTITIES.register("source_reeds_render",()->BlockEntityType.Builder.of(ReedsRenderEntity::new,REEDS.get()).build(null));
 public static void register(IEventBus bus,ModContainer container){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);container.registerConfig(ModConfig.Type.CLIENT,ReedsRenderConfig.SPEC,"hbm-reeds-client.toml");}
 public static ItemStack vinyl(int metadata){var stack=new ItemStack(VINYL_ITEM.get());stack.set(HbmHazards.LEGACY_METADATA.get(),metadata);return stack;}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(new ItemStack(REEDS_ITEM.get()),vinyl(0),vinyl(1),new ItemStack(MARKED_ITEM.get())):List.of();}
 private BasicStructureContent(){}
}

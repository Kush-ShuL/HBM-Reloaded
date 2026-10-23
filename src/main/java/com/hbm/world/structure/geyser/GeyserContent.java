package com.hbm.world.structure.geyser;
import com.hbm.entity.particle.EntityOrangeFX;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Actual two source BlockGeysir hosts and the real Agent Orange entity; eight source sprite items already exist. */
public final class GeyserContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> TILES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
 public static final DeferredBlock<GeyserBlock> CHLORINE=BLOCKS.register("geysir_chlorine",()->new GeyserBlock(false)),NETHER=BLOCKS.register("geysir_nether",()->new GeyserBlock(true));
 public static final DeferredItem<BlockItem> CHLORINE_ITEM=ITEMS.registerSimpleBlockItem(CHLORINE),NETHER_ITEM=ITEMS.registerSimpleBlockItem(NETHER);
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<GeyserBlockEntity>> ENTITY=TILES.register("geysir",()->BlockEntityType.Builder.of(GeyserBlockEntity::new,CHLORINE.get(),NETHER.get()).build(null));
 public static final DeferredHolder<EntityType<?>,EntityType<EntityOrangeFX>> ORANGE=ENTITIES.register("entity_agent_orange",()->EntityType.Builder.<EntityOrangeFX>of(EntityOrangeFX::new,MobCategory.MISC).sized(.2F,.2F).clientTrackingRange(63).updateInterval(1).build("hbm:entity_agent_orange"));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);TILES.register(bus);ENTITIES.register(bus);}
 private GeyserContent(){}
}

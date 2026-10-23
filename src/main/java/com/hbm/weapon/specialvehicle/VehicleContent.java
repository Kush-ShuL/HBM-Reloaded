package com.hbm.weapon.specialvehicle;

import com.hbm.block.specialvehicle.*;
import com.hbm.entity.specialvehicle.*;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Only the two actual source DecoBlocks and their falling entities are registered. */
public final class VehicleContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> TILES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredBlock<VehicleDecoBlock> BOXCAR=BLOCKS.register("boxcar",VehicleDecoBlock::new),BOAT=BLOCKS.register("boat",VehicleDecoBlock::new);
    public static final DeferredItem<BlockItem> BOXCAR_ITEM=ITEMS.registerSimpleBlockItem("boxcar",BOXCAR),BOAT_ITEM=ITEMS.registerSimpleBlockItem("boat",BOAT);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<VehicleDecoBlockEntity>> TILE=TILES.register("deco_vehicle",()->BlockEntityType.Builder.of(VehicleDecoBlockEntity::new,BOXCAR.get(),BOAT.get()).build(null));
    public static final DeferredHolder<EntityType<?>,EntityType<EntityBoxcar>> BOXCAR_ENTITY=ENTITIES.register("entity_boxcar",()->EntityType.Builder.<EntityBoxcar>of(EntityBoxcar::new,MobCategory.MISC).sized(.25F,.25F).fireImmune().clientTrackingRange(63).updateInterval(1).build("hbm:entity_boxcar"));
    public static final DeferredHolder<EntityType<?>,EntityType<EntityDuchessGambit>> BOAT_ENTITY=ENTITIES.register("entity_duchessgambit",()->EntityType.Builder.<EntityDuchessGambit>of(EntityDuchessGambit::new,MobCategory.MISC).sized(.25F,.25F).fireImmune().clientTrackingRange(63).updateInterval(1).build("hbm:entity_duchessgambit"));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);TILES.register(bus);ENTITIES.register(bus);bus.addListener(VehicleShockEffects::register);}
    public static List<ItemStack> creativeStacks(String tab){return "block".equals(tab)?List.of(new ItemStack(BOXCAR_ITEM.get()),new ItemStack(BOAT_ITEM.get())):List.of();}
    private VehicleContent(){}
}

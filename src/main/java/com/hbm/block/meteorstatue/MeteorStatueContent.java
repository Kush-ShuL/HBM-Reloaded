package com.hbm.block.meteorstatue;
import com.hbm.item.BasicBlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Exact statue_elb_f only; no creative tab existed in the source and none is added here. */
public final class MeteorStatueContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
 public static final DeferredBlock<MeteorStatueBlock> STATUE=BLOCKS.register("statue_elb_f",MeteorStatueBlock::new);
 public static final DeferredItem<BasicBlockItem> ITEM=ITEMS.register("statue_elb_f",()->new BasicBlockItem(STATUE.get(),new Item.Properties(),"DecoBlockAlt","statue_elb_f"));
 public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<MeteorStatueBlockEntity>> ENTITY=ENTITIES.register("statue_elb_f",()->BlockEntityType.Builder.of(MeteorStatueBlockEntity::new,STATUE.get()).build(null));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
 private MeteorStatueContent(){}
}

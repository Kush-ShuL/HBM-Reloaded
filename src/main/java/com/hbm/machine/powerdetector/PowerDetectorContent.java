package com.hbm.machine.powerdetector;

import com.hbm.item.BasicBlockItem;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class PowerDetectorContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<PowerDetectorBlock> BLOCK=BLOCKS.register("machine_detector",PowerDetectorBlock::new);
    public static final DeferredItem<BasicBlockItem> ITEM=ITEMS.register("machine_detector",()->new BasicBlockItem(BLOCK.get(),new Item.Properties(),"PowerDetector","machine_detector"));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PowerDetectorBlockEntity>> ENTITY=ENTITIES.register("machine_detector",()->BlockEntityType.Builder.of(PowerDetectorBlockEntity::new,BLOCK.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    private PowerDetectorContent(){}
}

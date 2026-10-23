package com.hbm.machine.airintake;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class AirIntakeContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<AirIntakeBlock> BLOCK=BLOCKS.register("machine_intake",AirIntakeBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem("machine_intake",BLOCK);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<AirIntakeBlockEntity>> ENTITY=ENTITIES.register("machine_intake",()->BlockEntityType.Builder.of(AirIntakeBlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<AirIntakeProxy>> PROXY=ENTITIES.register("machine_intake_proxy",()->BlockEntityType.Builder.of(AirIntakeProxy::new,BLOCK.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    private AirIntakeContent(){}
}

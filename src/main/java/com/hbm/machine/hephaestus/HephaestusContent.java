package com.hbm.machine.hephaestus;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class HephaestusContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<HephaestusBlock> BLOCK=BLOCKS.register("machine_hephaestus",HephaestusBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem("machine_hephaestus",BLOCK);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<HephaestusEntity>> ENTITY=ENTITIES.register("hephaestus",()->BlockEntityType.Builder.of(HephaestusEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<HephaestusPort>> PORT=ENTITIES.register("hephaestus_port",()->BlockEntityType.Builder.of(HephaestusPort::new,BLOCK.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    private HephaestusContent(){}
}

package com.hbm.machine.bigasstank;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.registries.*;

/** The actual16,000,000mB multiblock; the older BAT9000 has a different source identity. */
public final class BigAssTankContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<BigAssTankBlock> BLOCK=BLOCKS.register("machine_bigasstank",BigAssTankBlock::new);
    public static final DeferredItem<BigAssTankItem> ITEM=ITEMS.register("machine_bigasstank",()->new BigAssTankItem(BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<BigAssTankBlockEntity>> ENTITY=ENTITIES.register("big_ass_tank",()->BlockEntityType.Builder.of(BigAssTankBlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<BigAssTankPort>> PORT=ENTITIES.register("big_ass_tank_port",()->BlockEntityType.Builder.of(BigAssTankPort::new,BLOCK.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);bus.addListener((RegisterCapabilitiesEvent event)->event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side)));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    private BigAssTankContent(){}
}

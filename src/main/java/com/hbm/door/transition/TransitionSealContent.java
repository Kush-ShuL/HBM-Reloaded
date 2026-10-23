package com.hbm.door.transition;

import com.hbm.door.generic.*;
import java.util.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Original26x24 TRANSITION_SEAL on the only existing GenericDoor block/entity/state/permissions backend. */
public final class TransitionSealContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<GenericDoorBlock> BLOCK=BLOCKS.register("transition_seal",()->new GenericDoorBlock(GenericDoorDeclaration.TRANSITION_SEAL));
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem("transition_seal",BLOCK);
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return "machine".equals(tab)?List.of(new ItemStack(ITEM.get())):List.of();}
    public static Block[] withHost(Block[] previous){var blocks=Arrays.copyOf(previous,previous.length+1);blocks[previous.length]=BLOCK.get();return blocks;}
    private TransitionSealContent(){}
}

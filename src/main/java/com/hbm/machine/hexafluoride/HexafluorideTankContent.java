package com.hbm.machine.hexafluoride;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Original two hidden retained tank blocks, two distinct actual empty BE identities, no fluid/HE capability. */
public final class HexafluorideTankContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<HexafluorideTankBlock> UF6=BLOCKS.register("machine_uf6_tank",()->new HexafluorideTankBlock(false));
    public static final DeferredBlock<HexafluorideTankBlock> PUF6=BLOCKS.register("machine_puf6_tank",()->new HexafluorideTankBlock(true));
    public static final DeferredItem<BlockItem> UF6_ITEM=ITEMS.registerSimpleBlockItem(UF6);
    public static final DeferredItem<BlockItem> PUF6_ITEM=ITEMS.registerSimpleBlockItem(PUF6);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<HexafluorideTankEntity>> UF6_ENTITY=ENTITIES.register("machine_uf6_tank",()->BlockEntityType.Builder.of(HexafluorideTankEntity::new,UF6.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<HexafluorideTankEntity>> PUF6_ENTITY=ENTITIES.register("machine_puf6_tank",()->BlockEntityType.Builder.of(HexafluorideTankEntity::new,PUF6.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
    public static List<ItemStack> allStacks(){return List.of(new ItemStack(UF6_ITEM.get()),new ItemStack(PUF6_ITEM.get()));}
    public static List<ItemStack> creativeStacks(String tab){return List.of();}
    private HexafluorideTankContent(){}
}

package com.hbm.block.bobble;

import com.hbm.hazard.HbmHazards;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Original BlockBobble/ItemBlockMeta: no gameplay effects are attached to the figurines. */
public final class BobbleContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<BobbleBlock> BLOCK=BLOCKS.register("bobblehead",BobbleBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem("bobblehead",BLOCK);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<BobbleBlockEntity>> ENTITY=ENTITIES.register("bobblehead",()->BlockEntityType.Builder.of(BobbleBlockEntity::new,BLOCK.get()).build(null));
    /** Installed only by the actual client subscriber; no Minecraft client class is linked on a server. */
    public static Consumer<BobbleBlockEntity> openScreen=ignored->{};
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
    public static ItemStack stack(int type){ItemStack stack=new ItemStack(ITEM.get());stack.set(HbmHazards.LEGACY_METADATA.get(),type);return stack;}
    public static BobbleType type(ItemStack stack){return BobbleType.fromMetadata(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0));}
    public static List<ItemStack> creativeStacks(String tab){if(!"block".equals(tab))return List.of();List<ItemStack> result=new ArrayList<>();for(int i=1;i<BobbleType.values().length;i++)result.add(stack(i));return result;}
    private BobbleContent(){}
}

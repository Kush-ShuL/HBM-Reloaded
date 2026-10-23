package com.hbm.world.oldlantern;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** The actual hidden, still-registered ModBlocks.lantern_behemoth; no invented lamp inventory or power network. */
public final class OldLanternContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<OldLanternBlock> BLOCK=BLOCKS.register("lantern_behemoth",OldLanternBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.register("lantern_behemoth",()->new BlockItem(BLOCK.get(),new Item.Properties()){
        @Override public Component getName(ItemStack stack){return Component.translatable("tile.lantern_behemoth.name");}
    });
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<OldLanternEntity>> ENTITY=ENTITIES.register("lantern_behemoth",()->BlockEntityType.Builder.of(OldLanternEntity::new,BLOCK.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);bus.addListener(OldLanternPacket::register);}
    public static List<ItemStack> creativeStacks(String tab){return List.of();}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(ITEM.get()));}
    public static List<ItemStack> allStacks(){return indexedItems();}
    private OldLanternContent(){}
}

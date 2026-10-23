package com.hbm.bomb.fireworks;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class FireworksContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> TILES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredBlock<FireworksBlock> BLOCK=BLOCKS.register("fireworks",FireworksBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.register("fireworks",()->new BlockItem(BLOCK.get(),new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FireworksBlockEntity>> TILE=TILES.register("fireworks",()->BlockEntityType.Builder.of(FireworksBlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<EntityType<?>,EntityType<EntityFireworks>> BALL=ENTITIES.register("entity_firework_ball",()->EntityType.Builder.<EntityFireworks>of(EntityFireworks::new,MobCategory.MISC).sized(.6F,1.8F).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_firework_ball"));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);TILES.register(bus);ENTITIES.register(bus);FireworksEffects.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("nuke")?List.of(new ItemStack(ITEM.get())):List.of();}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(ITEM.get()));}
    private FireworksContent(){}
}

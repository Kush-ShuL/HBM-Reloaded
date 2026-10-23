package com.hbm.bomb.fissure;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class FissureBombContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredBlock<FissureBombBlock> BLOCK=BLOCKS.register("fissure_bomb",FissureBombBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.register("fissure_bomb",()->new BlockItem(BLOCK.get(),new Item.Properties()));
    /** Internal modern waiting carrier, not an invented source projectile or replacement blast. */
    public static final DeferredHolder<EntityType<?>,EntityType<FissureDetonation>> PENDING=ENTITIES.register("fissure_detonation_pending",()->EntityType.Builder.<FissureDetonation>of(FissureDetonation::new,MobCategory.MISC).sized(0,0).fireImmune().clientTrackingRange(1).updateInterval(Integer.MAX_VALUE).build("hbm:fissure_detonation_pending"));
    private FissureBombContent(){}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("nuke")?List.of(new ItemStack(ITEM.get())):List.of();}
}

package com.hbm.machine.generator.steamengine;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class SteamEngineContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<SteamEngineBlock> BLOCK=BLOCKS.register("machine_steam_engine",SteamEngineBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.register("machine_steam_engine",()->new SteamEngineItem(BLOCK.get(),new Item.Properties()));
    public static final DeferredItem<LargeGearItem> GEAR=ITEMS.register("gear_large",LargeGearItem::new);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SteamEngineBlockEntity>> ENTITY=ENTITIES.register("machine_steam_engine",()->BlockEntityType.Builder.of(SteamEngineBlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SteamEngineProxy>> PROXY=ENTITIES.register("machine_steam_engine_proxy",()->BlockEntityType.Builder.of(SteamEngineProxy::new,BLOCK.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):tab.equals("parts")?GEAR.get().creativeStacks():List.of();}
    private SteamEngineContent(){}
}

package com.hbm.block.pylon;

import com.hbm.energy.wire.PylonConfig;
import com.hbm.energy.wire.WiringItem;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PylonContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final EnumMap<PylonKind,DeferredBlock<Block>> BLOCKS_BY_KIND=new EnumMap<>(PylonKind.class);
    public static final EnumMap<PylonKind,DeferredItem<PylonBlockItem>> ITEMS_BY_KIND=new EnumMap<>(PylonKind.class);
    static { for(PylonKind kind:PylonKind.values()) { DeferredBlock<Block> block=BLOCKS.register(kind.id,()->kind.connector()?new ConnectorBlock(kind):new PylonBlock(kind)); BLOCKS_BY_KIND.put(kind,block); ITEMS_BY_KIND.put(kind,ITEMS.register(kind.id,()->new PylonBlockItem(block.get(),kind))); } }
    public static final DeferredItem<WiringItem> WIRING=ITEMS.register("wiring_red_copper",WiringItem::new);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PylonBlockEntity>> ENTITY=ENTITIES.register("pylon",()->BlockEntityType.Builder.of(PylonBlockEntity::new,BLOCKS_BY_KIND.values().stream().map(DeferredBlock::get).toArray(Block[]::new)).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PylonPortBlockEntity>> PORT_ENTITY=ENTITIES.register("pylon_conductor_port",()->BlockEntityType.Builder.of(PylonPortBlockEntity::new,BLOCKS_BY_KIND.get(PylonKind.SUBSTATION).get()).build(null));
    private PylonContent() {}
    public static void register(IEventBus bus,ModContainer container) { BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); container.registerConfig(ModConfig.Type.CLIENT,PylonConfig.SPEC,"hbm-wires-client.toml"); }
    public static List<ItemStack> creativeStacks(String tab) {
        var stacks=new ArrayList<ItemStack>();
        if(tab.equals("machine")) for(var item:ITEMS_BY_KIND.values()) stacks.add(new ItemStack(item.get()));
        if(tab.equals("parts")) stacks.add(new ItemStack(WIRING.get()));
        return List.copyOf(stacks);
    }
}

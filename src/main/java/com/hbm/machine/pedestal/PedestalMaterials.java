package com.hbm.machine.pedestal;
import java.util.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Actual otherwise-unported materials of the source legendary gun rituals. */
public final class PedestalMaterials {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<SourceDungeonChainBlock> CHAIN=BLOCKS.register("dungeon_chain",SourceDungeonChainBlock::new);
    public static final DeferredBlock<SourceBarbedWireBlock> WIRE=BLOCKS.register("barbed_wire",SourceBarbedWireBlock::new);
    public static final DeferredItem<BlockItem> CHAIN_ITEM=ITEMS.registerSimpleBlockItem(CHAIN),WIRE_ITEM=ITEMS.registerSimpleBlockItem(WIRE);
    public static final DeferredItem<PedestalCardItem> AOS=ITEMS.register("card_aos",()->new PedestalCardItem(false)),QOS=ITEMS.register("card_qos",()->new PedestalCardItem(true));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);PedestalCardItem.register();}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(new ItemStack(CHAIN_ITEM.get()),new ItemStack(WIRE_ITEM.get())):List.of();}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(CHAIN_ITEM.get()),new ItemStack(WIRE_ITEM.get()),new ItemStack(AOS.get()),new ItemStack(QOS.get()));}
    private PedestalMaterials(){}
}

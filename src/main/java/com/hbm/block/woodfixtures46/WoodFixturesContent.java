package com.hbm.block.woodfixtures46;

import java.util.List;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The two actual X5846 ISBRH wood fixtures; neither has a block entity. */
public final class WoodFixturesContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredBlock<WoodBarrierBlock> BARRIER = BLOCKS.register("wood_barrier", WoodBarrierBlock::new);
    public static final DeferredBlock<WoodStructureBlock> STRUCTURE = BLOCKS.register("wood_structure", WoodStructureBlock::new);
    public static final DeferredItem<BlockItem> BARRIER_ITEM = ITEMS.registerSimpleBlockItem(BARRIER);
    private static final List<DeferredItem<WoodStructureItem>> STRUCTURE_ITEMS=structureItems();
    private static List<DeferredItem<WoodStructureItem>> structureItems(){
        var result=new java.util.ArrayList<DeferredItem<WoodStructureItem>>();String[] names={"roof","scaffold","ceiling"};
        for(int i=0;i<names.length;i++){int type=i;result.add(ITEMS.register("wood_structure_"+names[i],()->new WoodStructureItem(STRUCTURE.get(),type)));}
        return List.copyOf(result);
    }
    public static WoodStructureItem structureItem(int type){return STRUCTURE_ITEMS.get(WoodStructureBlock.type(type)).get();}

    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); }
    public static List<ItemStack> creativeStacks(String tab) {
        return tab.equals("block") ? List.of(new ItemStack(BARRIER_ITEM.get()), WoodStructureItem.stack(0), WoodStructureItem.stack(1), WoodStructureItem.stack(2)) : List.of();
    }
    private WoodFixturesContent() {}
}

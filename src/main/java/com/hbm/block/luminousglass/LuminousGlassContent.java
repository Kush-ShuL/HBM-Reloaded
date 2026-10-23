package com.hbm.block.luminousglass;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The three actual source luminous glass IDs, source machineTab order and ordinary ItemBlock identities. */
public final class LuminousGlassContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final List<String> NAMES=List.of("glass_uranium","glass_trinitite","glass_polonium");
    public static final Map<String,DeferredBlock<LuminousGlassBlock>> GLASS=new LinkedHashMap<>();
    private static final Map<String,DeferredItem<BlockItem>> GLASS_ITEMS=new LinkedHashMap<>();
    static{for(String name:NAMES){var block=BLOCKS.register(name,LuminousGlassBlock::new);GLASS.put(name,block);GLASS_ITEMS.put(name,ITEMS.registerSimpleBlockItem(name,block));}}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?NAMES.stream().map(name->new ItemStack(GLASS_ITEMS.get(name).get())).toList():List.of();}
    private LuminousGlassContent(){}
}

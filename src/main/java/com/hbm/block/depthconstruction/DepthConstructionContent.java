package com.hbm.block.depthconstruction;

import com.hbm.block.ore.LegacyOreBlock;
import com.hbm.block.ore.LegacyOreItem;
import com.hbm.block.ore.OreSpec;
import java.util.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Actual five BlockDepth registrations on the sole existing IDepthRockTool/lore/drop backend. */
public final class DepthConstructionContent {
    public static final List<String> NAMES=List.of("depth_brick","depth_tiles","depth_nether_brick","depth_nether_tiles","depth_dnt");
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final Map<String,DeferredBlock<LegacyOreBlock>> BLOCK_MAP=new LinkedHashMap<>();
    public static final Map<String,DeferredItem<LegacyOreItem>> ITEM_MAP=new LinkedHashMap<>();
    static{
        for(String name:NAMES){
            OreSpec source=new OreSpec(name,"BlockDepth","rock","stone",-1,name.equals("depth_dnt")?36000:6,0,true,true,
                null,null,0,1,0,"NONE",0,List.of(),"COMMON",false,0,true,false);
            var block=BLOCKS.register(name,()->new LegacyOreBlock(source));BLOCK_MAP.put(name,block);
            ITEM_MAP.put(name,ITEMS.register(name,()->new LegacyOreItem(block.get())));
        }
    }
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?ITEM_MAP.values().stream().map(item->new ItemStack(item.get())).toList():List.of();}
    private DepthConstructionContent(){}
}

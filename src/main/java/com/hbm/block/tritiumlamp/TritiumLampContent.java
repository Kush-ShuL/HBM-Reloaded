package com.hbm.block.tritiumlamp;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Source registers all four IDs, exposes only OFF green then blue, and preserves normal block items for the hidden ON IDs. */
public final class TritiumLampContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final Map<String,DeferredBlock<TritiumLampBlock>> LAMPS=new LinkedHashMap<>();
    private static final Map<String,DeferredItem<BlockItem>> LAMP_ITEMS=new LinkedHashMap<>();
    static{for(String color:List.of("green","blue"))for(boolean on:List.of(false,true)){
        String name="lamp_tritium_"+color+(on?"_on":"_off");var block=BLOCKS.register(name,()->new TritiumLampBlock(color.equals("blue"),on));LAMPS.put(name,block);LAMP_ITEMS.put(name,ITEMS.registerSimpleBlockItem(name,block));
    }}
    public static TritiumLampBlock block(boolean blue,boolean on){return LAMPS.get("lamp_tritium_"+(blue?"blue":"green")+(on?"_on":"_off")).get();}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(new ItemStack(LAMP_ITEMS.get("lamp_tritium_green_off").get()),new ItemStack(LAMP_ITEMS.get("lamp_tritium_blue_off").get())):List.of();}
    private TritiumLampContent(){}
}

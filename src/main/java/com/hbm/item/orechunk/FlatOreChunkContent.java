package com.hbm.item.orechunk;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
/** Source ordinal is internal selection only; physical inventory stacks contain no metadata component. */
public final class FlatOreChunkContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final List<String> NAMES=List.of("rare","malachite","cryolite","moonstone","ilmenite");
    public static final List<Integer> ORDER=List.of(1,2,4,0,3);
    public static final List<DeferredItem<FlatOreChunkItem>> CHUNKS=NAMES.stream().map(name->ITEMS.register("chunk_ore_"+name,FlatOreChunkItem::new)).toList();
    public static void register(IEventBus bus){ITEMS.register(bus);}
    public static ItemStack stack(int sourceOrdinal,int count){return new ItemStack(CHUNKS.get(sourceOrdinal).get(),count);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("parts")?ORDER.stream().map(i->stack(i,1)).toList():List.of();}
    private FlatOreChunkContent(){}
}

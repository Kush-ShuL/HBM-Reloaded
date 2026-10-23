package com.hbm.block.sellafield;
import com.hbm.hazard.HbmHazards;
import java.util.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class SellafieldContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<SellafieldBlock> BLOCK=BLOCKS.register("sellafield",SellafieldBlock::new);
    public static final DeferredItem<SellafieldItem> ITEM=ITEMS.register("sellafield",()->new SellafieldItem(BLOCK.get()));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static ItemStack stack(int metadata){ItemStack stack=new ItemStack(ITEM.get());stack.set(HbmHazards.LEGACY_METADATA.get(),metadata);return stack;}
    public static int metadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0);}
    public static List<ItemStack> creativeStacks(String tab){if(!"block".equals(tab))return List.of();List<ItemStack> result=new ArrayList<>();for(int i=0;i<6;i++)result.add(stack(i));return result;}
    private SellafieldContent(){}
}

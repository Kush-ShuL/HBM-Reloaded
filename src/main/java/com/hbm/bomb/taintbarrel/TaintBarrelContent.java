package com.hbm.bomb.taintbarrel;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.*;

/** Actual single RedBarrel(false) block/item. The unused TileEntityRedBarrel is deliberately not registered. */
public final class TaintBarrelContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<TaintBarrelBlock> BARREL=BLOCKS.register("taint_barrel",TaintBarrelBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.register("taint_barrel",()->new BlockItem(BARREL.get(),new Item.Properties()){
        @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);lines.add(Component.translatable("tooltip.hbm.barrel.static"));}
    });
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);NeoForge.EVENT_BUS.addListener(TaintBarrelBurst::loaded);NeoForge.EVENT_BUS.addListener(TaintBarrelBurst::tick);NeoForge.EVENT_BUS.addListener(TaintBarrelBurst::unload);NeoForge.EVENT_BUS.addListener(TaintBarrelBurst::stopped);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("nuke")?List.of(new ItemStack(ITEM.get())):List.of();}
    private TaintBarrelContent(){}
}

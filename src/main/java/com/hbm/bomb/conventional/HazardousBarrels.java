package com.hbm.bomb.conventional;

import java.util.List;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class HazardousBarrels {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<HazardousBarrelBlock> RED=barrel("red_barrel",HazardousBarrelBlock.Kind.RED),PINK=barrel("pink_barrel",HazardousBarrelBlock.Kind.PINK),LOX=barrel("lox_barrel",HazardousBarrelBlock.Kind.LOX),YELLOW=barrel("yellow_barrel",HazardousBarrelBlock.Kind.YELLOW),VITRIFIED=barrel("vitrified_barrel",HazardousBarrelBlock.Kind.VITRIFIED);
    private HazardousBarrels(){}
    private static DeferredBlock<HazardousBarrelBlock> barrel(String id,HazardousBarrelBlock.Kind kind){var block=BLOCKS.register(id,()->new HazardousBarrelBlock(kind));ITEMS.register(id,()->new BlockItem(block.get(),new Item.Properties()){@Override public void appendHoverText(ItemStack stack,TooltipContext context,java.util.List<net.minecraft.network.chat.Component> lines,net.minecraft.world.item.TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);if(kind!=HazardousBarrelBlock.Kind.YELLOW&&kind!=HazardousBarrelBlock.Kind.VITRIFIED)lines.add(net.minecraft.network.chat.Component.translatable("tooltip.hbm.barrel.static"));}});return block;}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("nuke")?List.of(new ItemStack(RED.get()),new ItemStack(PINK.get()),new ItemStack(LOX.get()),new ItemStack(YELLOW.get()),new ItemStack(VITRIFIED.get())):List.of();}
}

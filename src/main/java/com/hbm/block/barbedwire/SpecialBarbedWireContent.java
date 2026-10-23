package com.hbm.block.barbedwire;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
/** Five genuinely functioning source variants only; ordinary barbed_wire remains the unique existing pedestal material. */
public final class SpecialBarbedWireContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final EnumMap<SpecialBarbedWireBlock.Kind,DeferredBlock<SpecialBarbedWireBlock>> WIRES=new EnumMap<>(SpecialBarbedWireBlock.Kind.class);
    static{for(var kind:SpecialBarbedWireBlock.Kind.values()){var block=BLOCKS.register(kind.id(),()->new SpecialBarbedWireBlock(kind));WIRES.put(kind,block);ITEMS.registerSimpleBlockItem(block);}}
    private SpecialBarbedWireContent(){}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){if(!tab.equals("block"))return List.of();var result=new ArrayList<ItemStack>();for(var block:WIRES.values())result.add(new ItemStack(block.get()));return result;}
}

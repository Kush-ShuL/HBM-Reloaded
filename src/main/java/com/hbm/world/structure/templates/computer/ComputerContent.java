package com.hbm.world.structure.templates.computer;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Source BlockDecoModel(Material.iron,DecoComputerEnum.class,true,false); the source enum contains exactly IBM_300PL. */
public final class ComputerContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<ComputerBlock> BLOCK=BLOCKS.register("deco_computer",ComputerBlock::new);
    public static final DeferredItem<ComputerItem> ITEM=ITEMS.register("deco_computer",()->new ComputerItem(BLOCK.get()));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(new ItemStack(ITEM.get())):List.of();}
    private ComputerContent(){}
}

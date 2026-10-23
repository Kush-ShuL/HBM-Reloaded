package com.hbm.world.structure.materials;

import com.hbm.block.ore.LegacyOreBlock;
import com.hbm.block.ore.LegacyOreItem;
import com.hbm.block.ore.OreSpec;
import java.util.List;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Three actual source blocks used in meteor templates, separate from decorative meteor bricks. */
public final class MeteorMaterialContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<LegacyOreBlock> METEOR=BLOCKS.register("block_meteor",()->new LegacyOreBlock(spec("block_meteor",null)));
    public static final DeferredBlock<LegacyOreBlock> COBBLE=BLOCKS.register("block_meteor_cobble",()->new LegacyOreBlock(spec("block_meteor_cobble","fragment_meteorite")));
    public static final DeferredBlock<NtmDirtBlock> DIRT=BLOCKS.register("ntm_dirt",NtmDirtBlock::new);
    public static final DeferredItem<LegacyOreItem> METEOR_ITEM=ITEMS.register("block_meteor",()->new LegacyOreItem(METEOR.get()));
    public static final DeferredItem<LegacyOreItem> COBBLE_ITEM=ITEMS.register("block_meteor_cobble",()->new LegacyOreItem(COBBLE.get()));
    public static final DeferredItem<BlockItem> DIRT_ITEM=ITEMS.registerSimpleBlockItem("ntm_dirt",DIRT);
    public static final DeferredBlock<ColoredConcreteBlock> COLORED=BLOCKS.register("concrete_colored",ColoredConcreteBlock::new);
    public static final DeferredItem<ColoredConcreteItem> COLORED_ITEM=ITEMS.register("concrete_colored",()->new ColoredConcreteItem(COLORED.get()));
    private static OreSpec spec(String name,String drop){
        //1.7 setResistance360 writes1080 to the field and returns216 to explosion callers.
        return new OreSpec(name,"BlockOre","rock","stone",15,216,0,true,true,drop,null,0,1,0,"NONE",0,List.of(),"COMMON",true,0,false,false);
    }
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){
        if(!tab.equals("block"))return List.of();
        var stacks=new java.util.ArrayList<ItemStack>();stacks.add(new ItemStack(METEOR_ITEM.get()));stacks.add(new ItemStack(COBBLE_ITEM.get()));stacks.addAll(COLORED_ITEM.get().creativeStacks());return stacks;
    }
    private MeteorMaterialContent(){}
}

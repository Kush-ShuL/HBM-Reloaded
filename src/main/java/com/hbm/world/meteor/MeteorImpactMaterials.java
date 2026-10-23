package com.hbm.world.meteor;

import com.hbm.block.ore.*;
import java.util.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Actual remaining impact materials, sharing the already ported meteor/cobble/ore identities. */
public final class MeteorImpactMaterials {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredBlock<LegacyOreBlock> BROKEN=BLOCKS.register("block_meteor_broken",()->new LegacyOreBlock(spec("block_meteor_broken","fragment_meteorite",3,0)));
 public static final DeferredBlock<MoltenMeteorBlock> MOLTEN=BLOCKS.register("block_meteor_molten",MoltenMeteorBlock::new);
 public static final DeferredBlock<MeteorTreasureBlock> TREASURE=BLOCKS.register("block_meteor_treasure",MeteorTreasureBlock::new);
 public static final DeferredItem<LegacyOreItem> BROKEN_ITEM=ITEMS.register("block_meteor_broken",()->new LegacyOreItem(BROKEN.get()));
 public static final DeferredItem<LegacyOreItem> MOLTEN_ITEM=ITEMS.register("block_meteor_molten",()->new LegacyOreItem(MOLTEN.get()));
 public static final DeferredItem<BlockItem> TREASURE_ITEM=ITEMS.registerSimpleBlockItem("block_meteor_treasure",TREASURE);
 static OreSpec spec(String name,String drop,int quantityRandom,int light){return new OreSpec(name,"BlockOre","rock","stone",15,216,light,true,true,drop,null,0,1,quantityRandom,"NONE",0,List.of(),"COMMON",true,0,false,false);}
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(new ItemStack(BROKEN_ITEM.get()),new ItemStack(MOLTEN_ITEM.get()),new ItemStack(TREASURE_ITEM.get())):List.of();}
 private MeteorImpactMaterials(){}
}

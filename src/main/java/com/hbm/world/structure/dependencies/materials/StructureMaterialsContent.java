package com.hbm.world.structure.dependencies.materials;
import java.util.List;
import com.hbm.registry.HbmOreDictionary;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.*;
public final class StructureMaterialsContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredBlock<FusionHeaterBlock> HEATER=BLOCKS.register("fusion_heater",FusionHeaterBlock::new);
 public static final DeferredBlock<SlagBlock> SLAG=BLOCKS.register("block_slag",SlagBlock::new);
 public static final DeferredItem<BlockItem> HEATER_ITEM=ITEMS.registerSimpleBlockItem("fusion_heater",HEATER);
 public static final DeferredItem<BlockItem> SLAG_ITEM=ITEMS.registerSimpleBlockItem("block_slag",SLAG);
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);bus.addListener(StructureMaterialsContent::setup);}
 private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->HbmOreDictionary.registerName("blockSlag"));}
 public static List<ItemStack> allStacks(){return List.of(new ItemStack(HEATER_ITEM.get()),new ItemStack(SLAG_ITEM.get()));}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(new ItemStack(SLAG_ITEM.get())):List.of();}
 private StructureMaterialsContent(){}
}

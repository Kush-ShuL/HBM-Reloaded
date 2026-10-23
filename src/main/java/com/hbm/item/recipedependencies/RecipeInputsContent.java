package com.hbm.item.recipedependencies;
import java.util.List;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Only previously unregistered source identities; the existing meteorite sword content is extended by a narrow patch. */
public final class RecipeInputsContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
 public static final DeferredItem<FortuneUpgradeItem>FORTUNE_1=ITEMS.register("upgrade_fortune_1",()->new FortuneUpgradeItem(1)),FORTUNE_2=ITEMS.register("upgrade_fortune_2",()->new FortuneUpgradeItem(2)),FORTUNE_3=ITEMS.register("upgrade_fortune_3",()->new FortuneUpgradeItem(3));
 public static final DeferredItem<GlyphidFoodItem>RAW=ITEMS.register("glyphid_meat",()->new GlyphidFoodItem(false)),GRILLED=ITEMS.register("glyphid_meat_grilled",()->new GlyphidFoodItem(true));
 public static final DeferredBlock<ReinforcedLaminateBlock>LAMINATE=BLOCKS.register("reinforced_laminate",ReinforcedLaminateBlock::new);
 public static final DeferredItem<ReinforcedLaminateItem>LAMINATE_ITEM=ITEMS.register("reinforced_laminate",()->new ReinforcedLaminateItem(LAMINATE.get()));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);bus.addListener((net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event)->event.enqueueWork(()->com.hbm.machine.assembler.AssemblerProcessingHooks.register((machine,inventory)->{if(inventory[0].is(com.hbm.item.meteoritesword.MeteoriteSwordContent.ALLOYED.get())){inventory[0]=new ItemStack(com.hbm.item.meteoritesword.MeteoriteSwordContent.MACHINED.get());machine.setChanged();}})));}
 public static List<ItemStack>creativeStacks(String tab){return switch(tab){case "control"->List.of(new ItemStack(FORTUNE_1.get()),new ItemStack(FORTUNE_2.get()),new ItemStack(FORTUNE_3.get()));case "consumable"->List.of(new ItemStack(RAW.get()),new ItemStack(GRILLED.get()));case "block"->List.of(new ItemStack(LAMINATE_ITEM.get()));default->List.of();};}
 private RecipeInputsContent(){}
}

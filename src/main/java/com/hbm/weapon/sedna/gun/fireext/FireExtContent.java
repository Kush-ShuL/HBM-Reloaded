package com.hbm.weapon.sedna.gun.fireext;
import java.util.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.*;
/** Unique real gun/ammo and two original BlockLayering registrations; full foam/sand_mix are existing source blocks. */
public final class FireExtContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
 public static final DeferredItem<ItemGunBaseNT> GUN=ITEMS.register("gun_fireext",FireExtFactory::create);
 public static final DeferredItem<FireExtAmmoItem> AMMO=ITEMS.register("ammo_fireext",FireExtAmmoItem::new);
 public static final DeferredBlock<ExtinguishingLayerBlock> FOAM_LAYER=BLOCKS.register("foam_layer",()->new ExtinguishingLayerBlock(true));
 public static final DeferredBlock<ExtinguishingLayerBlock> BORON_LAYER=BLOCKS.register("sand_boron_layer",()->new ExtinguishingLayerBlock(false));
 public static final DeferredItem<BlockItem> FOAM_ITEM=ITEMS.registerSimpleBlockItem("foam_layer",FOAM_LAYER),BORON_ITEM=ITEMS.registerSimpleBlockItem("sand_boron_layer",BORON_LAYER);
 public static void register(IEventBus bus,ModContainer container){FireExtBullets.init();BLOCKS.register(bus);ITEMS.register(bus);container.registerConfig(ModConfig.Type.SERVER,FireExtSettings.SPEC,"hbm-fire-extinguisher.toml");}
 public static List<ItemStack> creativeStacks(String tab){if(tab.equals("weapon")){var result=new ArrayList<ItemStack>();result.add(new ItemStack(GUN.get()));result.addAll(AMMO.get().creativeStacks());return result;}if(tab.equals("block"))return List.of(new ItemStack(FOAM_ITEM.get()),new ItemStack(BORON_ITEM.get()));return List.of();}
 public static List<ItemStack> indexedItems(){var result=new ArrayList<ItemStack>(creativeStacks("weapon"));result.addAll(creativeStacks("block"));return result;}
 private FireExtContent(){}
}

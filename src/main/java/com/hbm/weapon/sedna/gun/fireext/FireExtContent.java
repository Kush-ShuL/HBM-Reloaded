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
 private static final List<DeferredItem<FireExtAmmoItem>> AMMO_ITEMS=registerAmmo();
 private static List<DeferredItem<FireExtAmmoItem>> registerAmmo(){var items=new ArrayList<DeferredItem<FireExtAmmoItem>>();var names=List.of("ammo_fireext_water","ammo_fireext_foam","ammo_fireext_sand");for(int i=0;i<names.size();i++){int type=i;items.add(ITEMS.register(names.get(i),()->new FireExtAmmoItem(type)));}return List.copyOf(items);}
 public static FireExtAmmoItem ammo(int type){return AMMO_ITEMS.get(type).get();}
 public static List<ItemStack> ammoStacks(){return AMMO_ITEMS.stream().map(holder->new ItemStack(holder.get())).toList();}
 public static final DeferredBlock<ExtinguishingLayerBlock> FOAM_LAYER=BLOCKS.register("foam_layer",()->new ExtinguishingLayerBlock(true));
 public static final DeferredBlock<ExtinguishingLayerBlock> BORON_LAYER=BLOCKS.register("sand_boron_layer",()->new ExtinguishingLayerBlock(false));
 public static final DeferredItem<BlockItem> FOAM_ITEM=ITEMS.registerSimpleBlockItem("foam_layer",FOAM_LAYER),BORON_ITEM=ITEMS.registerSimpleBlockItem("sand_boron_layer",BORON_LAYER);
 public static void register(IEventBus bus,ModContainer container){FireExtBullets.init();BLOCKS.register(bus);ITEMS.register(bus);container.registerConfig(ModConfig.Type.SERVER,FireExtSettings.SPEC,"hbm-fire-extinguisher.toml");}
 public static List<ItemStack> creativeStacks(String tab){if(tab.equals("weapon")){var result=new ArrayList<ItemStack>();result.add(new ItemStack(GUN.get()));result.addAll(ammoStacks());return result;}if(tab.equals("block"))return List.of(new ItemStack(FOAM_ITEM.get()),new ItemStack(BORON_ITEM.get()));return List.of();}
 public static List<ItemStack> indexedItems(){var result=new ArrayList<ItemStack>(creativeStacks("weapon"));result.addAll(creativeStacks("block"));return result;}
 private FireExtContent(){}
}

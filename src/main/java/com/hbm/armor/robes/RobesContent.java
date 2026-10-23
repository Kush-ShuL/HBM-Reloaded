package com.hbm.armor.robes;
import java.util.List;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;
/** Source ModItemsArmor104..107: four plain ArmorFSB pieces, with no invented full-set potion or resistance. */
public final class RobesContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 private static final DeferredRegister<ArmorMaterial> MATERIALS=DeferredRegister.create(Registries.ARMOR_MATERIAL,"hbm");
 public static final DeferredHolder<ArmorMaterial,ArmorMaterial> MATERIAL=MATERIALS.register("robes",()->{
  var chain=ArmorMaterials.CHAIN.value();
  return new ArmorMaterial(chain.defense(),chain.enchantmentValue(),BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY),chain.repairIngredient(),List.of(new ArmorMaterial.Layer(ResourceLocation.parse("hbm:robes"))),0,0);
 });
 public static final DeferredItem<RobesArmorItem> HELMET=ITEMS.register("robes_helmet",()->new RobesArmorItem(MATERIAL,ArmorItem.Type.HELMET));
 public static final DeferredItem<RobesArmorItem> PLATE=ITEMS.register("robes_plate",()->new RobesArmorItem(MATERIAL,ArmorItem.Type.CHESTPLATE));
 public static final DeferredItem<RobesArmorItem> LEGS=ITEMS.register("robes_legs",()->new RobesArmorItem(MATERIAL,ArmorItem.Type.LEGGINGS));
 public static final DeferredItem<RobesArmorItem> BOOTS=ITEMS.register("robes_boots",()->new RobesArmorItem(MATERIAL,ArmorItem.Type.BOOTS));
 public static final DeferredItem<ItemRag> RAG=ITEMS.register("rag",ItemRag::new);
 public static void register(IEventBus bus){MATERIALS.register(bus);ITEMS.register(bus);bus.addListener(RobesContent::creative);}
 private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey().equals(CreativeModeTabs.COMBAT))for(var item:List.of(HELMET,PLATE,LEGS,BOOTS))event.accept(item.get());}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("parts")?List.of(new ItemStack(RAG.get())):List.of();}
 private RobesContent(){}
}

package com.hbm.equipment.steel;
import com.hbm.registry.HbmContent;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;
public final class SteelEquipmentContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ArmorMaterial> MATERIALS=DeferredRegister.create(Registries.ARMOR_MATERIAL,"hbm");
    public static final DeferredHolder<ArmorMaterial,ArmorMaterial> MATERIAL=MATERIALS.register("steel",()->new ArmorMaterial(Map.of(ArmorItem.Type.HELMET,3,ArmorItem.Type.CHESTPLATE,8,ArmorItem.Type.LEGGINGS,6,ArmorItem.Type.BOOTS,3),5,BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY),()->Ingredient.of(HbmContent.item("ingot_steel")),List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("hbm","steel"))),0,0));
    public static final DeferredItem<SteelArmorItem> HELMET=ITEMS.register("steel_helmet",()->new SteelArmorItem(ArmorItem.Type.HELMET,"steel_helmet"));
    public static final DeferredItem<SteelArmorItem> PLATE=ITEMS.register("steel_plate",()->new SteelArmorItem(ArmorItem.Type.CHESTPLATE,"steel_plate"));
    public static final DeferredItem<SteelArmorItem> LEGS=ITEMS.register("steel_legs",()->new SteelArmorItem(ArmorItem.Type.LEGGINGS,"steel_legs"));
    public static final DeferredItem<SteelArmorItem> BOOTS=ITEMS.register("steel_boots",()->new SteelArmorItem(ArmorItem.Type.BOOTS,"steel_boots"));
    public static void register(IEventBus bus){MATERIALS.register(bus);ITEMS.register(bus);bus.addListener(SteelEquipmentContent::creative);}
    private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey().equals(CreativeModeTabs.COMBAT))for(var item:List.of(HELMET,PLATE,LEGS,BOOTS))event.accept(item.get());}
    private SteelEquipmentContent(){}
}

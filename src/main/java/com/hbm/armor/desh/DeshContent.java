package com.hbm.armor.desh;
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
/** Original ModItemsArmor137..147 material and actual four steamsuit registrations. */
public final class DeshContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<ArmorMaterial> MATERIALS=DeferredRegister.create(Registries.ARMOR_MATERIAL,"hbm");
    public static final DeferredHolder<ArmorMaterial,ArmorMaterial> MATERIAL=MATERIALS.register("desh",()->new ArmorMaterial(Map.of(ArmorItem.Type.HELMET,3,ArmorItem.Type.CHESTPLATE,8,ArmorItem.Type.LEGGINGS,6,ArmorItem.Type.BOOTS,3),0,BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY),()->Ingredient.of(HbmContent.item("ingot_desh")),List.of(new ArmorMaterial.Layer(id("starmetal"))),0,0));
    public static final DeferredItem<ArmorDesh> HELMET=ITEMS.register("steamsuit_helmet",()->new ArmorDesh(ArmorItem.Type.HELMET,"steamsuit_helmet")),PLATE=ITEMS.register("steamsuit_plate",()->new ArmorDesh(ArmorItem.Type.CHESTPLATE,"steamsuit_plate")),LEGS=ITEMS.register("steamsuit_legs",()->new ArmorDesh(ArmorItem.Type.LEGGINGS,"steamsuit_legs")),BOOTS=ITEMS.register("steamsuit_boots",()->new ArmorDesh(ArmorItem.Type.BOOTS,"steamsuit_boots"));
    public static ResourceLocation id(String path){return ResourceLocation.parse("hbm:"+path);}
    public static void register(IEventBus bus){MATERIALS.register(bus);ITEMS.register(bus);bus.addListener(DeshContent::creative);}
    private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey().equals(CreativeModeTabs.COMBAT))for(var item:List.of(HELMET,PLATE,LEGS,BOOTS))event.accept(item.get());}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(HELMET.get()),new ItemStack(PLATE.get()),new ItemStack(LEGS.get()),new ItemStack(BOOTS.get()));}
    public static List<ItemStack> allStacks(){return indexedItems();}
    private DeshContent(){}
}

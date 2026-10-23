package com.hbm.equipment.vaultclothing;
import com.hbm.equipment.steel.SteelEquipmentContent;
import java.util.List;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;
/** Exact four original wardrobe items; existing canonical hazard/radiation/DTDR tables bind their actual registered IDs. */
public final class VaultClothingContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<MonoGasMaskItem> MONO=ITEMS.register("gas_mask_mono",MonoGasMaskItem::new);
    public static final DeferredItem<VaultArmorItem> GOGGLES=ITEMS.register("goggles",()->new VaultArmorItem(ArmorMaterials.IRON,ArmorItem.Type.HELMET,15,"goggles","textures/models/goggles.png"));
    public static final DeferredItem<VaultArmorItem> JACKET=ITEMS.register("jackt",()->new VaultArmorItem(SteelEquipmentContent.MATERIAL,ArmorItem.Type.CHESTPLATE,30,"jackt","textures/armor/jackt.png"));
    public static final DeferredItem<VaultArmorItem> JACKET2=ITEMS.register("jackt2",()->new VaultArmorItem(SteelEquipmentContent.MATERIAL,ArmorItem.Type.CHESTPLATE,30,"jackt2","textures/armor/jackt2.png"));
    public static void register(IEventBus bus){ITEMS.register(bus);bus.addListener(VaultClothingContent::creative);}
    private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey().equals(CreativeModeTabs.COMBAT))for(var item:List.of(MONO,GOGGLES,JACKET,JACKET2))event.accept(item.get());}
    private VaultClothingContent(){}
}

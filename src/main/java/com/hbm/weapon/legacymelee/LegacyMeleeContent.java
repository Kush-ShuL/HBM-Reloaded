package com.hbm.weapon.legacymelee;
import com.hbm.weapon.legacymelee.LegacySpecialWeaponItem.Kind;
import java.util.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;

public final class LegacyMeleeContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<LegacySpecialWeaponItem> HAMMER=special("schrabidium_hammer",Kind.HAMMER),SLEDGE=special("shimmer_sledge",Kind.SLEDGE),AXE=special("shimmer_axe",Kind.AXE),
            CABER=special("ullapool_caber",Kind.CABER),WRENCH_FLIPPED=special("wrench_flipped",Kind.WRENCH_FLIPPED),MEMESPOON=special("memespoon",Kind.MEMESPOON),WOOD_GAVEL=special("wood_gavel",Kind.WOOD_GAVEL),
            STOP=special("stopsign",Kind.STOP),SOP=special("sopsign",Kind.SOP),CHERNOBYL=special("chernobylsign",Kind.CHERNOBYL);
    public static final DeferredItem<LegacyModSwordItem> REER_GRAAR=ITEMS.register("reer_graar",LegacyModSwordItem::new);
    public static final DeferredItem<BigSwordItem> BIG_SWORD=ITEMS.register("big_sword",BigSwordItem::new);
    // Exact X5846 declaration only: source never registers pch or supplies a caller/recipe.
    // Keep Kind.PCH/HAMMER as source-only declaration data; modern Item holders require a real registration owner.
    private static DeferredItem<LegacySpecialWeaponItem> special(String name,Kind kind){return ITEMS.register(name,()->new LegacySpecialWeaponItem(kind,name));}
    public static void register(IEventBus bus){ITEMS.register(bus);bus.addListener(LegacyMeleeContent::creative);}
    private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey().equals(CreativeModeTabs.COMBAT))for(var item:List.of(HAMMER,SLEDGE,AXE,WRENCH_FLIPPED,MEMESPOON,WOOD_GAVEL,STOP,SOP,CHERNOBYL,REER_GRAAR,BIG_SWORD))event.accept(item.get());}
    public static List<ItemStack> weaponCreative(){return List.of(new ItemStack(CABER.get()));}
    private LegacyMeleeContent(){}
}

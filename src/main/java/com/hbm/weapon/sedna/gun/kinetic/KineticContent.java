package com.hbm.weapon.sedna.gun.kinetic;

import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.weapon.sedna.gun.legendary.LegendaryGunFactory;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Only the six ordinary X5846 12ga identities; Broken/Sexy and their ammo remain shared. */
public final class KineticContent {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static boolean initialized;
    static {
        for (String name : List.of("gun_maresleg", "gun_maresleg_akimbo", "gun_liberator", "gun_spas12", "gun_autoshotgun", "gun_autoshotgun_shredder"))
            ITEMS.register(name, () -> gun(name));
    }
    private KineticContent() {}
    private static ItemGunBaseNT gun(String name) {
        if (!initialized) { initialized = true; XFactory12ga.init(); }
        return switch (name) {
            case "gun_maresleg" -> KineticGuns.gun_maresleg;
            case "gun_maresleg_akimbo" -> KineticGuns.gun_maresleg_akimbo;
            case "gun_liberator" -> KineticGuns.gun_liberator;
            case "gun_spas12" -> KineticGuns.gun_spas12;
            case "gun_autoshotgun" -> KineticGuns.gun_autoshotgun;
            case "gun_autoshotgun_shredder" -> KineticGuns.gun_autoshotgun_shredder;
            default -> throw new IllegalArgumentException(name);
        };
    }
    public static void register(IEventBus bus, ModContainer container) {
        XFactory12ga.initConfig();
        KineticSettings.register(container);
        ITEMS.register(bus);
        bus.addListener(KineticContent::setup);
    }
    private static void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            LegendaryGunFactory.initCasingItems();
            // Original submunitions clone the fully initialized ordinary config;
            // modern registry item suppliers become available at this lifecycle point.
            BulletConfig[] original = {XFactory12ga.g12, XFactory12ga.g12_slug, XFactory12ga.g12_flechette,
                    XFactory12ga.g12_magnum, XFactory12ga.g12_explosive, XFactory12ga.g12_phosphorus};
            BulletConfig[] sub = {XFactory12ga.g12_sub, XFactory12ga.g12_sub_slug, XFactory12ga.g12_sub_flechette,
                    XFactory12ga.g12_sub_magnum, XFactory12ga.g12_sub_explosive, XFactory12ga.g12_sub_phosphorus};
            for (int i = 0; i < sub.length; i++) {
                sub[i].casingItem = original[i].casingItem;
                sub[i].casingAmount = original[i].casingAmount;
            }
            KineticModBindings.initialize();
        });
    }
    public static List<ItemStack> creativeStacks(String tab) {
        return tab.equals("weapon") ? List.of(new ItemStack(KineticGuns.gun_maresleg), new ItemStack(KineticGuns.gun_liberator),
                new ItemStack(KineticGuns.gun_spas12), new ItemStack(KineticGuns.gun_autoshotgun)) : List.of();
    }
    public static List<ItemStack> allStacks() {
        return List.of(new ItemStack(KineticGuns.gun_maresleg), new ItemStack(KineticGuns.gun_maresleg_akimbo),
                new ItemStack(KineticGuns.gun_liberator), new ItemStack(KineticGuns.gun_spas12),
                new ItemStack(KineticGuns.gun_autoshotgun), new ItemStack(KineticGuns.gun_autoshotgun_shredder));
    }
    public static List<ItemStack> indexedItems() { return allStacks(); }
}

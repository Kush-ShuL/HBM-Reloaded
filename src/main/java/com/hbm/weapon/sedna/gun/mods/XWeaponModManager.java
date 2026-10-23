package com.hbm.weapon.sedna.gun.mods;

import com.google.common.collect.HashBiMap;
import com.hbm.hazard.HbmHazards;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.mags.IMagazine;
import java.util.*;
import net.minecraft.util.Mth;
import net.minecraft.world.item.*;

/** Actual X5846 ordered upgrade lookup/install/evaluation. Each admitted gun registers only its real source mod definitions. */
public final class XWeaponModManager {
    public static final String KEY_MOD_LIST = "KEY_MOD_LIST_";
    public static final HashBiMap<Integer, IWeaponMod> idToMod = HashBiMap.create();
    public static final Map<ComparableStack, WeaponModDefinition> stackToMod = new HashMap<>();
    public static final Map<IWeaponMod, ItemStack> modToStack = new HashMap<>();
    public static final Comparator<IWeaponMod> modSorter = (first, second) -> second.getModPriority() - first.getModPriority();
    private static Object prevMagType; private static int prevMagCount; private static boolean changedMagState;
    public record ComparableStack(Item item, int metadata) {
        public ComparableStack(ItemStack stack) { this(stack.getItem(), stack.getItem() instanceof com.hbm.item.FlatSourceItem ? 0 : stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0)); }
        public ComparableStack(Item item) { this(item, 0); }
        public ItemStack toStack() { var stack=new ItemStack(item);if(!(item instanceof com.hbm.item.FlatSourceItem))stack.set(HbmHazards.LEGACY_METADATA.get(),metadata);return stack; }
    }
    public static ItemStack[] getUpgradeItems(ItemStack stack, int cfg) {
        int[] ids = GunStackData.data(stack).getIntArray(KEY_MOD_LIST + cfg); var result = new ItemStack[ids.length];
        for (int i = 0; i < ids.length; i++) { var mod = idToMod.get(ids[i]); var item = mod == null ? null : modToStack.get(mod); result[i] = item == null ? ItemStack.EMPTY : item.copy(); }
        return result;
    }
    public static boolean hasUpgrade(ItemStack stack, int cfg, int id) { for (int value : GunStackData.data(stack).getIntArray(KEY_MOD_LIST + cfg)) if (value == id) return true; return false; }
    public static void changedMagState() { changedMagState = true; }
    private static void saveMagState(ItemStack stack, int cfg) { var mag = ((ItemGunBaseNT)stack.getItem()).getConfig(stack, cfg).getReceivers(stack)[0].getMagazine(stack); if (mag != null) { prevMagType = mag.getType(stack, null); prevMagCount = mag.getAmount(stack, null); } }
    private static void restoreMagState(ItemStack stack, int cfg) {
        if (!changedMagState) return; changedMagState = false;
        var mag = ((ItemGunBaseNT)stack.getItem()).getConfig(stack, cfg).getReceivers(stack)[0].getMagazine(stack);
        if (mag != null) mag.setAmount(stack, mag.getType(stack, null) == prevMagType ? Mth.clamp(prevMagCount, 0, mag.getCapacity(stack)) : 0);
    }
    public static void install(ItemStack stack, int cfg, ItemStack... mods) {
        saveMagState(stack, cfg); uninstall(stack, cfg); var selected = new ArrayList<IWeaponMod>(); var gun = new ComparableStack(stack);
        for (var mod : mods) {
            if (mod == null || mod.isEmpty()) continue;
            var def = stackToMod.get(new ComparableStack(mod)); if (def == null) continue;
            var handler = def.modByGun.get(gun); if (handler == null) handler = def.modByGun.get(null); if (handler != null) selected.add(handler);
        }
        if (selected.isEmpty()) return;
        selected.sort(modSorter); int[] ids = new int[selected.size()];
        for (int i = 0; i < ids.length; i++) { var mod = selected.get(i); ids[i] = idToMod.inverse().get(mod); onInstallStack(stack, modToStack.get(mod), cfg); }
        GunStackData.update(stack, data -> data.putIntArray(KEY_MOD_LIST + cfg, ids)); restoreMagState(stack, cfg);
    }
    public static void uninstall(ItemStack stack, int cfg) {
        if (stack == null || !GunStackData.hasData(stack)) return;
        for (var mod : getUpgradeItems(stack, cfg)) onUninstallStack(stack, mod, cfg);
        GunStackData.update(stack, data -> data.remove(KEY_MOD_LIST + cfg));
    }
    public static IWeaponMod modFromStack(ItemStack gun, ItemStack mod, int cfg) {
        if (gun == null || mod == null || mod.isEmpty()) return null;
        var def = stackToMod.get(new ComparableStack(mod)); if (def == null) return null;
        var handler = def.modByGun.get(new ComparableStack(gun)); return handler == null ? def.modByGun.get(null) : handler;
    }
    public static void onInstallStack(ItemStack gun, ItemStack mod, int cfg) { var handler = modFromStack(gun, mod, cfg); if (handler != null) handler.onInstall(gun, mod, cfg); }
    public static void onUninstallStack(ItemStack gun, ItemStack mod, int cfg) { var handler = modFromStack(gun, mod, cfg); if (handler != null) handler.onUninstall(gun, mod, cfg); }
    public static boolean isApplicable(ItemStack gun, ItemStack mod, int cfg, boolean checkMutex) {
        var handler = modFromStack(gun, mod, cfg); if (handler == null) return false;
        if (checkMutex) for (int id : GunStackData.data(gun).getIntArray(KEY_MOD_LIST + cfg)) { var old = idToMod.get(id); if (old != null) for (String a : handler.getSlots()) for (String b : old.getSlots()) if (a.equals(b)) return false; }
        return true;
    }
    public static <T> T eval(T base, ItemStack stack, String key, Object parent, int cfg) {
        if (stack == null || !GunStackData.hasData(stack)) return base;
        for (int id : GunStackData.data(stack).getIntArray(KEY_MOD_LIST + cfg)) { var handler = idToMod.get(id); if (handler != null) base = handler.eval(base, stack, key, parent); }
        return base;
    }
    public static final class WeaponModDefinition {
        public final HashMap<ComparableStack, IWeaponMod> modByGun = new HashMap<>(); public final ItemStack stack;
        public WeaponModDefinition(ItemStack stack) { this.stack = stack.copy(); stackToMod.put(new ComparableStack(stack), this); }
        public WeaponModDefinition addMod(ItemStack gun, IWeaponMod mod) { return addMod(new ComparableStack(gun), mod); }
        public WeaponModDefinition addMod(Item gun, IWeaponMod mod) { return addMod(new ComparableStack(gun), mod); }
        public WeaponModDefinition addMod(Item[] guns, IWeaponMod mod) { for (Item gun : guns) addMod(gun, mod); return this; }
        public WeaponModDefinition addMod(ComparableStack gun, IWeaponMod mod) {
            modByGun.put(gun, mod); modToStack.put(mod, stack);
            if (gun != null) { var item = (ItemGunBaseNT)gun.item(); var upgrade = new ComparableStack(stack); if (!item.recognizedMods.contains(upgrade)) item.recognizedMods.add(upgrade); }
            return this;
        }
        public WeaponModDefinition addDefault(IWeaponMod mod) { return addMod((ComparableStack)null, mod); }
    }
    private XWeaponModManager() {}
}

package com.hbm.world.structure.logic;

import com.hbm.armor.GasMaskFilters;
import com.hbm.entity.mob.ai.EntityAIFireGun;
import com.hbm.machine.pedestal.mixin.MobGoalsAccess;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Actual X5846 pools and equip operations; deliberately independent from StructureLoot's quantity RNG. */
public final class MobUtil {
    public static final Map<Integer, List<WeightedRandomObject>> slotPoolCommonS = pool("slotPoolCommonS");
    public static final Map<Integer, List<WeightedRandomObject>> slotPoolRangedS = pool("slotPoolRangedS");
    public static final Map<Integer, List<WeightedRandomObject>> slotPoolCommon = pool("slotPoolCommon");
    public static final Map<Integer, List<WeightedRandomObject>> slotPoolRanged = pool("slotPoolRanged");
    public static final Map<Integer, List<WeightedRandomObject>> slotPoolAdv = pool("slotPoolAdv");
    public static final Map<Integer, List<WeightedRandomObject>> slotPoolAdvRanged = advancedRanged();
    public static final HashMap<Double, List<WeightedRandomObject>> slotPoolGuns = sootGuns();
    public static final Map<Integer, List<WeightedRandomObject>> slotPoolGunsTier1 = pool("slotPoolGunsTier1");
    public static final Map<Integer, List<WeightedRandomObject>> slotPoolGunsTier2 = pool("slotPoolGunsTier2");
    public static final Map<Integer, List<WeightedRandomObject>> slotPoolGunsTier3 = pool("slotPoolGunsTier3");
    public static final Map<Integer, List<WeightedRandomObject>> slotPoolMasks = pool("slotPoolMasks");
    public static final Map<Integer, List<WeightedRandomObject>> slotPoolHelms = pool("slotPoolHelms");
    public static final Map<Integer, List<WeightedRandomObject>> slotPoolTierArmor = pool("slotPoolTierArmor");
    public static final Map<Integer, List<WeightedRandomObject>> slotPoolMelee = pool("slotPoolMelee");

    private static List<WeightedRandomObject> entries(com.google.gson.JsonObject row) {
        var result = new ArrayList<WeightedRandomObject>();
        if (!row.get("nullEntryWeight").isJsonNull()) result.add(new WeightedRandomObject(null, row.get("nullEntryWeight").getAsInt()));
        for (var value : row.getAsJsonArray("entries")) {
            var entry = value.getAsJsonObject(); String symbol = entry.get("symbol").getAsString();
            result.add(new WeightedRandomObject(new WeightedRandomObject.SourceItem(symbol, LogicDependencies.itemId(symbol)), entry.get("weight").getAsInt()));
        }
        return result;
    }
    private static Map<Integer, List<WeightedRandomObject>> pool(String name) {
        var result = new HashMap<Integer, List<WeightedRandomObject>>();
        for (var value : LogicDependencies.data().getAsJsonObject("pools").getAsJsonArray(name)) {
            var row = value.getAsJsonObject(); result.put(row.get("slot").getAsInt(), entries(row));
        }
        return result;
    }
    private static Map<Integer, List<WeightedRandomObject>> advancedRanged() {
        var result = new HashMap<>(slotPoolAdv); result.remove(0); return result;
    }
    private static HashMap<Double, List<WeightedRandomObject>> sootGuns() {
        var result = new HashMap<Double, List<WeightedRandomObject>>();
        for (var value : LogicDependencies.data().getAsJsonObject("pools").getAsJsonArray("slotPoolGuns")) {
            var row = value.getAsJsonObject(); result.put(row.get("slot").getAsDouble(), entries(row));
        }
        return result;
    }
    public static List<WeightedRandomObject> createSlotPool(int nullWeight, Object[][] items) {
        var pool = new ArrayList<WeightedRandomObject>(); pool.add(new WeightedRandomObject(null, nullWeight));
        addItems(pool, items); return pool;
    }
    public static List<WeightedRandomObject> createSlotPool(Object[][] items) {
        var pool = new ArrayList<WeightedRandomObject>(); addItems(pool, items); return pool;
    }
    private static void addItems(List<WeightedRandomObject> pool, Object[][] items) {
        for (Object[] row : items) {
            Object value = row[0]; int weight = (int) row[1];
            if (value instanceof Item item) pool.add(new WeightedRandomObject(new ItemStack(item), weight));
            else if (value instanceof ItemStack) pool.add(new WeightedRandomObject(value, weight));
        }
    }
    /** Same one nextInt(total) and subtract-then-negative selection as original WeightedRandom. */
    public static WeightedRandomObject choose(Random random, List<WeightedRandomObject> pool) {
        int total = 0; for (var entry : pool) total += entry.itemWeight;
        if (total <= 0) throw new IllegalArgumentException("Total weight must be > 0");
        int value = random.nextInt(total);
        for (var entry : pool) { value -= entry.itemWeight; if (value < 0) return entry; }
        return null;
    }
    public static void equipFullSet(LivingEntity entity, Item helmet, Item chest, Item legs, Item boots) {
        entity.setItemSlot(EquipmentSlot.HEAD, new ItemStack(helmet)); entity.setItemSlot(EquipmentSlot.CHEST, new ItemStack(chest));
        entity.setItemSlot(EquipmentSlot.LEGS, new ItemStack(legs)); entity.setItemSlot(EquipmentSlot.FEET, new ItemStack(boots));
    }
    public static void assignItemsToEntity(LivingEntity entity, Map<Integer, List<WeightedRandomObject>> slots, Random random) {
        LogicDependencies.requirePool(slots);
        // Install prerequisites are checked before the first selection, not after a partial equip/RNG consumption.
        boolean needsFilter = slots.values().stream().flatMap(List::stream).anyMatch(entry -> entry.item instanceof WeightedRandomObject.SourceItem item && filterMask(item.symbol()));
        if (needsFilter) LogicDependencies.requireItems("gas_mask_filter");
        for (var entry : slots.entrySet()) {
            int slot = entry.getKey(); var pool = entry.getValue(); var choice = choose(random, pool);
            if (choice == null) continue;
            ItemStack stack = choice.asStack(); if (stack.isEmpty()) continue;
            for (String symbol : List.of("gas_mask_m65", "gas_mask_olde", "gas_mask_mono"))
                if (LogicDependencies.hasItem(LogicDependencies.itemId(symbol)) && stack.is(LogicDependencies.item(symbol))) {
                    GasMaskFilters.install(stack, new ItemStack(LogicDependencies.item("gas_mask_filter")), entity.level().registryAccess()); break;
                }
            entity.setItemSlot(slot(slot), stack);
            if (slot == 0 && entity instanceof AbstractSkeleton skeleton && pool == slots.get(0)) addFireTask(skeleton);
        }
    }
    private static boolean filterMask(String symbol) { return symbol.equals("gas_mask_m65") || symbol.equals("gas_mask_olde") || symbol.equals("gas_mask_mono"); }
    public static EquipmentSlot slot(int oldSlot) {
        return switch (oldSlot) { case 0 -> EquipmentSlot.MAINHAND; case 1 -> EquipmentSlot.FEET; case 2 -> EquipmentSlot.LEGS; case 3 -> EquipmentSlot.CHEST; case 4 -> EquipmentSlot.HEAD; default -> throw new IllegalArgumentException("Source equipment slot " + oldSlot); };
    }
    public static void addFireTask(Mob entity) { addFireTask(entity, new EntityAIFireGun(entity)); }
    public static void addFireTask(Mob entity, EntityAIFireGun task) {
        entity.setDropChance(EquipmentSlot.MAINHAND, 0);
        var goals = ((MobGoalsAccess) entity).hbm$goals();
        if (goals.getAvailableGoals().stream().anyMatch(entry -> entry.getGoal() instanceof EntityAIFireGun)) return;
        goals.addGoal(3, task);
    }
    private MobUtil() {}
}

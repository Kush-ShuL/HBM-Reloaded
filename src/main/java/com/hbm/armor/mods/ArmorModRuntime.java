package com.hbm.armor.mods;

import com.hbm.armor.ArmorModItem;
import com.hbm.armor.ArmorModStorage;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/** Original ModEventHandler living-update order; existing hazard/attribute handlers remain authoritative. */
public final class ArmorModRuntime {
    private static final EquipmentSlot[] ORDER = {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};
    private record ModifierKey(Holder<Attribute> attribute, ResourceLocation id) { }
    // Entity.equals/hashCode use its numeric ID, which is shared by different logical-side objects.
    // Keep the source's per-living-instance diff without retaining unloaded entities.
    private static final ReferenceQueue<LivingEntity> COLLECTED = new ReferenceQueue<>();
    private static final Map<LivingIdentity, Map<ModifierKey, AttributeModifier>> PREVIOUS = Collections.synchronizedMap(new HashMap<>());
    private static final class LivingIdentity extends WeakReference<LivingEntity> {
        private final int hash;
        private LivingIdentity(LivingEntity entity, ReferenceQueue<LivingEntity> queue) {
            super(entity, queue);
            hash = System.identityHashCode(entity);
        }
        @Override public int hashCode() { return hash; }
        @Override public boolean equals(Object other) {
            if (this == other) return true;
            var entity = get();
            return entity != null && other instanceof LivingIdentity identity && entity == identity.get();
        }
    }
    public static void register() { NeoForge.EVENT_BUS.addListener(ArmorModRuntime::tick); }
    private static void tick(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof LivingEntity wearer)) return;
        Map<ModifierKey, AttributeModifier> next = null;
        for (var slot : ORDER) {
            var armor = wearer.getItemBySlot(slot);
            if (!ArmorModStorage.hasMods(armor)) continue;
            for (var installed : ArmorModStorage.pryMods(armor, wearer.level().registryAccess())) {
                if (!(installed.getItem() instanceof ArmorModItem item)) continue;
                item.modUpdate(wearer, armor);
                for (var modifier : item.getModifiers(armor).entrySet()) {
                    if (next == null) next = new HashMap<>();
                    next.put(new ModifierKey(modifier.getKey(), modifier.getValue().id()), modifier.getValue());
                }
            }
        }
        applyModifiers(wearer, next == null ? Map.of() : next);
    }
    private static void applyModifiers(LivingEntity wearer, Map<ModifierKey, AttributeModifier> next) {
        for (var stale = COLLECTED.poll(); stale != null; stale = COLLECTED.poll()) PREVIOUS.remove(stale);
        var identity = new LivingIdentity(wearer, null);
        var previous = PREVIOUS.get(identity);
        if (previous == null && next.isEmpty()) return;
        if (previous != null) for (var entry : previous.entrySet()) {
            if (entry.getValue().equals(next.get(entry.getKey()))) continue;
            var attribute = wearer.getAttribute(entry.getKey().attribute());
            if (attribute != null) attribute.removeModifier(entry.getKey().id());
        }
        for (var entry : next.entrySet()) {
            var attribute = wearer.getAttribute(entry.getKey().attribute());
            if (attribute == null) continue;
            var actual = attribute.getModifier(entry.getKey().id());
            if (!entry.getValue().equals(actual)) {
                if (actual != null) attribute.removeModifier(entry.getKey().id());
                attribute.addTransientModifier(entry.getValue());
            }
        }
        if (next.isEmpty()) PREVIOUS.remove(identity);
        else PREVIOUS.put(previous == null ? new LivingIdentity(wearer, COLLECTED) : identity, Map.copyOf(next));
    }
    private ArmorModRuntime() { }
}

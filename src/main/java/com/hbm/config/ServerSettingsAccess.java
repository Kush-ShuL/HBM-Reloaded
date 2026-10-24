package com.hbm.config;

import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Command metadata points to registered values; the native configs remain their only storage. */
public final class ServerSettingsAccess {
    public enum Kind {
        BOOLEAN("Boolean"), INTEGER("Integer"), FLOAT("Float");
        private final String typeName;
        Kind(String typeName) { this.typeName = typeName; }
        public String typeName() { return typeName; }
        private Object parse(String value) {
            return switch (this) {
                case BOOLEAN -> Boolean.parseBoolean(value);
                case INTEGER -> Integer.parseInt(value);
                case FLOAT -> Float.parseFloat(value);
            };
        }
    }
    public record Binding(String key, String fileName, List<String> path, Kind kind, boolean fixedDamage) {}
    private record Resolved(ModConfig config, ModConfigSpec.ConfigValue<?> value) {}
    private static final NavigableMap<String, Binding> VALUES;
    static {
        var values = new TreeMap<String, Binding>();
        bind(values, "autocal.execution.maxClock", "hbm-autocal-server.toml", "execution.maxClock", Kind.INTEGER, false);
        bind(values, "autocal.memory.maxBufferLength", "hbm-autocal-server.toml", "memory.maxBufferLength", Kind.INTEGER, false);
        bind(values, "autocal.memory.maxStackSize", "hbm-autocal-server.toml", "memory.maxStackSize", Kind.INTEGER, false);
        bind(values, "conveyors.congestion.breakBeltEnabled", "hbm-conveyors.toml", "congestion.breakBeltEnabled", Kind.BOOLEAN, false);
        bind(values, "conveyors.congestion.maxOverlappingObjects", "hbm-conveyors.toml", "congestion.maxOverlappingObjects", Kind.INTEGER, false);
        bind(values, "crates.drops.keepContentsEnabled", "hbm-storage-crates-server.toml", "drops.keepContentsEnabled", Kind.BOOLEAN, false);
        bind(values, "crates.interaction.openHeldEnabled", "hbm-storage-crates-server.toml", "interaction.openHeldEnabled", Kind.BOOLEAN, false);
        bind(values, "medicines.effects.mkuEnabled", "hbm-medicines.toml", "effects.mkuEnabled", Kind.BOOLEAN, false);
        bind(values, "medicines.effects.taintTrailsEnabled", "hbm-medicines.toml", "effects.taintTrailsEnabled", Kind.BOOLEAN, false);
        bind(values, "mines.damage.antiPersonnel", "hbm-mines.toml", "damage.antiPersonnel", Kind.FLOAT, false);
        bind(values, "mines.damage.highExplosive", "hbm-mines.toml", "damage.highExplosive", Kind.FLOAT, false);
        bind(values, "mines.damage.naval", "hbm-mines.toml", "damage.naval", Kind.FLOAT, false);
        bind(values, "mines.damage.nuclear", "hbm-mines.toml", "damage.nuclear", Kind.FLOAT, false);
        bind(values, "mines.damage.shrapnel", "hbm-mines.toml", "damage.shrapnel", Kind.FLOAT, false);
        bind(values, "server.damage.compatibilityModeEnabled", "hbm-source-server.toml", "damage.compatibilityModeEnabled", Kind.BOOLEAN, false);
        bind(values, "server.doors.requireRedstoneControl", "hbm-source-server.toml", "doors.requireRedstoneControl", Kind.BOOLEAN, false);
        bind(values, "server.hazards.droppedItemTickInterval", "hbm-source-server.toml", "hazards.droppedItemTickInterval", Kind.INTEGER, false);
        bind(values, "server.network.portDebugParticlesEnabled", "hbm-source-server.toml", "network.portDebugParticlesEnabled", Kind.BOOLEAN, false);
        bind(values, "server.structureAuthoring.previewModeEnabled", "hbm-source-server.toml", "structureAuthoring.previewModeEnabled", Kind.BOOLEAN, false);
        bind(values, "turrets.fritz.damage.fritz", "hbm-turret-fritz-server.toml", "damage.fritz", Kind.FLOAT, false);
        bind(values, "turrets.howard.damage.howard", "hbm-howard-turrets-server.toml", "damage.howard", Kind.FLOAT, false);
        bind(values, "turrets.howard.damage.methusalem", "hbm-howard-turrets-server.toml", "damage.methusalem", Kind.FLOAT, false);
        bind(values, "turrets.jeremy.damage.jeremy", "hbm-jeremy-turret-server.toml", "damage.jeremy", Kind.FLOAT, false);
        bind(values, "turrets.maxwell.damage.maxwell", "hbm-turret-maxwell-server.toml", "damage.maxwell", Kind.FLOAT, false);
        bind(values, "turrets.richard.damage.richard", "hbm-turret-richard-server.toml", "damage.richard", Kind.FLOAT, false);
        bind(values, "turrets.rotary.damage.chekhov", "hbm-rotary-turrets-server.toml", "damage.chekhov", Kind.FLOAT, false);
        bind(values, "turrets.rotary.damage.friendly", "hbm-rotary-turrets-server.toml", "damage.friendly", Kind.FLOAT, false);
        bind(values, "turrets.sentry.damage.brown", "hbm-turrets-server.toml", "damage.brown", Kind.FLOAT, false);
        bind(values, "turrets.sentry.damage.edwin", "hbm-turrets-server.toml", "damage.edwin", Kind.FLOAT, false);
        bind(values, "turrets.tauon.damage.tauon", "hbm-turret-tauon-server.toml", "damage.tauon", Kind.FLOAT, false);
        bind(values, "weapons.aberrator.damage.aberrator", "hbm-sedna-aberrator.toml", "damage.aberrator", Kind.FLOAT, false);
        bind(values, "weapons.aberrator.damage.eottLeft", "hbm-sedna-aberrator.toml", "damage.eottLeft", Kind.FLOAT, false);
        bind(values, "weapons.aberrator.damage.eottRight", "hbm-sedna-aberrator.toml", "damage.eottRight", Kind.FLOAT, false);
        bind(values, "weapons.accelerator.damage.coilgun", "hbm-accelerator-guns-server.toml", "damage.coilgun", Kind.FLOAT, false);
        bind(values, "weapons.accelerator.damage.ni4ni", "hbm-accelerator-guns-server.toml", "damage.ni4ni", Kind.FLOAT, false);
        bind(values, "weapons.accelerator.damage.tauCannon", "hbm-accelerator-guns-server.toml", "damage.tauCannon", Kind.FLOAT, false);
        bind(values, "weapons.accelerator.damage.tauCannonCharge", "hbm-accelerator-guns-server.toml", "damage.tauCannonCharge", Kind.FLOAT, false);
        bind(values, "weapons.chargeThrower.damage.chargeThrower", "hbm-charge-thrower.toml", "damage.chargeThrower", Kind.FLOAT, false);
        bind(values, "weapons.drill.damage.poweredDrill", "hbm-drill-gun-server.toml", "damage.poweredDrill", Kind.FLOAT, false);
        bind(values, "weapons.energy.damage.laserPistol", "hbm-energy-guns-server.toml", "damage.laserPistol", Kind.FLOAT, false);
        bind(values, "weapons.energy.damage.laserRifle", "hbm-energy-guns-server.toml", "damage.laserRifle", Kind.FLOAT, false);
        bind(values, "weapons.energy.damage.morningGlory", "hbm-energy-guns-server.toml", "damage.morningGlory", Kind.FLOAT, false);
        bind(values, "weapons.energy.damage.pewPew", "hbm-energy-guns-server.toml", "damage.pewPew", Kind.FLOAT, false);
        bind(values, "weapons.energy.damage.teslaCannon", "hbm-energy-guns-server.toml", "damage.teslaCannon", Kind.FLOAT, false);
        bind(values, "weapons.extinguisher.damage.extinguisher", "hbm-fire-extinguisher.toml", "damage.extinguisher", Kind.FLOAT, false);
        bind(values, "weapons.fatman.damage.fatman", "hbm-sedna-fatman.toml", "damage.fatman", Kind.FLOAT, false);
        bind(values, "weapons.flamer.damage.daybreaker", "hbm-flamer-guns-server.toml", "damage.daybreaker", Kind.FLOAT, false);
        bind(values, "weapons.flamer.damage.flamer", "hbm-flamer-guns-server.toml", "damage.flamer", Kind.FLOAT, false);
        bind(values, "weapons.flamer.damage.topaz", "hbm-flamer-guns-server.toml", "damage.topaz", Kind.FLOAT, false);
        bind(values, "weapons.flaregun.damage.flare", "hbm-sedna-flaregun.toml", "damage.flare", Kind.FLOAT, false);
        bind(values, "weapons.grenade40.damage.congoLake", "hbm-sedna-40mm.toml", "damage.congoLake", Kind.FLOAT, false);
        bind(values, "weapons.grenade40.damage.mk108", "hbm-sedna-40mm.toml", "damage.mk108", Kind.FLOAT, false);
        bind(values, "weapons.heavyPowder.damage.bolter", "hbm-sedna-heavy-powder.toml", "damage.bolter", Kind.FLOAT, false);
        bind(values, "weapons.heavyPowder.damage.heretic", "hbm-sedna-heavy-powder.toml", "damage.heretic", Kind.FLOAT, false);
        bind(values, "weapons.heavyPowder.damage.sacredDragon", "hbm-sedna-heavy-powder.toml", "damage.sacredDragon", Kind.FLOAT, false);
        bind(values, "weapons.kinetic.damage.autoshotgun", "hbm-sedna-12ga.toml", "damage.autoshotgun", Kind.FLOAT, false);
        bind(values, "weapons.kinetic.damage.liberator", "hbm-sedna-12ga.toml", "damage.liberator", Kind.FLOAT, false);
        bind(values, "weapons.kinetic.damage.maresLeg", "hbm-sedna-12ga.toml", "damage.maresLeg", Kind.FLOAT, false);
        bind(values, "weapons.kinetic.damage.maresLegAkimboLeft", "hbm-sedna-12ga.toml", "damage.maresLegAkimboLeft", Kind.FLOAT, false);
        bind(values, "weapons.kinetic.damage.maresLegAkimboRight", "hbm-sedna-12ga.toml", "damage.maresLegAkimboRight", Kind.FLOAT, false);
        bind(values, "weapons.kinetic.damage.shredder", "hbm-sedna-12ga.toml", "damage.shredder", Kind.FLOAT, false);
        bind(values, "weapons.kinetic.damage.spas", "hbm-sedna-12ga.toml", "damage.spas", Kind.FLOAT, false);
        bind(values, "weapons.legendary.damage.broken", "hbm-sedna-legendary.toml", "damage.broken", Kind.FLOAT, false);
        bind(values, "weapons.legendary.damage.lilmac", "hbm-sedna-legendary.toml", "damage.lilmac", Kind.FLOAT, false);
        bind(values, "weapons.legendary.damage.sexy", "hbm-sedna-legendary.toml", "damage.sexy", Kind.FLOAT, false);
        bind(values, "weapons.missileLauncher.damage.missileLauncher", "hbm-sedna-missile-launcher.toml", "damage.missileLauncher", Kind.FLOAT, false);
        bind(values, "weapons.ncrpa.damage.largeTargetMultiplier", "hbm-ncr-pa-weapons-server.toml", "damage.largeTargetMultiplier", Kind.FLOAT, false);
        bind(values, "weapons.ncrpa.damage.ranged", "hbm-ncr-pa-weapons-server.toml", "damage.ranged", Kind.FLOAT, false);
        bind(values, "weapons.ncrpa.damage.sweep", "hbm-ncr-pa-weapons-server.toml", "damage.sweep", Kind.FLOAT, false);
        bind(values, "weapons.ncrpa.damage.swing", "hbm-ncr-pa-weapons-server.toml", "damage.swing", Kind.FLOAT, false);
        bind(values, "weapons.panzerschreck.damage.panzerschreck", "hbm-sedna-panzerschreck.toml", "damage.panzerschreck", Kind.FLOAT, false);
        bind(values, "weapons.pepperbox.damage.pepperbox", "hbm-sedna-server.toml", "damage.pepperbox", Kind.FLOAT, false);
        bind(values, "weapons.pistols.damage.am180", "hbm-sedna-pistols.toml", "damage.am180", Kind.FLOAT, false);
        bind(values, "weapons.pistols.damage.atlas", "hbm-sedna-pistols.toml", "damage.atlas", Kind.FLOAT, false);
        bind(values, "weapons.pistols.damage.daniCelestial", "hbm-sedna-pistols.toml", "damage.daniCelestial", Kind.FLOAT, false);
        bind(values, "weapons.pistols.damage.daniLunar", "hbm-sedna-pistols.toml", "damage.daniLunar", Kind.FLOAT, false);
        bind(values, "weapons.pistols.damage.greasegun", "hbm-sedna-pistols.toml", "damage.greasegun", Kind.FLOAT, false);
        bind(values, "weapons.pistols.damage.hangman", "hbm-sedna-pistols.toml", "damage.hangman", Kind.FLOAT, false);
        bind(values, "weapons.pistols.damage.hangmanSmack", "hbm-sedna-pistols.toml", "damage.hangmanSmack", Kind.FLOAT, false);
        bind(values, "weapons.pistols.damage.heavyRevolver", "hbm-sedna-pistols.toml", "damage.heavyRevolver", Kind.FLOAT, false);
        bind(values, "weapons.pistols.damage.henry", "hbm-sedna-pistols.toml", "damage.henry", Kind.FLOAT, false);
        bind(values, "weapons.pistols.damage.lightRevolver", "hbm-sedna-pistols.toml", "damage.lightRevolver", Kind.FLOAT, false);
        bind(values, "weapons.pistols.damage.lincoln", "hbm-sedna-pistols.toml", "damage.lincoln", Kind.FLOAT, false);
        bind(values, "weapons.pistols.damage.longPistol", "hbm-sedna-pistols.toml", "damage.longPistol", Kind.FLOAT, false);
        bind(values, "weapons.pistols.damage.protege", "hbm-sedna-pistols.toml", "damage.protege", Kind.FLOAT, false);
        bind(values, "weapons.pistols.damage.starf", "hbm-sedna-pistols.toml", "damage.starf", Kind.FLOAT, false);
        bind(values, "weapons.pistols.damage.starfAkimboLeft", "hbm-sedna-pistols.toml", "damage.starfAkimboLeft", Kind.FLOAT, false);
        bind(values, "weapons.pistols.damage.starfAkimboRight", "hbm-sedna-pistols.toml", "damage.starfAkimboRight", Kind.FLOAT, false);
        bind(values, "weapons.pistols.damage.uzi", "hbm-sedna-pistols.toml", "damage.uzi", Kind.FLOAT, false);
        bind(values, "weapons.pistols.damage.uziAkimboLeft", "hbm-sedna-pistols.toml", "damage.uziAkimboLeft", Kind.FLOAT, false);
        bind(values, "weapons.pistols.damage.uziAkimboRight", "hbm-sedna-pistols.toml", "damage.uziAkimboRight", Kind.FLOAT, false);
        bind(values, "weapons.quadro.damage.quadro", "hbm-sedna-quadro.toml", "damage.quadro", Kind.FLOAT, false);
        bind(values, "weapons.rifle50.damage.antiMateriel", "hbm-sedna-50.toml", "damage.antiMateriel", Kind.FLOAT, false);
        bind(values, "weapons.rifle50.damage.m2", "hbm-sedna-50.toml", "damage.m2", Kind.FLOAT, false);
        bind(values, "weapons.rifle50.damage.penance", "hbm-sedna-50.toml", "damage.penance", Kind.FLOAT, false);
        bind(values, "weapons.rifle50.damage.subtlety", "hbm-sedna-50.toml", "damage.subtlety", Kind.FLOAT, false);
        // 此变量可读取与保存；当前突击步枪使用固定伤害，不参与伤害计算。
        bind(values, "weapons.rifle556.damage.assaultRifle", "hbm-sedna-556.toml", "damage.assaultRifle", Kind.FLOAT, true);
        bind(values, "weapons.rifle556.damage.stg77", "hbm-sedna-556.toml", "damage.stg77", Kind.FLOAT, false);
        bind(values, "weapons.rifle556.damage.zebra", "hbm-sedna-556.toml", "damage.zebra", Kind.FLOAT, false);
        bind(values, "weapons.rifle762.damage.carbine", "hbm-sedna-762.toml", "damage.carbine", Kind.FLOAT, false);
        bind(values, "weapons.rifle762.damage.carbineBayonet", "hbm-sedna-762.toml", "damage.carbineBayonet", Kind.FLOAT, false);
        bind(values, "weapons.rifle762.damage.lacunae", "hbm-sedna-762.toml", "damage.lacunae", Kind.FLOAT, false);
        bind(values, "weapons.rifle762.damage.minigun", "hbm-sedna-762.toml", "damage.minigun", Kind.FLOAT, false);
        bind(values, "weapons.rifle762.damage.minigunDualLeft", "hbm-sedna-762.toml", "damage.minigunDualLeft", Kind.FLOAT, false);
        bind(values, "weapons.rifle762.damage.minigunDualRight", "hbm-sedna-762.toml", "damage.minigunDualRight", Kind.FLOAT, false);
        bind(values, "weapons.rifle762.damage.southStar", "hbm-sedna-762.toml", "damage.southStar", Kind.FLOAT, false);
        bind(values, "weapons.rifle762.damage.southStarBayonet", "hbm-sedna-762.toml", "damage.southStarBayonet", Kind.FLOAT, false);
        bind(values, "weapons.rpa.damage.largeTargetMultiplier", "hbm-pa-weapons-server.toml", "damage.largeTargetMultiplier", Kind.FLOAT, false);
        bind(values, "weapons.rpa.damage.slap", "hbm-pa-weapons-server.toml", "damage.slap", Kind.FLOAT, false);
        bind(values, "weapons.rpa.damage.swing", "hbm-pa-weapons-server.toml", "damage.swing", Kind.FLOAT, false);
        bind(values, "weapons.stinger.damage.stinger", "hbm-sedna-stinger.toml", "damage.stinger", Kind.FLOAT, false);
        VALUES = Collections.unmodifiableNavigableMap(values);
    }

    private static void bind(NavigableMap<String, Binding> values, String key, String fileName,
                             String path, Kind kind, boolean fixedDamage) {
        if (values.put(key, new Binding(key, fileName, List.of(path.split("\\.")), kind, fixedDamage)) != null)
            throw new IllegalStateException("Duplicate server variable " + key);
    }
    public static Set<String> keys() { return VALUES.navigableKeySet(); }
    public static Binding binding(String key) { return VALUES.get(key); }

    private static ModConfig config(String fileName) {
        var config = ModConfigs.getFileMap().get(fileName);
        if (config == null || !"hbm".equals(config.getModId()) || config.getType() == ModConfig.Type.CLIENT)
            throw new IllegalStateException("Server config is not registered: " + fileName);
        if (config.getLoadedConfig() == null)
            throw new IllegalStateException("Server config is not loaded: " + fileName);
        return config;
    }
    private static Resolved resolve(Binding binding) {
        var config = config(binding.fileName());
        if (!(config.getSpec() instanceof ModConfigSpec spec))
            throw new IllegalStateException("Unsupported config spec: " + binding.fileName());
        Object value = spec.getValues().get(binding.path());
        if (!(value instanceof ModConfigSpec.ConfigValue<?> configValue))
            throw new IllegalStateException("Server variable is not registered: " + binding.key());
        return new Resolved(config, configValue);
    }
    public static String read(Binding binding) {
        Object value = resolve(binding).value().get();
        return binding.kind() == Kind.FLOAT ? Float.toString(((Number) value).floatValue()) : String.valueOf(value);
    }
    @SuppressWarnings("unchecked")
    public static void set(Binding binding, String text) {
        var resolved = resolve(binding);
        var value = (ModConfigSpec.ConfigValue<Object>) resolved.value();
        Object parsed = binding.kind().parse(text);
        // Preserve float parsing/rounding before adapting to the existing TOML Double storage.
        if (parsed instanceof Float number && value.getDefault() instanceof Double) parsed = (double) number;
        if (!value.getSpec().test(parsed))
            throw new IllegalArgumentException("Value does not satisfy the registered config constraints.");
        value.set(parsed);
        value.save();
    }

    /** Run the actual per-file FML reload chain, without registering another file watcher. */
    public static void reload() throws ReflectiveOperationException {
        Class<?> watcher = Class.forName("net.neoforged.fml.config.ConfigWatcher", false, ConfigTracker.class.getClassLoader());
        if (!watcher.getModule().isOpen(watcher.getPackageName(), ServerSettingsAccess.class.getModule()))
            throw new IllegalStateException("FML config package is not open for the per-file reload entry point.");
        Constructor<?> constructor = watcher.getDeclaredConstructor(ModConfig.class, Path.class, ClassLoader.class);
        if (!constructor.trySetAccessible())
            throw new IllegalStateException("FML per-file reload entry point is inaccessible.");
        var files = new LinkedHashSet<String>();
        VALUES.values().forEach(binding -> files.add(binding.fileName()));
        var reloads = new ArrayList<Runnable>();
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        for (String fileName : files) {
            var config = config(fileName);
            reloads.add((Runnable) constructor.newInstance(config, config.getFullPath(), contextLoader));
        }
        for (Runnable reload : reloads) reload.run();
    }
    private ServerSettingsAccess() {}
}

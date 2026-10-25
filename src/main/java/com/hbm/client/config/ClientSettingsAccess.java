package com.hbm.client.config;

import com.hbm.block.rebar.RebarConfig;
import com.hbm.block.structurebasic.ReedsRenderConfig;
import com.hbm.config.ServerSettingsAccess;
import com.hbm.energy.wire.PylonConfig;
import com.hbm.item.toolability.CobaltToolClientConfig;
import com.hbm.weapon.sedna.gun.GunClientSettings;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Command bindings use the actual current config values and schema, without legacy-name aliases. */
public final class ClientSettingsAccess {
    public record Binding(String key, ModConfigSpec spec, java.util.List<String> path, boolean horizontalGeiger) {
        ModConfigSpec.ConfigValue<?> value() {
            var value = spec.getValues().get(path);
            if (!(value instanceof ModConfigSpec.ConfigValue<?> entry))
                throw new IllegalStateException("Unregistered client setting " + key);
            return entry;
        }
        public String typeName() { return spec == null ? "Integer" : value().getDefault().getClass().getSimpleName(); }
    }
    private static final NavigableMap<String, Binding> VALUES;
    static {
        var values = new TreeMap<String, Binding>();
        values.put("hud.geiger.horizontalOffset", new Binding("hud.geiger.horizontalOffset", null, java.util.List.of(), true));
        values.put("hud.geiger.verticalOffset", new Binding("hud.geiger.verticalOffset", null, java.util.List.of(), false));
        bind(values, "pylons", PylonConfig.SPEC, "wires.saggingEnabled", "hud.infoBox.position", "hud.infoBox.horizontalOffset", "hud.infoBox.verticalOffset");
        bind(values, "weapons", GunClientSettings.SPEC, "viewmodel.useCameraFov", "viewmodel.visualRecoilEnabled", "animation.classicStyleEnabled", "animation.speedMultiplier");
        bind(values, "tooltips", ClientTooltipConfig.SPEC, "advanced.itemTagsEnabled", "materials.customNukeEnabled");
        bind(values, "effects", ClientEffectsConfig.SPEC, "mainMenu.customSplashesEnabled", "hud.lookOverlayEnabled", "nuclear.hudFlashEnabled", "nuclear.hudShakeEnabled", "recipeViewer.hideSecretsEnabled", "coolingTower.steamParticlesEnabled", "solarBoiler.maxHeliostatBeams", "hud.blockStateDebugEnabled", "hud.modeBadgesEnabled", "worldScenes.remoteOrbitalMessagesEnabled");
        bind(values, "reeds", ReedsRenderConfig.SPEC, "rendering.underwaterSegmentsEnabled");
        bind(values, "rebar", RebarConfig.SPEC, "geometry.simplifiedModelEnabled", "rendering.maxWetConcreteSurfacesPerFrame");
        bind(values, "tools", CobaltToolClientConfig.SPEC, "hud.horizontalOffset", "hud.verticalOffset");
        VALUES = Collections.unmodifiableNavigableMap(values);
    }
    private static void bind(NavigableMap<String, Binding> values, String prefix, ModConfigSpec spec, String... paths) {
        for (String path : paths) {
            String key = prefix + "." + path;
            values.put(key, new Binding(key, spec, java.util.List.of(path.split("\\.")), false));
        }
    }
    public static Set<String> keys() { return VALUES.navigableKeySet(); }
    public static Binding binding(String key) { return VALUES.get(key); }
    public static String read(Binding binding) {
        return binding.spec() == null ? Integer.toString(binding.horizontalGeiger() ? LegacyClientConfig.geigerHorizontal() : LegacyClientConfig.geigerVertical())
                : String.valueOf(binding.value().get());
    }
    @SuppressWarnings("unchecked")
    public static void set(Binding binding, String text) throws java.io.IOException {
        if (binding.spec() == null) {
            LegacyClientConfig.setGeigerOffset(binding.horizontalGeiger(), Integer.parseInt(text));
            return;
        }
        var value = (ModConfigSpec.ConfigValue<Object>) binding.value();
        Object initial = value.getDefault(), parsed;
        if (initial instanceof Boolean) parsed = Boolean.parseBoolean(text);
        else if (initial instanceof Integer) parsed = Integer.parseInt(text);
        else if (initial instanceof Double) parsed = Double.parseDouble(text);
        else throw new IllegalStateException("Unsupported client setting type " + initial.getClass().getSimpleName());
        if (!value.getSpec().test(parsed)) throw new IllegalArgumentException("Value does not satisfy the registered config constraints.");
        value.set(parsed);
        value.save();
    }
    public static void reload() throws ReflectiveOperationException {
        var configs = new LinkedHashSet<ModConfig>();
        for (var binding : VALUES.values()) if (binding.spec() != null) {
            var config = ModConfigs.getFileMap().values().stream()
                    .filter(candidate -> candidate.getSpec() == binding.spec() && candidate.getType() == ModConfig.Type.CLIENT && "hbm".equals(candidate.getModId()))
                    .findFirst().orElseThrow(() -> new IllegalStateException("Client config is not registered: " + binding.key()));
            if (config.getLoadedConfig() == null) throw new IllegalStateException("Client config is not loaded: " + binding.key());
            configs.add(config);
        }
        ServerSettingsAccess.reloadNativeConfigs(configs);
        LegacyClientConfig.reload();
    }
    private ClientSettingsAccess() {}
}

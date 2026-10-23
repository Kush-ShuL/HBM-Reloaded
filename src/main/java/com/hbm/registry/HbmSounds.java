package com.hbm.registry;

import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Every original sound event, with canonical resource identifiers for modern Minecraft. */
public final class HbmSounds {
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, "hbm");
    private static final Map<String, Supplier<SoundEvent>> EVENTS = new LinkedHashMap<>();
    private static final Map<String, SoundEvent> UNDEFINED_EVENTS = new ConcurrentHashMap<>();

    private HbmSounds() {}

    public static void register(IEventBus bus) {
        try (var stream = HbmSounds.class.getResourceAsStream("/assets/hbm/sounds.json")) {
            if (stream == null) throw new IllegalStateException("Missing sounds.json: run tools/port_resources.py");
            var definitions = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            for (String event : definitions.keySet()) {
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath("hbm", event);
                EVENTS.put(event, SOUNDS.register(event, () -> SoundEvent.createVariableRangeEvent(id)));
            }
        } catch (java.io.IOException error) {
            throw new IllegalStateException("Cannot load original sound events", error);
        }
        SOUNDS.register(bus);
    }

    public static SoundEvent get(String originalName) {
        String canonical = originalName.toLowerCase(Locale.ROOT);
        if (canonical.startsWith("hbm:")) canonical = canonical.substring(4);
        var event = EVENTS.get(canonical);
        if (event != null) return event.get();
        // X5846 also calls names absent from its own sounds.json (e.g. the chopper's
        // weapon.osiprShoot). Native playback resolves those as missing audio;
        // it must not abort an entity/server tick before the original attack runs.
        return UNDEFINED_EVENTS.computeIfAbsent(canonical,
                name -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("hbm", name)));
    }
}

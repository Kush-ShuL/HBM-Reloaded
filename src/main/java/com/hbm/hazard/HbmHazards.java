package com.hbm.hazard;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Registers component identity and binds source definitions to actual content. */
public final class HbmHazards {
    private static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "hbm");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LEGACY_METADATA =
            COMPONENTS.register("legacy_metadata", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.intRange(0, 32767)).networkSynchronized(ByteBufCodecs.VAR_INT).build());
    private static HazardRegistry registry = new HazardRegistry();
    private static List<JsonObject> unboundDefinitions = List.of();

    private HbmHazards() {}

    public static void register(IEventBus bus) {
        COMPONENTS.register(bus);
        com.hbm.exposure.HbmExposure.register(bus);
        HazardRuntime.register();
        bus.addListener(HbmHazards::setup);
    }

    public static HazardRegistry registry() { return registry; }
    public static List<JsonObject> unboundDefinitions() { return unboundDefinitions.stream().map(JsonObject::deepCopy).toList(); }

    private static void setup(FMLCommonSetupEvent event) { event.enqueueWork(HbmHazards::loadDefinitions); }

    private static void loadDefinitions() {
        try (var stream = HbmHazards.class.getResourceAsStream("/hbm/hazards.json")) {
            if (stream == null) throw new IllegalStateException("Missing X5846 hazard definitions");
            var root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            var loaded = new HazardRegistry();
            var unbound = new ArrayList<JsonObject>();
            JsonObject profiles = root.has("itemStateProfiles") ? root.getAsJsonObject("itemStateProfiles") : new JsonObject();
            float xenon135 = constant(root, "xe135"), nugget = constant(root, "nugget");
            for (var value : root.getAsJsonArray("definitions")) {
                var definition = value.getAsJsonObject();
                var selector = definition.getAsJsonObject("selector");
                var id = ResourceLocation.parse(selector.get("id").getAsString());
                String kind = selector.get("kind").getAsString();
                if (!kind.equals("tag") && !BuiltInRegistries.ITEM.containsKey(id)) {
                    unbound.add(definition.deepCopy());
                    continue;
                }
                HazardData data = decodeData(definition.getAsJsonObject("data"), profiles.has(id.toString()) ? profiles.getAsJsonObject(id.toString()) : null, xenon135, nugget);
                switch (kind) {
                    case "tag" -> loaded.registerTag(id.toString(), data);
                    case "item" -> loaded.registerItem(id.toString(), data);
                    case "stack" -> loaded.registerStack(id.toString(), selector.get("metadata").getAsInt(), data);
                    default -> throw new IllegalStateException("Unknown source hazard selector " + kind);
                }
            }
            for (var value : root.getAsJsonArray("blacklists")) {
                var selector = value.getAsJsonObject();
                loaded.blacklistTag(ResourceLocation.parse(selector.get("id").getAsString()).toString());
            }
            Map<String,String> aliases = new LinkedHashMap<>();
            if (root.has("legacyAliases")) root.getAsJsonObject("legacyAliases").entrySet().forEach(entry -> aliases.put(entry.getKey(),entry.getValue().getAsString()));
            JsonObject containers = root.has("containerProfiles") ? root.getAsJsonObject("containerProfiles") : new JsonObject();
            loaded.addTransformer(new HazardContainerRadiation(loaded,containers,aliases,false));
            loaded.addTransformer(new HazardContainerRadiation(loaded,containers,aliases,true));
            HazardArmorMods.configure(aliases);
            registry = loaded;
            unboundDefinitions = List.copyOf(unbound);
        } catch (java.io.IOException issue) {
            throw new IllegalStateException("Could not load X5846 hazard definitions", issue);
        }
    }

    private static float constant(JsonObject root,String name) {
        for(var entry:root.getAsJsonArray("constants"))if(entry.getAsJsonObject().get("name").getAsString().equals(name))return entry.getAsJsonObject().get("value").getAsFloat();
        throw new IllegalStateException("Missing original hazard constant "+name);
    }

    private static HazardData decodeData(JsonObject value,JsonObject profile,float xenon135,float nugget) {
        var entries = new ArrayList<HazardEntry>();
        for (var item : value.getAsJsonArray("entries")) {
            var entry = item.getAsJsonObject();
            var pending = new ArrayList<String>();
            var modifiers = new ArrayList<HazardModifier>();
            for (var modifier : entry.getAsJsonArray("modifiers")) {
                var spec = modifier.getAsJsonObject();
                modifiers.add(HazardStateModifiers.decode(spec,profile,xenon135,nugget));
            }
            entries.add(new HazardEntry(HazardType.valueOf(entry.get("type").getAsString()),
                    entry.get("value").getAsFloat(), modifiers, pending));
        }
        return new HazardData(entries, value.get("doesOverride").getAsBoolean(), value.get("mutex").getAsInt());
    }
}

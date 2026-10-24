package com.hbm.fluid;

import com.google.gson.JsonObject;
import com.hbm.HbmReloaded;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

/** Original independent NTM fluid identity; deliberately separate from Forge fluids. */
public final class HbmFluidType {
    public static final int ROOM_TEMPERATURE = 20;
    public enum Symbol {
        NONE(0, 0), RADIATION(195, 2), NOWATER(195, 63), ACID(195, 124),
        ASPHYXIANT(195, 185), CROYGENIC(134, 185), ANTIMATTER(73, 185), OXIDIZER(12, 185);
        public final int x, y;
        Symbol(int x, int y) { this.x = x; this.y = y; }
    }
    public record Canister(int color) {}
    public record Gastank(int bottleColor, int labelColor) {}

    private int id;
    private String name;
    private int color, tint;
    private String localizedOverride;
    private ResourceLocation texture;
    public int poison, flammability, reactivity, temperature;
    public Symbol symbol;
    public boolean renderWithTint;
    private final Map<String, FluidTraits.Trait> traits = new LinkedHashMap<>();
    private final Map<Class<?>, Object> containers = new LinkedHashMap<>();

    HbmFluidType() {}
    HbmFluidType(JsonObject definition) { setupBase(definition); }

    void setupBase(JsonObject data) {
        // 每次完整定义应用均重置可选值，避免上一次服务器或重载的样式、名称和特性残留。
        containers.clear();
        traits.clear();
        localizedOverride = data.has("localizedOverride") ? data.get("localizedOverride").getAsString() : null;
        id = data.get("id").getAsInt();
        name = data.get("name").getAsString();
        color = data.get("color").getAsInt();
        poison = data.get("p").getAsInt();
        flammability = data.get("f").getAsInt();
        reactivity = data.get("r").getAsInt();
        symbol = Symbol.valueOf(data.get("symbolMark").getAsString());
        temperature = data.get("temperature").getAsInt();
        tint = data.has("tint") ? data.get("tint").getAsInt() : 0xFFFFFF;
        renderWithTint = data.has("renderWithTint") && data.get("renderWithTint").getAsBoolean();
        texture = texture(data.get("texture").getAsString());
        if (data.has("containers")) {
            var object = data.getAsJsonObject("containers");
            if (object.has("canister")) containers.put(Canister.class, new Canister(object.getAsJsonObject("canister").get("color").getAsInt()));
            if (object.has("gastank")) {
                var gas = object.getAsJsonObject("gastank");
                containers.put(Gastank.class, new Gastank(gas.get("bottleColor").getAsInt(), gas.get("labelColor").getAsInt()));
            }
        }
        if (data.has("traits")) replaceTraits(data.getAsJsonObject("traits"));
    }

    void setupCustom(String internalName, JsonObject data) {
        containers.clear();
        traits.clear();
        localizedOverride = null;
        name = internalName;
        id = data.get("id").getAsInt();
        localizedOverride = data.get("name").getAsString();
        color = data.get("color").getAsInt();
        tint = data.get("tint").getAsInt();
        poison = data.get("p").getAsInt();
        flammability = data.get("f").getAsInt();
        reactivity = data.get("r").getAsInt();
        symbol = data.has("symbol") ? Symbol.valueOf(data.get("symbol").getAsString()) : Symbol.NONE;
        texture = texture(data.get("texture").getAsString());
        temperature = data.get("temperature").getAsInt();
        renderWithTint = true;
    }

    public void replaceTraits(JsonObject values) {
        traits.clear();
        values.entrySet().forEach(entry -> {
            try { traits.put(entry.getKey(), FluidTraits.parse(entry.getKey(), entry.getValue().getAsJsonObject())); }
            catch (RuntimeException error) { HbmReloaded.LOGGER.error("Cannot read fluid trait {} for {}", entry.getKey(), name, error); }
        });
    }

    public Map<String, FluidTraits.Trait> traits() { return Collections.unmodifiableMap(traits); }
    public HbmFluidType addTraits(FluidTraits.Trait... values) {
        for (var trait : values) traits.put(trait.name(), trait);
        return this;
    }
    public FluidTraits.Trait getTrait(String name) { return traits.get(name); }
    public boolean hasTrait(String name) { return traits.containsKey(name); }
    public boolean hasTrait(Class<? extends FluidTraits.Trait> type) { return getTrait(type) != null; }
    public <T extends FluidTraits.Trait> T getTrait(Class<T> type) {
        return traits.values().stream().filter(type::isInstance).map(type::cast).findFirst().orElse(null);
    }
    public <T> T getContainer(Class<T> type) { return type.cast(containers.get(type)); }
    public int getID() { return id; }
    public String getName() { return name; }
    public int getColor() { return color; }
    public int getTint() { return tint; }
    public ResourceLocation getTexture() { return texture; }
    public String getUnlocalizedName() { return "hbmfluid." + name.toLowerCase(Locale.US); }
    public String getConditionalName() { return localizedOverride == null ? getUnlocalizedName() : localizedOverride; }
    public boolean hasLocalizedOverride() { return localizedOverride != null; }
    public Component displayName() { return localizedOverride == null ? Component.translatable(getUnlocalizedName()) : Component.literal(localizedOverride); }
    public String getDict(int quantity, boolean enableFluidContainerCompat) {
        return (enableFluidContainerCompat ? "container" : "ntmcontainer") + quantity + name.replace("_", "").toLowerCase(Locale.US);
    }
    public HbmFluidType setTemp(int temperature) { this.temperature = temperature; return this; }
    public boolean isHot() { return temperature >= 100; }
    public boolean isCorrosive() { return hasTrait(FluidTraits.Corrosive.class); }
    public boolean isHighlyCorrosive() { var trait = getTrait(FluidTraits.Corrosive.class); return trait != null && trait.isHighlyCorrosive(); }
    public boolean isAntimatter() { return hasTrait("amat"); }
    public boolean hasNoContainer() { return hasTrait("nocontainer"); }
    public boolean hasNoID() { return hasTrait("noid"); }
    public boolean needsLeadContainer() { return hasTrait("leadcontainer"); }
    public boolean isDispersable() { return !(hasTrait("amat") || hasTrait("nocontainer") || hasTrait("viscous")); }

    /** Complete in-memory definition; no configuration I/O or integration callbacks. */
    JsonObject definitionJson() {
        var data = new JsonObject();
        data.addProperty("id", id);
        data.addProperty("name", name);
        data.addProperty("color", color);
        data.addProperty("tint", tint);
        data.addProperty("p", poison);
        data.addProperty("f", flammability);
        data.addProperty("r", reactivity);
        data.addProperty("symbolMark", symbol.name());
        data.addProperty("temperature", temperature);
        data.addProperty("texture", texture.toString());
        data.addProperty("renderWithTint", renderWithTint);
        if (localizedOverride != null) data.addProperty("localizedOverride", localizedOverride);
        var styles = new JsonObject();
        var canister = getContainer(Canister.class);
        if (canister != null) {
            var style = new JsonObject();
            style.addProperty("color", canister.color());
            styles.add("canister", style);
        }
        var gastank = getContainer(Gastank.class);
        if (gastank != null) {
            var style = new JsonObject();
            style.addProperty("bottleColor", gastank.bottleColor());
            style.addProperty("labelColor", gastank.labelColor());
            styles.add("gastank", style);
        }
        data.add("containers", styles);
        data.add("traits", traitsJson());
        return data;
    }

    public JsonObject traitsJson() {
        var object = new JsonObject();
        traits.forEach((key, trait) -> object.add(key, trait.toJson()));
        return object;
    }

    /** Original trait onRelease dispatch; radiation applies to VOID, BURN and SPILL. */
    public void release(ServerLevel level, BlockPos position, FluidTank tank, int amount, FluidTraits.Release release) {
        var pollution = getTrait(FluidTraits.Polluting.class);
        if (pollution != null) pollution.release(level, position, release, amount);
        var radiation = getTrait(FluidTraits.VentRadiation.class);
        if (radiation != null) radiation.release(level, position, amount);
    }

    private static ResourceLocation texture(String value) {
        return value.contains(":") ? ResourceLocation.parse(value) : HbmReloaded.id(value.startsWith("textures/") ? value : "textures/gui/fluids/" + value + ".png");
    }
}

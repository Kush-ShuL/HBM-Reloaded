package com.hbm.fluid;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hbm.pollution.PollutionSystem;
import com.hbm.pollution.PollutionType;
import com.hbm.radiation.HbmRadiation;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/** Typed X5846 trait values, with the original JSON field names retained. */
public final class FluidTraits {
    private FluidTraits() {}
    public enum Release { VOID, BURN, SPILL }
    public enum FuelGrade { LOW, MEDIUM, HIGH, AERO, GAS }
    public enum HeatingType { BOILER, HEATEXCHANGER, PWR, ICF, PA }
    public enum CoolingType { TURBINE, HEATEXCHANGER }

    public abstract static class Trait {
        private final String name;
        protected final JsonObject data;
        protected Trait(String name, JsonObject data) { this.name = name; this.data = data.deepCopy(); }
        public final String name() { return name; }
        public final JsonObject toJson() { return data.deepCopy(); }
    }
    public static final class Marker extends Trait {
        private Marker(String name, JsonObject data) { super(name, data); }
    }
    public static final class Corrosive extends Trait {
        private Corrosive(JsonObject data) { super("corrosive", data); }
        public int getRating() { return data.get("rating").getAsInt(); }
        public boolean isHighlyCorrosive() { return getRating() > 50; }
    }
    public static final class Flammable extends Trait {
        private Flammable(JsonObject data) { super("flammable", data); }
        public long getHeatEnergy() { return data.get("energy").getAsLong(); }
    }
    public static final class Combustible extends Trait {
        private Combustible(JsonObject data) { super("combustible", data); }
        public long getCombustionEnergy() { return data.get("energy").getAsLong(); }
        public FuelGrade getGrade() { return FuelGrade.valueOf(data.get("grade").getAsString()); }
    }
    public static final class Polluting extends Trait {
        private Polluting(JsonObject data) { super("polluting", data); }
        public Map<PollutionType, Float> releaseMap() { return pollutionMap("release"); }
        public Map<PollutionType, Float> burnMap() { return pollutionMap("burn"); }
        private Map<PollutionType, Float> pollutionMap(String name) {
            var values = new EnumMap<PollutionType, Float>(PollutionType.class);
            if (data.has(name)) {
                var object = data.getAsJsonObject(name);
                for (var type : PollutionType.values()) if (object.has(type.name())) values.put(type, object.get(type.name()).getAsFloat());
            }
            return Map.copyOf(values);
        }
        public void release(ServerLevel level, BlockPos position, Release release, int amount) {
            if (release == Release.SPILL) releaseMap().forEach((type, rate) -> PollutionSystem.increment(level, position, type, rate * amount));
            if (release == Release.BURN) burnMap().forEach((type, rate) -> PollutionSystem.increment(level, position, type, rate * amount));
        }
    }
    public static final class VentRadiation extends Trait {
        private VentRadiation(JsonObject data) { super("ventradiation", data); }
        public float getRadPerMB() { return data.get("radiation").getAsFloat(); }
        public void release(ServerLevel level, BlockPos position, int amount) {
            HbmRadiation.incrementRad(level, position.getX(), position.getY(), position.getZ(), amount * getRadPerMB());
        }
    }
    public record HeatingStep(int amountReq, int heatReq, String typeProducedName, int amountProduced) {
        public HbmFluidType typeProduced() { return HbmFluids.fromName(typeProducedName); }
    }
    public static final class Heatable extends Trait {
        private Heatable(JsonObject data) { super("heatable", data); }
        public List<HeatingStep> steps() {
            List<HeatingStep> result = new ArrayList<>();
            for (var value : data.getAsJsonArray("steps")) {
                var step = value.getAsJsonObject();
                result.add(new HeatingStep(step.get("amountReq").getAsInt(), step.get("heatReq").getAsInt(), step.get("typeProduced").getAsString(), step.get("amountProd").getAsInt()));
            }
            return List.copyOf(result);
        }
        public HeatingStep getFirstStep() { return steps().getFirst(); }
        public double getEfficiency(HeatingType type) { return data.has(type.name()) ? data.get(type.name()).getAsDouble() : 0D; }
    }
    public static final class Coolable extends Trait {
        private Coolable(JsonObject data) { super("coolable", data); }
        public HbmFluidType coolsTo() { return HbmFluids.fromName(data.get("coolsTo").getAsString()); }
        public int amountReq() { return data.get("amountReq").getAsInt(); }
        public int amountProduced() { return data.get("amountProd").getAsInt(); }
        public int heatEnergy() { return data.get("heatEnergy").getAsInt(); }
        public double getEfficiency(CoolingType type) { return data.has(type.name()) ? data.get(type.name()).getAsDouble() : 0D; }
    }
    public static final class PwrModerator extends Trait {
        private PwrModerator(JsonObject data) { super("pwrmoderator", data); }
        public double getMultiplier() { return data.get("multiplier").getAsDouble(); }
    }
    public static final class Poison extends Trait {
        private Poison(JsonObject data) { super("poison", data); }
        public int getLevel() { return data.get("level").getAsInt(); }
        public boolean isWithering() { return data.get("withering").getAsBoolean(); }
    }
    public static final class Pheromone extends Trait {
        private Pheromone(JsonObject data) { super("pheromone", data); }
        public int getType() { return data.get("type").getAsInt(); }
    }
    /** Data is preserved even while armor protection and potion execution await their ports. */
    public static final class Toxin extends Trait {
        private Toxin(JsonObject data) { super("toxin", data); }
        public List<ToxinEntry> entries() {
            var entries = new ArrayList<ToxinEntry>();
            for (var value : data.getAsJsonArray("entries")) {
                var entry = value.getAsJsonObject();
                String mask = entry.get("masktype").getAsString();
                boolean hazmat = entry.get("hazmat").getAsBoolean();
                switch (entry.get("type").getAsString()) {
                    case "directdamage" -> entries.add(new DirectDamage(entry.get("source").getAsString(), entry.get("amount").getAsFloat(), entry.get("delay").getAsInt(), mask, hazmat));
                    case "effects" -> {
                        var effects = new ArrayList<Potion>();
                        for (var effectValue : entry.getAsJsonArray("effects")) {
                            JsonArray effect = effectValue.getAsJsonArray();
                            effects.add(new Potion(effect.get(0).getAsString(), effect.get(1).getAsInt(), effect.get(2).getAsInt(), effect.get(3).getAsBoolean()));
                        }
                        entries.add(new Effects(List.copyOf(effects), mask, hazmat));
                    }
                    default -> { /* The original reader ignores unrecognized entry types. */ }
                }
            }
            return List.copyOf(entries);
        }
    }
    public sealed interface ToxinEntry permits DirectDamage, Effects {}
    public record DirectDamage(String source, float amount, int delay, String maskType, boolean fullBody) implements ToxinEntry {}
    public record Effects(List<Potion> effects, String maskType, boolean fullBody) implements ToxinEntry {}
    public record Potion(String resourceName, int duration, int amplifier, boolean ambient) {}

    public static Trait parse(String name, JsonObject data) {
        return switch (name) {
            case "corrosive" -> new Corrosive(data);
            case "flammable" -> new Flammable(data);
            case "combustible" -> new Combustible(data);
            case "polluting" -> new Polluting(data);
            case "heatable" -> new Heatable(data);
            case "coolable" -> new Coolable(data);
            case "pwrmoderator" -> new PwrModerator(data);
            case "poison" -> new Poison(data);
            case "toxin" -> new Toxin(data);
            case "ventradiation" -> new VentRadiation(data);
            case "pheromone" -> new Pheromone(data);
            case "gaseous", "gaseous_art", "liquid", "viscous", "plasma", "amat", "leadcontainer", "delicious", "noid", "nocontainer", "unsiphonable" -> new Marker(name, data);
            default -> throw new IllegalArgumentException("Unknown original fluid trait: " + name);
        };
    }
}

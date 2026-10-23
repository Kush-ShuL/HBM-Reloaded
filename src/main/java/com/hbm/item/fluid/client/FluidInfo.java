package com.hbm.item.fluid.client;

import com.hbm.fluid.FluidTraits;
import com.hbm.fluid.HbmFluidType;
import com.hbm.item.battery.BatteryItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/** Original FluidType/trait tooltip order, values and translation keys. */
public final class FluidInfo {
    private static final List<String> ORDER = List.of("corrosive", "flammable", "combustible", "polluting", "heatable", "coolable", "pwrmoderator", "poison", "toxin", "ventradiation", "pheromone", "gaseous", "gaseous_art", "liquid", "viscous", "plasma", "amat", "leadcontainer", "delicious", "noid", "nocontainer", "unsiphonable");
    private FluidInfo() {}
    private static String key(String key) { return Component.translatable("hbmfluid.trait." + key).getString(); }
    private static Component text(String text, ChatFormatting color) { return Component.literal(text).withStyle(color); }
    private static void header(List<Component> list, String key, ChatFormatting color) { list.add(text("[" + key(key) + "]", color)); }
    public static void append(HbmFluidType type, List<Component> result, boolean shift) {
        if (type.temperature != 20 && type.temperature != 0) result.add(text(type.temperature + "°C", type.temperature < 0 ? ChatFormatting.BLUE : ChatFormatting.RED));
        var allHidden = new ArrayList<Component>();
        for (String name : ORDER) {
            var trait = type.getTrait(name); if (trait == null) continue;
            var hidden = new ArrayList<Component>();
            switch (name) {
                case "corrosive" -> { var corrosion = (FluidTraits.Corrosive) trait; header(result, corrosion.isHighlyCorrosive() ? "corrosiveStrong" : "corrosive", corrosion.isHighlyCorrosive() ? ChatFormatting.GOLD : ChatFormatting.YELLOW); }
                case "flammable" -> {
                    header(result, "flammable", ChatFormatting.YELLOW);
                    long energy = ((FluidTraits.Flammable) trait).getHeatEnergy();
                    if (energy > 0) result.add(text(key("provides") + " ", ChatFormatting.YELLOW).copy().append(text(BatteryItem.shortNumber(energy) + "TU ", ChatFormatting.RED)).append(text(key("perBucket"), ChatFormatting.YELLOW)));
                }
                case "combustible" -> {
                    header(result, "combustible", ChatFormatting.GOLD); var value = (FluidTraits.Combustible) trait;
                    if (value.getCombustionEnergy() > 0) {
                        result.add(text(key("provides") + " ", ChatFormatting.GOLD).copy().append(text(BatteryItem.shortNumber(value.getCombustionEnergy()) + "HE ", ChatFormatting.RED)).append(text(key("perBucket"), ChatFormatting.GOLD)));
                        String grade = switch (value.getGrade()) { case AERO -> "aviation"; case GAS -> "gaseous"; default -> value.getGrade().name().toLowerCase(Locale.US); };
                        result.add(text(key("fuelGrade") + ": ", ChatFormatting.GOLD).copy().append(text(key("fuel." + grade), ChatFormatting.RED)));
                    }
                }
                case "polluting" -> {
                    header(result, "polluting", ChatFormatting.GOLD); var value = (FluidTraits.Polluting) trait;
                    if (!value.releaseMap().isEmpty()) { hidden.add(text(key("spilled") + ":", ChatFormatting.GREEN)); value.releaseMap().forEach((kind, amount) -> hidden.add(text(" - " + amount + " " + Component.translatable("pollution.trait." + kind.name().toLowerCase(Locale.US)).getString() + " " + key("perMB"), ChatFormatting.GREEN))); }
                    if (!value.burnMap().isEmpty()) { hidden.add(text(key("burned") + ":", ChatFormatting.RED)); value.burnMap().forEach((kind, amount) -> hidden.add(text(" - " + amount + " " + Component.translatable("pollution.trait." + kind.name().toLowerCase(Locale.US)).getString() + " " + key("perMB"), ChatFormatting.RED))); }
                }
                case "heatable" -> {
                    var value = (FluidTraits.Heatable) trait; var first = value.getFirstStep();
                    hidden.add(text(key("thermalCapacity") + ": " + first.heatReq() + " " + key("perTU") + " " + first.amountReq() + "mB", ChatFormatting.RED));
                    String[] keys = {"boilable", "heatable", "coolantPWR", "coolantICF", "coolantPA"};
                    for (var mode : FluidTraits.HeatingType.values()) efficiency(hidden, keys[mode.ordinal()], value.getEfficiency(mode));
                }
                case "coolable" -> {
                    var value = (FluidTraits.Coolable) trait;
                    hidden.add(text(key("thermalCapacity") + ": " + value.heatEnergy() + " " + key("perTU") + " " + value.amountReq() + "mB", ChatFormatting.RED));
                    for (var mode : FluidTraits.CoolingType.values()) efficiency(hidden, mode == FluidTraits.CoolingType.TURBINE ? "steam" : "coolable", value.getEfficiency(mode));
                }
                case "pwrmoderator" -> { header(result, "pwrFluxMultiplier", ChatFormatting.BLUE); int percent = (int) (((FluidTraits.PwrModerator) trait).getMultiplier() * 100 - 100); hidden.add(text(key("pwrFluxCore") + " " + (percent >= 0 ? "+" : "") + percent + "%", ChatFormatting.BLUE)); }
                case "poison" -> hidden.add(text("[Toxic Fumes]", ChatFormatting.GREEN));
                case "toxin" -> {
                    header(hidden, "toxin", ChatFormatting.LIGHT_PURPLE);
                    for (var entry : ((FluidTraits.Toxin) trait).entries()) {
                        if (entry instanceof FluidTraits.DirectDamage damage) hidden.add(text("- " + mask(damage.maskType()) + (damage.fullBody() ? " (" + key("hazmat") + ")" : "") + ": " + String.format(Locale.US, "%,.1f", damage.amount() * 20 / damage.delay()) + " " + key("perDamage"), ChatFormatting.YELLOW));
                        if (entry instanceof FluidTraits.Effects effects) {
                            hidden.add(text("- " + mask(effects.maskType()) + (effects.fullBody() ? " (" + key("hazmat") + ")" : "") + ":", ChatFormatting.YELLOW));
                            for (var potion : effects.effects()) {
                                int seconds = potion.duration() / 20;
                                String potionKey = potion.resourceName().equals("hbm:death") ? "potion.hbm_death" : "effect." + potion.resourceName().replace(':', '.');
                                hidden.add(text("   - " + Component.translatable(potionKey).getString() + (potion.amplifier() > 0 ? " " + Component.translatable("potion.potency." + potion.amplifier()).getString().trim() : "") + " " + seconds / 60 + ":" + String.format(Locale.US, "%02d", seconds % 60), ChatFormatting.YELLOW));
                            }
                        }
                    }
                }
                case "ventradiation" -> header(result, "radioactive", ChatFormatting.YELLOW);
                case "pheromone" -> header(result, ((FluidTraits.Pheromone) trait).getType() == 1 ? "glyphidPheromones" : "modifiedPheromones", ((FluidTraits.Pheromone) trait).getType() == 1 ? ChatFormatting.AQUA : ChatFormatting.BLUE);
                case "amat" -> header(result, "antimatter", ChatFormatting.DARK_RED);
                case "leadcontainer" -> header(result, "leadContainer", ChatFormatting.DARK_RED);
                case "gaseous", "gaseous_art", "liquid", "viscous", "unsiphonable" -> header(hidden, name.equals("gaseous_art") ? "gaseousRoom" : name, ChatFormatting.BLUE);
                case "delicious" -> header(hidden, "delicious", ChatFormatting.DARK_GREEN);
                case "plasma" -> hidden.add(text("[Plasma]", ChatFormatting.LIGHT_PURPLE));
            }
            if (shift) result.addAll(hidden);
            allHidden.addAll(hidden);
        }
        if (!shift && !allHidden.isEmpty()) result.add(com.hbm.item.TooltipHints.moreInfo());
    }
    private static void efficiency(List<Component> result, String mode, double efficiency) {
        if (efficiency > 0) result.add(text("[" + key(mode) + "] ", ChatFormatting.YELLOW).copy().append(text(key("efficiency") + ": " + (int) (efficiency * 100) + "%", ChatFormatting.AQUA)));
    }
    private static String mask(String type) {
        String name = switch (type) {
            case "GAS_LUNG" -> "gasChlorine"; case "GAS_MONOXIDE" -> "gasMonoxide"; case "GAS_INERT" -> "gasInert";
            case "PARTICLE_COARSE" -> "particleCoarse"; case "PARTICLE_FINE" -> "particleFine"; case "BACTERIA" -> "bacteria";
            case "GAS_BLISTERING" -> "corrosive"; case "SAND" -> "sand"; case "LIGHT" -> "light"; default -> type;
        };
        return Component.translatable("hazard." + name).getString();
    }
}

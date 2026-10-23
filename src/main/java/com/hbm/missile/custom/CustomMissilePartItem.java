package com.hbm.missile.custom;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import static com.hbm.missile.custom.CustomMissileParts.*;

/** Real ItemCustomMissilePart attributes/lore; source rarity is a part-loot weight, not Item rarity. */
public final class CustomMissilePartItem extends Item {
    public final Definition definition;
    public CustomMissilePartItem(Definition definition) { super(new Properties().stacksTo(1)); this.definition = definition; }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        var d = definition;
        if (d.title() != null) lines.add(Component.literal("\"" + d.title() + "\"").withStyle(ChatFormatting.DARK_PURPLE));
        switch (d.type()) {
            case CHIP -> line(lines, "inaccuracy", Component.literal(d.number(0) * 100 + "%"));
            case WARHEAD -> {
                line(lines, "size", size(d.bottom())); line(lines, "type", warhead(d.enumName(0)));
                line(lines, "strength", Component.literal(Float.toString(d.number(1))));
                line(lines, "weight", Component.literal(d.number(2) + "t"));
            }
            case FUSELAGE -> {
                line(lines, "topSize", size(d.top())); line(lines, "bottomSize", size(d.bottom()));
                line(lines, "fuelType", fuel(d.enumName(0))); line(lines, "fuelAmount", Component.literal(d.number(1) + "l"));
            }
            case FINS -> { line(lines, "size", size(d.top())); line(lines, "inaccuracy", Component.literal(d.number(0) * 100 + "%")); }
            case THRUSTER -> {
                line(lines, "size", size(d.top())); line(lines, "fuelType", fuel(d.enumName(0)));
                line(lines, "fuelConsumption", Component.literal(d.number(1) + "l/tick"));
                line(lines, "maxPayload", Component.literal(d.number(2) + "t"));
            }
        }
        if (d.type() != PartType.CHIP) line(lines, "health", Component.literal(d.health() + "HP"));
        if (d.rarity() != null) {
            var color = switch (d.rarity()) { case "COMMON" -> ChatFormatting.GRAY; case "UNCOMMON" -> ChatFormatting.YELLOW;
                case "RARE" -> ChatFormatting.AQUA; case "EPIC" -> ChatFormatting.LIGHT_PURPLE; case "LEGENDARY" -> ChatFormatting.DARK_GREEN; default -> ChatFormatting.DARK_AQUA; };
            line(lines, "rarity", Component.translatable("item.missile.part.rarity." + (d.rarity().startsWith("SEWS_") ? "strange" : d.rarity().toLowerCase(java.util.Locale.ROOT))).withStyle(color));
        }
        if (d.author() != null) lines.add(Component.literal("   ").append(Component.translatable("item.missile.part.by")).append(" " + d.author()).withStyle(ChatFormatting.WHITE));
        if (d.witty() != null) lines.add(Component.literal("   \"" + d.witty() + "\"").withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC));
    }
    private static void line(List<Component> lines, String key, Component value) {
        lines.add(Component.translatable("item.missile.part." + key).withStyle(ChatFormatting.BOLD).append(": ")
                .append(Component.empty().withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.GRAY).withBold(false)).append(value)));
    }
    public static Component size(PartSize size) { return switch (size) {
        case ANY -> Component.translatable("item.missile.part.size.any"); case NONE -> Component.translatable("item.missile.part.size.none");
        case SIZE_10 -> Component.literal("1.0m"); case SIZE_15 -> Component.literal("1.5m"); case SIZE_20 -> Component.literal("2.0m"); }; }
    public static Component fuel(String type) {
        String suffix; ChatFormatting color;
        switch (FuelType.valueOf(type)) {
            case KEROSENE -> { suffix = "kerosene_peroxide"; color = ChatFormatting.LIGHT_PURPLE; }
            case SOLID -> { suffix = "solid"; color = ChatFormatting.GOLD; }
            case HYDROGEN -> { suffix = "hydrogen"; color = ChatFormatting.DARK_AQUA; }
            case XENON -> { suffix = "xenon"; color = ChatFormatting.DARK_PURPLE; }
            case BALEFIRE -> { suffix = "balefire"; color = ChatFormatting.GREEN; }
            default -> throw new IllegalArgumentException(type);
        }
        return Component.translatable("item.missile.fuel." + suffix).withStyle(color);
    }
    public static Component warhead(String type) {
        if (WarheadType.valueOf(type).labelCustom != null) return Component.literal(WarheadType.valueOf(type).labelCustom);
        String suffix; ChatFormatting color;
        switch (WarheadType.valueOf(type)) {
            case HE -> { suffix = "he"; color = ChatFormatting.YELLOW; }
            case INC -> { suffix = "incendiary"; color = ChatFormatting.GOLD; }
            case BUSTER -> { suffix = "bunker_buster"; color = ChatFormatting.WHITE; }
            case CLUSTER -> { suffix = "cluster"; color = ChatFormatting.GRAY; }
            case NUCLEAR -> { suffix = "nuclear"; color = ChatFormatting.DARK_GREEN; }
            case TX -> { suffix = "thermonuclear"; color = ChatFormatting.DARK_PURPLE; }
            case N2 -> { suffix = "n2"; color = ChatFormatting.RED; }
            case BALEFIRE -> { suffix = "balefire"; color = ChatFormatting.GREEN; }
            case SCHRAB -> { suffix = "schrabidium"; color = ChatFormatting.AQUA; }
            case TAINT -> { suffix = "taint"; color = ChatFormatting.DARK_PURPLE; }
            case CLOUD -> { suffix = "cloud"; color = ChatFormatting.LIGHT_PURPLE; }
            case TURBINE -> { suffix = "turbine"; color = System.currentTimeMillis() % 1000 < 500 ? ChatFormatting.RED : ChatFormatting.LIGHT_PURPLE; }
            default -> { return Component.translatable("general.na").withStyle(ChatFormatting.BOLD); }
        }
        return Component.translatable("item.warhead.desc." + suffix).withStyle(color);
    }
}

package com.hbm.missile.custom;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;

/** Source ItemCustomMissile assembly stores component identity only, not copied components' metadata/NBT/count. */
public final class CustomMissileItem extends Item {
    public CustomMissileItem() { super(new Properties().stacksTo(1)); }
    public static ItemStack build(ItemStack chip, ItemStack warhead, ItemStack fuselage, ItemStack fins, ItemStack thruster) {
        var stack = new ItemStack(CustomMissileContent.MISSILE.get());
        var tag = new CompoundTag();
        for (var entry : new Object[][]{{"chip", chip}, {"warhead", warhead}, {"fuselage", fuselage}, {"stability", fins}, {"thruster", thruster}}) {
            var component = (ItemStack) entry[1];
            if (!component.isEmpty()) tag.putString((String) entry[0], net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(component.getItem()).toString());
        }
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag)); return stack;
    }
    public static CustomMissileStruct getStruct(ItemStack stack) {
        if (!(stack.getItem() instanceof CustomMissileItem)) return null;
        var data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return new CustomMissileStruct(CustomMissileStruct.readPart(data, "warhead"), CustomMissileStruct.readPart(data, "fuselage"),
                CustomMissileStruct.readPart(data, "stability"), CustomMissileStruct.readPart(data, "thruster"));
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        if (!stack.has(DataComponents.CUSTOM_DATA)) return;
        var data = stack.get(DataComponents.CUSTOM_DATA).copyTag(); var missile = getStruct(stack);
        var chip = CustomMissileStruct.readPart(data, "chip");
        if (chip == null || missile == null || missile.warhead() == null || missile.fuselage() == null || missile.thruster() == null) {
            lines.add(Component.translatable("error.generic").withStyle(ChatFormatting.RED)); return;
        }
        var w = missile.warhead().definition; var f = missile.fuselage().definition; var s = missile.fins() == null ? null : missile.fins().definition; var t = missile.thruster().definition;
        line(lines, "warhead", CustomMissilePartItem.warhead(w.enumName(0)));
        line(lines, "strength", Component.literal(Float.toString(w.number(1))));
        line(lines, "fuelType", CustomMissilePartItem.fuel(f.enumName(0)));
        line(lines, "fuelAmount", Component.literal(f.number(1) + "l"));
        line(lines, "chipInaccuracy", Component.literal(chip.definition.number(0) * 100 + "%"));
        line(lines, "finInaccuracy", Component.literal(s == null ? "100%" : s.number(0) * 100 + "%"));
        line(lines, "size", CustomMissilePartItem.size(f.top()).copy().append("/").append(CustomMissilePartItem.size(f.bottom())));
        line(lines, "health", Component.literal((w.health() + f.health() + t.health() + (s == null ? 0 : s.health())) + "HP"));
    }
    private static void line(List<Component> lines, String key, Component value) {
        lines.add(Component.translatable("item.missile.desc." + key).withStyle(ChatFormatting.BOLD).append(": ")
                .append(Component.empty().withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(ChatFormatting.GRAY).withBold(false)).append(value)));
    }
}

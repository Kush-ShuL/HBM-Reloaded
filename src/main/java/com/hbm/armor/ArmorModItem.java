package com.hbm.armor;

import com.hbm.hazard.HazardArmorModItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Original ItemArmorMod slot and applicability flags; no inferred protection or energy capability. */
public class ArmorModItem extends Item implements HazardArmorModItem {
    public final int type;
    public final boolean helmet, chestplate, leggings, boots;
    public ArmorModItem(int type, boolean helmet, boolean chestplate, boolean leggings, boolean boots) {
        this(new Properties().stacksTo(1),type,helmet,chestplate,leggings,boots);
    }
    protected ArmorModItem(Properties properties,int type,boolean helmet,boolean chestplate,boolean leggings,boolean boots) {
        super(properties);this.type=type;this.helmet=helmet;this.chestplate=chestplate;this.leggings=leggings;this.boots=boots;
    }
    /** Original ItemArmorMod callback; effects are implemented only by the real subclass. */
    public void modUpdate(net.minecraft.world.entity.LivingEntity entity, ItemStack armor) { }
    public Component installedDescription(ItemStack stack) { return stack.getHoverName(); }
    /** Original pre-armor callback, invoked in the existing source damage/mod-slot order. */
    public void modDamage(net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent event, ItemStack armor) { }
    /** Original getModifiers adapted to native attribute holders; empty means no mod attributes. */
    public java.util.Map<net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute>, net.minecraft.world.entity.ai.attributes.AttributeModifier> getModifiers(ItemStack armor) { return java.util.Map.of(); }
    /** Source addDesc receives the actual armor; older mods retain their one-stack override. */
    public Component installedDescription(ItemStack stack, ItemStack armor) { return installedDescription(stack); }
    /** Additional source addDesc lines follow the same installed mod, not a second tooltip event. */
    public void appendInstalledDetails(ItemStack stack, ItemStack armor, TooltipContext context, List<Component> lines, TooltipFlag flags) { }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("armorMod.applicableTo").withStyle(ChatFormatting.DARK_PURPLE));
        if (helmet&&chestplate&&leggings&&boots) lines.add(Component.literal("  ").append(Component.translatable("armorMod.all")));
        else {
            if(helmet)lines.add(Component.literal("  ").append(Component.translatable("armorMod.helmets")));
            if(chestplate)lines.add(Component.literal("  ").append(Component.translatable("armorMod.chestplates")));
            if(leggings)lines.add(Component.literal("  ").append(Component.translatable("armorMod.leggings")));
            if(boots)lines.add(Component.literal("  ").append(Component.translatable("armorMod.boots")));
        }
        lines.add(Component.translatable("armorMod.slot").withStyle(ChatFormatting.DARK_PURPLE));
        String[] slots={"helmet","chestplate","leggings","boots","servo","cladding","insert","special","battery"};
        if(type>=0&&type<slots.length)lines.add(Component.literal("  ").append(Component.translatable("armorMod.type."+slots[type])));
    }
}

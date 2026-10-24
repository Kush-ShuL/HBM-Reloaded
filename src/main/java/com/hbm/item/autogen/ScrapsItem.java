package com.hbm.item.autogen;

import com.hbm.material.HbmMaterials;
import com.hbm.material.MaterialDistributionConfig;
import com.hbm.material.MaterialStack;
import com.hbm.material.NtmMaterial;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

/** Source foundry scraps, including the distinction between no NBT and an existing tag with no amount. */
public final class ScrapsItem extends AutogenItem {
    public static BooleanSupplier leftShiftHeld=()->false;
    public ScrapsItem(AutogenContent.Spec spec,NtmMaterial material) { super(spec,null,material); }
    public static boolean liquid(ItemStack stack) { return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getBoolean("liquid"); }
    public static MaterialStack getMaterials(ItemStack stack) {
        if(!(stack.getItem() instanceof ScrapsItem item)) return null;
        NtmMaterial material=item.material();
        int amount=stack.has(DataComponents.CUSTOM_DATA)?stack.get(DataComponents.CUSTOM_DATA).copyTag().getInt("amount"):HbmMaterials.shape("INGOT").q(1);
        return new MaterialStack(material,amount);
    }
    public static ItemStack create(MaterialStack materials) { return create(materials,false); }
    public static ItemStack create(MaterialStack materials,boolean liquid) {
        if(materials.material==null) return MaterialDistributionConfig.legacyStack("hbm:nothing",0);
        ItemStack stack=materials.material.make(AutogenContent.item("scraps")); CompoundTag tag=new CompoundTag(); tag.putInt("amount",materials.amount); if(liquid) tag.putBoolean("liquid",true); stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag)); return stack;
    }
    @Override public int color(ItemStack stack) { NtmMaterial material=material(); return liquid(stack)&&material!=null?0xff000000|material.moltenColor():super.color(stack); }
    @Override public Component getName(ItemStack stack) {
        MaterialStack contents=getMaterials(stack); if(contents==null) return Component.literal("Foundry Scraps");
        return liquid(stack)?contents.material.displayName():Component.translatable(spec().descriptionId(),contents.material.displayName());
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag) {
        MaterialStack contents=getMaterials(stack); if(contents==null) return;
        lines.add(HbmMaterials.formatAmount(contents.amount,leftShiftHeld.getAsBoolean()));
        if(liquid(stack)&&contents.material.smeltingBehavior()==NtmMaterial.SmeltingBehavior.ADDITIVE) lines.add(Component.literal("Additive, not castable!").withStyle(ChatFormatting.DARK_RED));
    }
    @Override public List<ItemStack> creativeStacks() { return material().smeltingBehavior()==NtmMaterial.SmeltingBehavior.SMELTABLE || material().smeltingBehavior()==NtmMaterial.SmeltingBehavior.ADDITIVE?List.of(new ItemStack(this)):List.of(); }
}

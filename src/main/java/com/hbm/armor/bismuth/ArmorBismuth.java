package com.hbm.armor.bismuth;

import com.hbm.armor.t51.SourceFullSetArmorItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;

/** Actual unpowered X5846 ArmorBismuth: source material/finite durability and four source full-set effects. */
public final class ArmorBismuth extends SourceFullSetArmorItem {
    private final String sourceName;
    public ArmorBismuth(Type slot,String sourceName){super(BismuthContent.MATERIAL,slot,100);this.sourceName=sourceName;setDashCount(3);}
    @Override public List<MobEffectInstance> fsbEffects(){return List.of(new MobEffectInstance(MobEffects.JUMP,20,6,true,true),new MobEffectInstance(MobEffects.MOVEMENT_SPEED,20,6,true,true),new MobEffectInstance(MobEffects.REGENERATION,20,1,true,true),new MobEffectInstance(MobEffects.NIGHT_VISION,300,0,true,true));}
    @Override public Component getName(ItemStack stack){return Component.translatable("item."+sourceName+".name");}
    @Override public ResourceLocation getArmorTexture(ItemStack stack,Entity entity,EquipmentSlot slot,ArmorMaterial.Layer layer,boolean inner){return BismuthContent.id("textures/armor/"+(slot==EquipmentSlot.CHEST?"starmetal_2":"starmetal_1")+".png");}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> text,TooltipFlag flags){text.add(Component.translatable("armor.fullSetBonus").withStyle(ChatFormatting.GOLD));text.add(Component.translatable("effect.minecraft.jump_boost").append(", ").append(Component.translatable("effect.minecraft.speed")).append(", ").append(Component.translatable("effect.minecraft.regeneration")).append(", ").append(Component.translatable("effect.minecraft.night_vision")).withStyle(ChatFormatting.AQUA));text.add(Component.literal("  ").append(Component.translatable("armor.dash",fsbDashCount())).withStyle(ChatFormatting.AQUA));}
}

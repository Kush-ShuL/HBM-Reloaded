package com.hbm.armor.schrabidium;

import com.hbm.armor.t51.SourceFullSetArmorItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Actual unpowered ArmorFSB/cloneStats quartet; the one shared full-set tick entry applies these source templates. */
public final class SchrabidiumArmorItem extends SourceFullSetArmorItem {
    private static final List<MobEffectInstance> EFFECTS = List.of(
            new MobEffectInstance(MobEffects.DIG_SPEED,20,2,true,true),
            new MobEffectInstance(MobEffects.DAMAGE_BOOST,20,2,true,true),
            new MobEffectInstance(MobEffects.JUMP,20,1,true,true),
            new MobEffectInstance(MobEffects.MOVEMENT_SPEED,20,2,true,true));
    public SchrabidiumArmorItem(Holder<ArmorMaterial> material,Type type) { super(material,type,100); }
    @Override public List<MobEffectInstance> fsbEffects() { return EFFECTS; }
    @Override public ResourceLocation getArmorTexture(ItemStack stack,Entity entity,EquipmentSlot slot,ArmorMaterial.Layer layer,boolean inner) {
        return ResourceLocation.fromNamespaceAndPath("hbm","textures/armor/schrabidium_"+(slot==EquipmentSlot.LEGS?2:1)+".png");
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flags) {
        // Original ArmorFSB lists effect names only, without invented levels/durations or armor abilities.
        lines.add(Component.translatable("armor.fullSetBonus").withStyle(ChatFormatting.GOLD));
        var effects = Component.empty();
        for (int i=0;i<EFFECTS.size();i++) { if(i>0)effects.append(", "); effects.append(EFFECTS.get(i).getEffect().value().getDisplayName()); }
        lines.add(effects.withStyle(ChatFormatting.AQUA));
    }
}

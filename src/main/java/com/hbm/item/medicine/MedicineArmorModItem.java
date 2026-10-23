package com.hbm.item.medicine;

import com.hbm.armor.ArmorModItem;
import com.hbm.armor.ArmorModStorage;
import com.hbm.exposure.ExposureEffects;
import com.hbm.exposure.HbmExposure;
import com.hbm.registry.HbmSounds;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** The original three medical extra-slot armor modifications. */
public final class MedicineArmorModItem extends ArmorModItem {
    private static final ResourceLocation TRIGAMMA=ResourceLocation.fromNamespaceAndPath("hbm","trigamma_86d44ca9_44f1_4ca6_bdbb_d9d33bead251");
    private final String id;
    MedicineArmorModItem(String id) {super(ArmorModStorage.EXTRA,id.equals("serum"),true,id.equals("serum"),id.equals("serum"));this.id=id;}
    @Override public Component getName(ItemStack stack) {return Component.translatable("item."+id+".name");}
    public Component installedDescription(ItemStack stack) {
        var result=Component.literal("  ").append(stack.getHoverName());
        if(id.equals("serum"))result.append(Component.translatable("tooltip.hbm.medicine.serum.installed"));
        return result.withStyle(id.equals("injector_knife")?ChatFormatting.RED:ChatFormatting.BLUE);
    }
    void update(LivingEntity entity,ItemStack armor) {
        if(entity.level().isClientSide)return;
        switch(id) {
            case "serum" -> {
                if(entity.hasEffect(MobEffects.POISON)){entity.removeEffect(MobEffects.POISON);entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,100,4));}
            }
            case "injector_5htp" -> {
                if(HbmExposure.digamma(entity)>=5) {
                    ArmorModStorage.removeMod(armor,ArmorModStorage.EXTRA);
                    entity.level().playSound(null,entity.getX(),entity.getY(),entity.getZ(),HbmSounds.get("item.syringe"),SoundSource.NEUTRAL,1,1);
                    HbmExposure.setDigamma(entity,HbmExposure.digamma(entity)-5);
                    entity.addEffect(new MobEffectInstance(ExposureEffects.STABILITY,60*20,0));entity.heal(20);
                }
            }
            case "injector_knife" -> {
                if(entity.tickCount%50==0&&entity.getMaxHealth()>2) {
                    entity.level().playSound(null,entity.getX(),entity.getY(),entity.getZ(),HbmSounds.get("entity.slicer"),SoundSource.NEUTRAL,1,1);
                    var attribute=entity.getAttribute(Attributes.MAX_HEALTH);float previous=entity.getMaxHealth();
                    attribute.removeModifier(TRIGAMMA);
                    attribute.addPermanentModifier(new AttributeModifier(TRIGAMMA,-(entity.getMaxHealth()-previous+2),AttributeModifier.Operation.ADD_VALUE));
                    // X5846's "bloodvomit" auxiliary type has no client handler. Do not invent one.
                    if(entity instanceof ServerPlayer player) {
                        if(entity.getMaxHealth()>2)MedicineVisuals.jolt(player,10000+entity.getRandom().nextInt(10000),10000);
                        else {MedicineVisuals.jolt(player,0,0);MedicineContent.achievement(player,"some_wounds");}
                    }
                }
            }
            default -> throw new IllegalStateException(id);
        }
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag) {
        switch(id) {
            case "serum" -> tooltip.add(Component.translatable("tooltip.hbm.medicine.serum").withStyle(ChatFormatting.GREEN));
            case "injector_5htp" -> tooltip.add(Component.translatable("tooltip.hbm.medicine.injector_5htp").withStyle(ChatFormatting.BLUE));
            case "injector_knife" -> {tooltip.add(Component.translatable("tooltip.hbm.medicine.injector_knife.pain").withStyle(ChatFormatting.RED));tooltip.add(Component.empty());tooltip.add(Component.translatable("tooltip.hbm.medicine.injector_knife.hurts").withStyle(ChatFormatting.RED));}
            default -> { }
        }
        tooltip.add(Component.empty());super.appendHoverText(stack,context,tooltip,flag);
    }
}

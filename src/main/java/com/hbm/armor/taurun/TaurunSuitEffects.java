package com.hbm.armor.taurun;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Original ArmorFSB.handleTick is player-only, and original step has1.003F discriminator. */
public final class TaurunSuitEffects {
    private static final ResourceLocation STEP=TaurunContent.id("taurun_step");
    public static boolean fullSet(Player player){return player.getItemBySlot(EquipmentSlot.HEAD).is(TaurunContent.HELMET.get())&&player.getItemBySlot(EquipmentSlot.CHEST).is(TaurunContent.PLATE.get())&&player.getItemBySlot(EquipmentSlot.LEGS).is(TaurunContent.LEGS.get())&&player.getItemBySlot(EquipmentSlot.FEET).is(TaurunContent.BOOTS.get());}
    static void tick(PlayerTickEvent.Pre event){
        var player=event.getEntity();boolean active=fullSet(player);
        if(active)player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,20,0,true,true));
        var step=player.getAttribute(Attributes.STEP_HEIGHT);if(step==null)return;
        // Keep the existing native attribute/base and other mods' modifiers intact.
        // Transient modifier reproduces source absolute1.003F while worn without
        // rewriting another mod's base. Leave an unchanged modifier alone.
        var previous=step.getModifier(STEP);
        if(active&&previous!=null&&step.getValue()==(double)1.003F)return;
        if(previous!=null)step.removeModifier(STEP);
        if(active){double value=step.getValue();boolean zero=value==0;step.addTransientModifier(new AttributeModifier(STEP,zero?(double)1.003F:(double)1.003F/value-1,zero?AttributeModifier.Operation.ADD_VALUE:AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));}
    }
    private TaurunSuitEffects(){}
}

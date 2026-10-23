package com.hbm.item.treasure;

import com.hbm.armor.ArmorModStorage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.item.ArmorItem;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/** Source armor attribute replacement and HIGHEST-priority death cancellation, sharing real armor NBT. */
final class TreasureArmorRuntime {
    private static final EquipmentSlot[] ORDER={EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD};
    private static final ResourceLocation[] IDS={
        ResourceLocation.parse("hbm:8d6e5c77-133e-4056-9c80-a9e42a1a0b65"),ResourceLocation.parse("hbm:b1b7ee0e-1d14-4400-8037-f7f2e02f21ca"),
        ResourceLocation.parse("hbm:30b50d2a-4858-4e5b-88d4-3e3612224238"),ResourceLocation.parse("hbm:426ee0d0-7587-4697-aaef-4772ab202e78")};
    static void register(){NeoForge.EVENT_BUS.addListener(TreasureArmorRuntime::tick);NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST,TreasureArmorRuntime::death);}
    private static void tick(EntityTickEvent.Pre event){
        if(!(event.getEntity() instanceof LivingEntity living)||living.level().isClientSide)return;
        var attribute=living.getAttribute(Attributes.MAX_HEALTH);if(attribute==null)return;
        for(var slot:ORDER){var armor=living.getItemBySlot(slot);int type=switch(slot){case HEAD->0;case CHEST->1;case LEGS->2;default->3;};float bonus=0;
            if(armor.getItem() instanceof ArmorItem&&ArmorModStorage.hasMods(armor))for(var mod:ArmorModStorage.pryMods(armor,living.level().registryAccess()))if(mod.getItem() instanceof HealthArmorModItem item)bonus+=item.health();
            var existing=attribute.getModifier(IDS[type]);if(existing!=null&&(bonus==0||existing.amount()!=bonus)){attribute.removeModifier(IDS[type]);existing=null;}
            if(bonus!=0&&existing==null)attribute.addTransientModifier(new AttributeModifier(IDS[type],bonus,AttributeModifier.Operation.ADD_VALUE));
        }
    }
    private static void death(LivingDeathEvent event){
        var living=event.getEntity();if(living.level().isClientSide)return;
        for(var slot:ORDER){var armor=living.getItemBySlot(slot);if(!(armor.getItem() instanceof ArmorItem)||!ArmorModStorage.hasMods(armor))continue;var revive=ArmorModStorage.pryMod(armor,ArmorModStorage.EXTRA,living.level().registryAccess());
            if(revive.getItem() instanceof com.hbm.armor.shackles.ItemModShackles&&com.hbm.exposure.HbmExposure.radiation(living)<1000F){
                revive.setDamageValue(revive.getDamageValue()+1);int damage=revive.getDamageValue();
                ArmorModStorage.applyMod(armor,revive,living.registryAccess());
                living.setHealth(living.getMaxHealth());com.hbm.exposure.HbmExposure.incrementRadiation(living,damage*damage);
                event.setCanceled(true);return;
            }
            if(!(revive.getItem() instanceof ReviveArmorModItem))continue;
            revive.setDamageValue(revive.getDamageValue()+1);if(revive.getDamageValue()>=revive.getMaxDamage())ArmorModStorage.removeMod(armor,ArmorModStorage.EXTRA);else ArmorModStorage.applyMod(armor,revive,living.level().registryAccess());
            living.setHealth(living.getMaxHealth());living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,60,99));event.setCanceled(true);return;
        }
    }
    private TreasureArmorRuntime(){}
}

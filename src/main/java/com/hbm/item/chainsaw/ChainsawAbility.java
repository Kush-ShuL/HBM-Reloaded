package com.hbm.item.chainsaw;

import com.hbm.registry.HbmContent;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.death.LegacyDeathEffects;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/** Exact source IWeaponAbility.CHAINSAW handler, called by the sole AvailableAbilities dispatcher. */
public final class ChainsawAbility {
    public static void onHit(LivingEntity victim,int level) {
        if(victim.getHealth()<=0F) {
            int divider=new int[]{15,10}[level];
            int count=Math.min((int)Math.ceil(victim.getMaxHealth()/divider),250);
            for(int i=0;i<count;i++) {
                victim.spawnAtLocation(new ItemStack(HbmContent.item("nitra_small")),1);
                victim.level().addFreshEntity(new ExperienceOrb(victim.level(),victim.getX(),victim.getY(),victim.getZ(),1));
            }
            LegacyDeathEffects.gib(victim);
            victim.level().playSound(null,victim.getX(),victim.getY()+victim.getBbHeight()*.5,victim.getZ(),HbmSounds.get("weapon.chainsaw"),SoundSource.PLAYERS,.5F,1F);
        }
    }
    private ChainsawAbility() { }
}

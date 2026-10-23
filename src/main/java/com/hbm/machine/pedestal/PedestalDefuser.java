package com.hbm.machine.pedestal;

import com.hbm.machine.pedestal.mixin.*;
import com.hbm.registry.HbmContent;
import com.hbm.registry.HbmSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.effect.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/** Actual ItemModDefuser path reused by the pedestal's golden-defuser aura and future real tools. */
public final class PedestalDefuser {
    public static boolean defuse(Creeper creeper,LivingEntity actor,boolean dropItem){
        creeper.setSwellDir(-1);creeper.getEntityData().set(CreeperIgnitionAccess.hbm$ignited(),false);
        if(creeper.level().isClientSide)return false;
        var goals=((MobGoalsAccess)creeper).hbm$goals();SwellGoal swell=null;
        for(var goal:goals.getAvailableGoals())if(goal.getGoal() instanceof SwellGoal actual){swell=actual;break;}
        if(swell==null)return false;goals.removeGoal(swell);
        if(dropItem){
            creeper.level().playSound(null,creeper.getX(),creeper.getY(),creeper.getZ(),HbmSounds.get("item.pinBreak"),SoundSource.HOSTILE,1,1);
            creeper.spawnAtLocation(HbmContent.item("safety_fuse"),1);creeper.hurt(actor!=null?creeper.damageSources().mobAttack(actor):creeper.damageSources().magic(),1F);
            // The source order is duration=0, amplifier=200, not a guessed200tick weakness.
            creeper.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,0,200));
        }
        creeper.getPersistentData().putBoolean("hfr_defused",true);return true;
    }
    public static void register(){NeoForge.EVENT_BUS.addListener((EntityTickEvent.Pre event)->{if(event.getEntity() instanceof Creeper creeper&&creeper.getPersistentData().getBoolean("hfr_defused"))defuse(creeper,null,false);});}
    private PedestalDefuser(){}
}

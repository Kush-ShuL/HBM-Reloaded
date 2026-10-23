package com.hbm.weapon.sedna.gun.pistols;

import com.hbm.damage.LegacyEntityDamage;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.LambdaContext;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.*;

/** Hangman's original timed inspect melee uses its real mouse-over result and original vanilla player damage. */
public final class PistolMelee {
    public static void smack(LambdaContext context,float damage){
        var player=context.getPlayer();if(player==null)return;
        var hit=LegacyEntityDamage.getMouseOver(player,3);
        if(hit instanceof EntityHitResult entityHit){
            var target=entityHit.getEntity();target.hurt(player.damageSources().playerAttack(player),damage);
            var motion=target.getDeltaMovement();target.setDeltaMovement(motion.x*2,motion.y,motion.z*2);
            target.level().playSound(null,target.getX(),target.getY(),target.getZ(),HbmSounds.get("weapon.fire.smack"),target.getSoundSource(),1,.9F+context.entity.getRandom().nextFloat()*.2F);
        }else if(hit instanceof BlockHitResult blockHit){
            var position=blockHit.getBlockPos();var state=player.level().getBlockState(position);var sound=state.getSoundType(player.level(),position,context.entity).getStepSound();var point=hit.getLocation();
            player.level().playSound(null,point.x,point.y,point.z,sound,SoundSource.BLOCKS,2,.9F+context.entity.getRandom().nextFloat()*.2F);
        }
    }
    private PistolMelee(){}
}

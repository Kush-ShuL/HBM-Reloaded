package com.hbm.weapon.sedna.gun.accelerator;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.LambdaContext;
import com.hbm.bomb.nuclear.NuclearPlasmaBlastEffects;
import com.hbm.damage.HbmDamageSources;
import com.hbm.registry.HbmSounds;
import java.util.function.BiConsumer;
import net.minecraft.sounds.*;
import net.minecraft.world.item.ItemStack;
/** Original Tau charge/overload consumption and failure sequence, plus Coilgun reload audio. */
public final class AcceleratorOrchestras {
    public static final BiConsumer<ItemStack,LambdaContext> ORCHESTRA_TAU=(stack,ctx)->{
        var entity=ctx.entity;var type=ItemGunBaseNT.getLastAnim(stack,ctx.configIndex);int timer=ItemGunBaseNT.getAnimTimer(stack,ctx.configIndex);
        if(entity.level().isClientSide){com.hbm.client.weapon.accelerator.TauChargeAudio.update(entity,type,timer);return;}
        if(type==GunAnimation.CYCLE&&timer==0)GunSounds.play(entity,"hbm:weapon.fire.tau",.5F,.9F+entity.getRandom().nextFloat()*.2F);
        if(type==GunAnimation.ALT_CYCLE&&timer==0)GunSounds.play(entity,"hbm:weapon.fire.tau",.5F,.7F+entity.getRandom().nextFloat()*.2F);
        if(type==GunAnimation.SPINUP){
            if(timer%10==0&&timer<130){var magazine=ctx.config.getReceivers(stack)[0].getMagazine(stack);if(magazine.getAmount(stack,ctx.inventory)<=0){ItemGunBaseNT.playAnimation(ctx.getPlayer(),stack,GunAnimation.CYCLE_DRY,ctx.configIndex);return;}magazine.useUpAmmo(stack,ctx.inventory,1);}
            if(timer>200){
                ItemGunBaseNT.playAnimation(ctx.getPlayer(),stack,GunAnimation.CYCLE_DRY,ctx.configIndex);entity.hurt(HbmDamageSources.source(entity.level(),HbmDamageSources.tauBlast),1000F);
                ItemGunBaseNT.setWear(stack,ctx.configIndex,Math.min(ItemGunBaseNT.getWear(stack,ctx.configIndex)+10000F,ctx.config.getDurability(stack)));
                double x=entity.getX(),y=entity.getY()+entity.getEyeHeight(),z=entity.getZ();entity.level().playSound(null,x,y,z,HbmSounds.get("entity.ufoBlast"),SoundSource.NEUTRAL,5F,.9F);entity.level().playSound(null,x,y,z,SoundEvents.FIREWORK_ROCKET_BLAST,SoundSource.NEUTRAL,5F,.5F);
                float yaw=entity.level().random.nextFloat()*180F;for(int i=0;i<3;i++)NuclearPlasmaBlastEffects.send(entity.level(),x,y,z,1F,.8F,.5F,2F,-60F+60F*i,yaw,100);
            }
        }
    };
    public static final BiConsumer<ItemStack,LambdaContext> ORCHESTRA_COILGUN=(stack,ctx)->{
        var entity=ctx.entity;if(entity.level().isClientSide)return;var type=ItemGunBaseNT.getLastAnim(stack,ctx.configIndex);int timer=ItemGunBaseNT.getAnimTimer(stack,ctx.configIndex);
        if(type==GunAnimation.CYCLE&&stack.getItem()==AcceleratorGuns.gun_n_i_4_n_i&&timer==0)GunNetwork.flash(entity);
        if(type==GunAnimation.RELOAD&&timer==0)GunSounds.play(entity,"hbm:weapon.coilgunReload",1F,1F);
    };
    private AcceleratorOrchestras(){}
}

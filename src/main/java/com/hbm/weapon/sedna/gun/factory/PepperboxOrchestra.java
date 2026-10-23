package com.hbm.weapon.sedna.gun.factory;

import com.hbm.weapon.sedna.gun.*;
import java.util.function.BiConsumer;
import net.minecraft.world.item.ItemStack;

/** Original ORCHESTRA_PEPPERBOX server tick points, independently of client frame rate. */
public final class PepperboxOrchestra {
    public static final BiConsumer<ItemStack,ItemGunBaseNT.LambdaContext> PEPPERBOX=(stack,ctx)->{
        var entity=ctx.entity;if(entity.level().isClientSide)return;
        var type=ItemGunBaseNT.getLastAnim(stack,ctx.configIndex);int timer=ItemGunBaseNT.getAnimTimer(stack,ctx.configIndex);
        if(type==GunAnimation.RELOAD){if(timer==24)GunSounds.play(entity,"hbm:weapon.reload.magSmallInsert",1F,1F);if(timer==55)GunSounds.play(entity,"hbm:weapon.reload.revolverSpin",1F,1F);}
        if(type==GunAnimation.CYCLE){if(timer==0)GunNetwork.flash(entity);if(timer==21)GunSounds.play(entity,"hbm:weapon.reload.revolverCock",1F,.6F);}
        if(type==GunAnimation.CYCLE_DRY){if(timer==2)GunSounds.play(entity,"hbm:weapon.reload.dryFireClick",1F,.8F);if(timer==11)GunSounds.play(entity,"hbm:weapon.reload.revolverCock",1F,.6F);}
        if(type==GunAnimation.INSPECT&&timer==3)GunSounds.play(entity,"hbm:weapon.reload.revolverSpin",1F,1F);
        if(type==GunAnimation.JAMMED){if(timer==28)GunSounds.play(entity,"hbm:weapon.reload.dryFireClick",1F,.75F);if(timer==45)GunSounds.play(entity,"hbm:weapon.reload.dryFireClick",1F,.6F);}
    };
    private PepperboxOrchestra(){}
}

package com.hbm.weapon.sedna.gun.flamer;

import com.hbm.weapon.sedna.gun.GunAnimation;
import com.hbm.weapon.sedna.gun.GunSounds;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.LambdaContext;
import java.util.function.BiConsumer;
import net.minecraft.world.item.ItemStack;

/** Original normal/Topaz client loop dispatch and actual server reload sounds; Daybreaker has no loop. */
public final class FlamerOrchestras {
    public static final BiConsumer<ItemStack,LambdaContext> ORCHESTRA_FLAMER=(stack,context)->{if(context.entity.level().isClientSide){FlamerContent.CLIENT_ORCHESTRA.accept(stack,context);return;}reload(stack,context);};
    public static final BiConsumer<ItemStack,LambdaContext> ORCHESTRA_FLAMER_DAYBREAKER=(stack,context)->{if(context.entity.level().isClientSide)return;reload(stack,context);};
    private static void reload(ItemStack stack,LambdaContext context){if(ItemGunBaseNT.getLastAnim(stack,context.configIndex)!=GunAnimation.RELOAD)return;int timer=ItemGunBaseNT.getAnimTimer(stack,context.configIndex);
        if(timer==15)GunSounds.play(context.entity,"hbm:weapon.reload.openLatch",1,1);
        if(timer==35)GunSounds.play(context.entity,"hbm:weapon.reload.impact",.5F,1);
        if(timer==60)GunSounds.play(context.entity,"hbm:weapon.reload.revolverClose",1,.75F);
        if(timer==70)GunSounds.play(context.entity,"hbm:weapon.reload.insertCanister",1,1);
        if(timer==85)GunSounds.play(context.entity,"hbm:weapon.reload.pressureValve",1,1);
    }
    private FlamerOrchestras(){}
}

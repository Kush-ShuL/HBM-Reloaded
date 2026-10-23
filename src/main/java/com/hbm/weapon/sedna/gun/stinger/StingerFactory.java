package com.hbm.weapon.sedna.gun.stinger;
import java.util.function.BiConsumer;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.*;
import com.hbm.weapon.sedna.gun.mags.MagazineSingleReload;
import com.hbm.weapon.sedna.gun.pistols.PistolAmmo;
import com.hbm.weapon.sedna.gun.panzerschreck.*;
import net.minecraft.world.item.ItemStack;

/** The source Stinger uses the same rocket_rpzb array and animation as Panzerschreck. */
public final class StingerFactory {
    /** Physical-client bridge installed by this family's real client setup; dedicated-server branch never calls it. */
    public static BiConsumer<ItemStack,LambdaContext> CLIENT_SOUND=(stack,context)->{};
    private StingerFactory(){}
    public static ItemGunStinger create(){
        var gun=new ItemGunStinger(WeaponQuality.SPECIAL,new GunConfig().dura(300).draw(7).inspect(40).crosshair(Crosshair.L_BOX_OUTLINE)
            .rec(new Receiver(0).dmg(StingerSettings.DAMAGE).delay(5).reload(50).jam(40).sound("hbm:weapon.rpgShoot",1F,1F)
                .mag(new MagazineSingleReload(0,1).addConfigs(PanzerschreckBullets.ROCKETS)).offset(1,-.0625*1.5,-.1875D).setupLockonFire().recoil((stack,ctx)->{}))
            .setupStandardConfiguration().ps((stack,ctx)->ItemGunStinger.setIsLockingOn(stack,true)).rs((stack,ctx)->ItemGunStinger.setIsLockingOn(stack,false))
            .anim(PanzerschreckFactory.LAMBDA_PANZERSCHRECK_ANIMS).orchestra(StingerFactory::orchestra));
        gun.setDefaultAmmo(PistolAmmo.ROCKET_HEAT,3);return gun;
    }
    private static void orchestra(ItemStack stack,LambdaContext ctx){
        if(ctx.entity.level().isClientSide){CLIENT_SOUND.accept(stack,ctx);return;}
        var type=ItemGunBaseNT.getLastAnim(stack,ctx.configIndex);int timer=ItemGunBaseNT.getAnimTimer(stack,ctx.configIndex);
        if(type==GunAnimation.CYCLE&&timer==0)GunNetwork.flash(ctx.entity);
        if(type==GunAnimation.RELOAD&&timer==30)GunSounds.play(ctx.entity,"hbm:weapon.reload.insertCanister",1F,1F);
    }
}

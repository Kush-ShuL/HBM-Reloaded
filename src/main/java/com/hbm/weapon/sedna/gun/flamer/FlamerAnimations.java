package com.hbm.weapon.sedna.gun.flamer;

import com.hbm.client.animation.bus.BusAnimation;
import com.hbm.client.animation.bus.BusAnimationSequence;
import com.hbm.client.animation.bus.BusAnimationKeyframe.IType;
import com.hbm.weapon.sedna.gun.GunAnimation;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.world.item.ItemStack;

/** The common source bus definitions; client owns only actual original JSON reload publication. */
public final class FlamerAnimations {
    public static volatile Supplier<BusAnimation> RELOAD=()->null;
    public static final BiFunction<ItemStack,GunAnimation,BusAnimation> LAMBDA_FLAMER_ANIMS=(stack,type)->switch(type){
        case EQUIP->new BusAnimation().addBus("EQUIP",new BusAnimationSequence().addPos(-45,0,0,0).addPos(0,0,0,500,IType.SIN_DOWN));
        case RELOAD->RELOAD.get();
        case INSPECT,JAMMED->new BusAnimation().addBus("ROTATE",new BusAnimationSequence().addPos(0,0,45,250,IType.SIN_FULL).addPos(0,0,45,350).addPos(0,0,-15,150,IType.SIN_FULL).addPos(0,0,0,100,IType.SIN_FULL));
        default->null;
    };
    private FlamerAnimations(){}
}

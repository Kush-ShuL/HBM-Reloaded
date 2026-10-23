package com.hbm.client.chainsaw;

import com.hbm.client.animation.bus.*;
import com.hbm.item.chainsaw.ChainsawItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;

/** Mechanical extraction of ItemChainsaw.getAnimation's original three-phase swing and strict50ms overlap gate. */
final class ChainsawAnimations {
    static void receive() {
        var player=Minecraft.getInstance().player;if(player==null)return;
        var stack=player.getMainHandItem();if(!(stack.getItem() instanceof ChainsawItem))return;
        try(var hand=HbmAnimations.hand(InteractionHand.MAIN_HAND)) {
            var animation=animation();if(animation!=null)HbmAnimations.hotbar[player.getInventory().selected][0]=new HbmAnimations.Animation(stack.getItem().getDescriptionId(),System.currentTimeMillis(),animation);
        }
    }
    private static BusAnimation animation() {
        int forward=150,sideways=100,retire=200;
        if(HbmAnimations.getRelevantAnim()==null)return new BusAnimation()
                .addBus("SWING_ROT",new BusAnimationSequence().addPos(0,0,90,forward).addPos(45,0,90,sideways).addPos(0,0,0,retire))
                .addBus("SWING_TRANS",new BusAnimationSequence().addPos(0,0,3,forward).addPos(2,0,2,sideways).addPos(0,0,0,retire));
        var rotation=HbmAnimations.getRelevantTransformation("SWING_ROT");var translation=HbmAnimations.getRelevantTransformation("SWING_TRANS");
        if(System.currentTimeMillis()-HbmAnimations.getRelevantAnim().startMillis<50)return null;
        return new BusAnimation()
                .addBus("SWING_ROT",new BusAnimationSequence().addPos(rotation[0],rotation[1],rotation[2],0).addPos(0,0,90,forward).addPos(45,0,90,sideways).addPos(0,0,0,retire))
                .addBus("SWING_TRANS",new BusAnimationSequence().addPos(translation[0],translation[1],translation[2],0).addPos(0,0,3,forward).addPos(2,0,2,sideways).addPos(0,0,0,retire));
    }
    private ChainsawAnimations() { }
}

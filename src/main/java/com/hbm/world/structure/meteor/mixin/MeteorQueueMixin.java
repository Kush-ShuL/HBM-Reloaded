package com.hbm.world.structure.meteor.mixin;

import com.hbm.world.structure.meteor.MeteorJigsawLimits;
import java.util.Deque;
import net.minecraft.util.SequencedPriorityIterator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Retain the vanilla priority queues; source pieces draw a random tied entry instead of FIFO. */
@Mixin(value=SequencedPriorityIterator.class,remap=false)
public abstract class MeteorQueueMixin {
    @Shadow private Deque<Object> highestPrioQueue;
    @Shadow private void switchCacheToNextHighestPrioQueue(){throw new AssertionError();}
    @Inject(method="computeNext",at=@At("HEAD"),cancellable=true)
    private void hbm$randomSourcePriorityTie(CallbackInfoReturnable<Object> cir){
        var random=MeteorJigsawLimits.queueRandom(this);
        if(random==null||highestPrioQueue==null)return;
        int selected=random.nextInt(highestPrioQueue.size());
        var iterator=highestPrioQueue.iterator();Object result=null;
        for(int index=0;index<=selected;index++)result=iterator.next();
        iterator.remove();
        if(highestPrioQueue.isEmpty())switchCacheToNextHighestPrioQueue();
        cir.setReturnValue(result);
    }
}
